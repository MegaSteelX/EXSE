package com.megasteelx.exse.activities;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.megasteelx.exse.*;
import com.megasteelx.exse.other.*;
import com.megasteelx.exse.utils.*;
import com.megasteelx.exse.widget.*;
import java.io.*;
import java.lang.reflect.*;
import com.megasteelx.exse.items.*;
import java.util.*;
import android.widget.AdapterView.*;
import android.support.v4.widget.*;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;



public class CardEditActivity extends Activity
{
	MarkRecorder cardMarks=new MarkRecorder();
	Map<String,Integer> idMap=new HashMap<String,Integer>();
	public int getViewId(String s){
		return idMap.get(s);
	}
	public void onReturnData(String itemName,String data,int groupId){
		//check cardmarks.
		//确认返回的item Toast.makeText(this,itemName+data,Toast.LENGTH_SHORT).show();
		if(groupId==-1){
			//is not ItemGroup child, do normal check
			cardData.setItemData(itemName,data);
			card.linkData(cardData);
			//Toast.makeText(this,cardData.toString(),Toast.LENGTH_SHORT).show();
			cardMarks.checkMarks(cardData,SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/marks/marks.dfn");
			if(cardMarks.isChanged()){
				cardSet.setCard(cardSetDex,cardData.toString());
				refreshCardView();
			}
		}else{
			//is ItemGroup child
			ItemsGroup ig=(ItemsGroup)(findViewById(groupId));
			ig.dealDataUpdate(this,data);
			cardData.setItemData(itemName,ig.getData());
			card.linkData(cardData);
		}
	}

    public Card getCard() {
        return card;
    }

    public View.OnClickListener barListClickListener=new View.OnClickListener(){
		@Override
		public void onClick(View p1)
		{
			final int position=p1.getTag(R.id.tag_position);
			switch(p1.getId()){
			case R.id.list_copy:
				cardSet.copyCard(position);
				barAdapter.notifyDataSetChanged();
				break;
			case R.id.list_del:
				AlertDialog.Builder deleteCardDialog=new AlertDialog.Builder(CardEditActivity.this);
					deleteCardDialog.setTitle(R.string.delete_card_alert).setPositiveButton(R.string.positive, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								cardSet.delCard(position);
								barAdapter.notifyDataSetChanged();
							}
						}).setNegativeButton(R.string.negative,null)
					.show();
				break;
			case R.id.list_item:
				
				cardParentView.closeDrawers();
				cardSetDex=position;
				card=new Card();
				//cardSet.setCard(cardSetDex,cardData.toString());
				refreshCardView();
				break;
			default:
			}
			//监测position是否正确：(checked)Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
			//监测cardSet改动：(checked)Toast.makeText(getApplicationContext(),cardSet.savingCardSet(""),Toast.LENGTH_LONG).show();
		}
	};
	
	int currentImgViewId;
	String clickeeIdQueue="";
	
	public void recordPicClickee(int viewId){
		clickeeIdQueue=clickeeIdQueue+"\n"+viewId;
	}
	public void startImgLoad(int viewId){
		currentImgViewId=viewId;
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			"image/*");
		startActivityForResult(intent, 1);
		//todo:start photo zoom
	}

	Uri imageUri = Uri.parse("file://"+SettingUtils.PATH_WORKSPACE+"/image");

    private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	private void cropImageUri(Uri uri){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, 3);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	try{
			switch (requestCode) {
				case 1:
					cropImageUri(data.getData());
					break;
				case 3:
					if(imageUri != null){
						String filepath=SettingUtils.PATH_WORKSPACE+"/image";
						Bitmap cimage = decodeUriAsBitmap(imageUri);
						//cimage = Bitmap.createScaledBitmap(cimage, 600, 600, true);
						PictureLoader currentImage= (PictureLoader) findViewById(currentImgViewId);
						currentImage.setImageBitmap(cimage);
						File file=new File(filepath);//将要保存图片的路径 
						try { 
							BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)); 
							cimage.compress(Bitmap.CompressFormat.JPEG, 100, bos); 
							bos.flush(); 
							bos.close(); 
							String hush=FileUtils.FileToHush(file);
							FileUtils.ForcedCopyFile(filepath,SettingUtils.PATH_WORKSPACE+"/"+hush,true);
							currentImage.returnData(this,hush);
						} catch (IOException e) { 
							e.printStackTrace(); 
						} 
					}
					break;
				case SAVEDATA_SELECT_REQUEST:
					List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
					if(list.size()>1){
						LogUtils.e("open savedata_chosed more than 1 files");
					}else if(list.size()<1){
						LogUtils.e("open savedata_chosed no files");
					}else{
						FileUtils.ClearDir(SettingUtils.PATH_WORKSPACE);
						openSetTask openTask =new openSetTask(list.get(0),this);
						String path=list.get(0);
						String name=new File(path).getName().replace(SettingUtils.SUFFIX_SAVEDATA,"");
						drawerTitle.setText(name);
						openTask.setLoadDataComplete(new openSetTask.isLoadDataListener(){

							@Override
							public void loadComplete()
							{
								/*
								cardSetDex=0;
								cardSet.createCardSet(SettingUtils.PATH_WORKSPACE+"/set");
								refreshCardView();
								*/
								Intent intent=new Intent(CardEditActivity.this,CardEditActivity.class);
								startActivity(intent);
								finish();
							}
						});
						openTask.execute();
						
					}
					break;
				default:
					break;
			}
		}catch(Exception e){
			LogUtils.e("insert img@"+currentImgViewId+findViewById(currentImgViewId)+e);
		}
    	super.onActivityResult(requestCode, resultCode, data);
	}
	
	int cardSetDex=0;
	Card card=new Card();
	CardSet cardSet;
	CardData cardData;
	ListView barList;
	ImageView drawerImage;
	Spinner drawerGene;
	TextView drawerTitle;
	CardListAdapter barAdapter;
	DrawerLayout cardParentView;
	
	final int SAVEDATA_SELECT_REQUEST=0xdead;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.card_edit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.save_img:
				View img_view= findViewById(R.id.cardview);
				img_view.setDrawingCacheEnabled(true);
				img_view.destroyDrawingCache();
				img_view.buildDrawingCache();
				Bitmap img=OtherUtils.convertViewToBitmap(img_view);//img_view.getDrawingCache();
				String path=SettingUtils.PATH_PICTURE+File.separator;
				if(card.getItemFromName("name")!=null)
					path+=card.getItemFromName("name").getData();
				else
					path+="UnNamed";
				//TODO png format
				//TODO delete mode
				path=StringUtils.fixName(path,".jpg");
				
				Toast.makeText(this,FileUtils.saveFile(img,path)?"存储成功":"存储失败，请重试",Toast.LENGTH_SHORT).show();
				Uri data = Uri.parse(path);
				img_view.setDrawingCacheEnabled(false);
				sendBroadcast(new  Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data)); 
				
				//OtherUtils.updateMediaLibraryInsert(this,path);
			break;
			default:
			LogUtils.e("clicked undefined button");
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_card_edit);
		Toolbar tb=findViewById(R.id.cv_toolbar);
		tb.setBackgroundColor(0xFF000000);
		setActionBar(tb);
		//set card list
		cardSet=new CardSet(SettingUtils.PATH_WORKSPACE+"/set");
		barList=findViewById(R.id.cv_list);
		refreshCardView();
		barAdapter=new CardListAdapter(this,cardSet.getCards());
		barList.setAdapter(barAdapter);
		//set Drawer
		drawerImage=findViewById(R.id.cv_img);
		OtherUtils.SetImageToView(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/sample.jpg",drawerImage);
		drawerGene=findViewById(R.id.cv_gene);
		String[] geneList=new File(SettingUtils.PATH_SOURCE).list();
		final ArrayList<String> genesList=new ArrayList<String>();
		for(int i=0;i<geneList.length;i++){
			genesList.add(geneList[i]);
		}
		SpinnerAdapter geneAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,genesList);
		drawerGene.setAdapter(geneAdapter);
		int geneChose=genesList.indexOf(SettingUtils.CARD_SET_STYLE);
		if(geneChose<0){
			LogUtils.e("certain generation not exists_"+SettingUtils.CARD_SET_STYLE);
			geneChose=0;
		}
		drawerGene.setSelection(geneChose);
		drawerGene.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{
					if(!SettingUtils.CARD_SET_STYLE.equals(genesList.get(p3))){
						saveSetFile(false);
						FileUtils.ClearDir(SettingUtils.PATH_WORKSPACE);
						SettingUtils.setCurrentStyle(CardEditActivity.this,genesList.get(p3));
						CardSetUtils.PrepareCardSet(SettingUtils.PATH_WORKSPACE+"/set",SettingUtils.SETFILE_HEAD,genesList.get(p3));
						Intent intent=new Intent(CardEditActivity.this,CardEditActivity.class);
						startActivity(intent);
						finish();
						//refreshCardView();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{//do nothing
				}
			});
		drawerTitle=findViewById(R.id.cv_setname);
		String titleRandom=(int)(char)System.currentTimeMillis()+"";
		drawerTitle.setText(titleRandom);
		drawerTitle.setOnFocusChangeListener(new OnFocusChangeListener(){

				@Override
				public void onFocusChange(View p1, boolean p2)
				{
					if(drawerTitle.getText().toString().isEmpty()){
						String titleRandom=(int)(char)System.currentTimeMillis()+"";
						drawerTitle.setText(titleRandom);
					}
				}
			});
		//处理侧边栏按钮组的点击事件
		Button.OnClickListener barButtonGroupClickListener= new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					switch(p1.getId()){
					case R.id.cv_addc:
						cardSet.addCard(FileUtils.FileToString(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/new_card.dfn").replace("card:","").trim());
						barAdapter.notifyDataSetChanged();
						break;
					case R.id.cv_open:
							new AlertDialog.Builder(CardEditActivity.this).setTitle(R.string.open_savedata_alert).setPositiveButton(R.string.positive, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									new LFilePicker()
											.withActivity(CardEditActivity.this)
											.withStartPath(SettingUtils.PATH_SAVEDATA)
											.withRequestCode(SAVEDATA_SELECT_REQUEST)
											.withMutilyMode(false)
											.withTitle("选择存档文件")
											.withFileFilter(new String[]{SettingUtils.SUFFIX_SAVEDATA})
											.start();

										}
							}).setNegativeButton(R.string.negative, null
							).show();
						break;
					case R.id.cv_clear:
						new AlertDialog.Builder(CardEditActivity.this).setTitle(R.string.clear_workspace_alert).setPositiveButton(R.string.positive, new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface p1, int p2)
									{
										FileUtils.ClearDir(SettingUtils.PATH_WORKSPACE);
										CardSetUtils.PrepareCardSet(SettingUtils.PATH_WORKSPACE+"/set",SettingUtils.SETFILE_HEAD,SettingUtils.CARD_SET_STYLE);
										cardSet.createCardSet(SettingUtils.PATH_WORKSPACE+"/set");
										refreshCardView();
									}
								}).setNegativeButton(R.string.negative, null
							).show();
						break;
					case R.id.cv_save:
						saveSetFile(false);
						break;
					case R.id.cv_setting:
						
						break;
					default:
						LogUtils.e("clicked a undefined button");
					}
				}
			};
		findViewById(R.id.cv_addc).setOnClickListener(barButtonGroupClickListener);
		findViewById(R.id.cv_clear).setOnClickListener(barButtonGroupClickListener);
		findViewById(R.id.cv_open).setOnClickListener(barButtonGroupClickListener);
		findViewById(R.id.cv_save).setOnClickListener(barButtonGroupClickListener);
		findViewById(R.id.cv_setting).setOnClickListener(barButtonGroupClickListener);
		//finished dealing.
		//处理侧边栏打开/关闭动作：打开时储存当前卡片并刷新list
		cardParentView=findViewById(R.id.cardparentview);
		cardParentView.addDrawerListener(new DrawerLayout.DrawerListener(){

				@Override
				public void onDrawerSlide(View p1, float p2)
				{
					// TO-DO: Implement this method
				}

				@Override
				public void onDrawerOpened(View p1)
				{
				switch(p1.getId()){
				case R.id.cv_Rbanner:
					int geneChose=genesList.indexOf(SettingUtils.CARD_SET_STYLE);
					if(geneChose<0){
						LogUtils.e("dwr:certain generation not exists_"+SettingUtils.CARD_SET_STYLE);
						geneChose=0;
					}
					drawerGene.setSelection(geneChose);
					
					LogUtils.e(storeCurrentCard());
					barAdapter.notifyDataSetChanged();
				break;
				case R.id.cv_Lbanner:
					LogUtils.displayWholeLog(CardEditActivity.this,(LinearLayout)findViewById(R.id.cv_Lbannerll));
				}
				}

				@Override
				public void onDrawerClosed(View p1)
				{
					// TO-DO: Implement this method
				}

				@Override
				public void onDrawerStateChanged(int p1)
				{
					// TO-DO: Implement this method
				}
			});
	}
	private void refreshCardView(){
		//
		String cardStyle=cardSet.getStyle();
		SettingUtils.CARD_SET_STYLE=cardStyle;
		File styleDir= new File(SettingUtils.PATH_SOURCE+"/"+cardStyle);
		if(!styleDir.exists()){
			LogUtils.e(cardStyle+"_no such style source");
			styleDir=new File(SettingUtils.PATH_SOURCE).listFiles()[0];
		}
		card.readCardItems(styleDir.getPath()+"/items.dfn");
		card.readCardStyle(card.getStyleFilePath(styleDir.getPath()));
		cardData=new CardData(cardSet.getCard(cardSetDex));
		//Check position and cards got:(checked)Toast.makeText(this,cardSetDex+""+cardData,Toast.LENGTH_SHORT).show();
		
		card.linkData(cardData);
		cardMarks.checkMarks(cardData,SettingUtils.PATH_SOURCE+"/"+cardStyle+"/marks/marks.dfn");
		for(int i=0;i<cardMarks.getMarks().size();i++){
			card.readCardStyle(styleDir.getPath()+"/marks/"+cardMarks.getMarks().get(i).trim()+".mdl");
		}
		
		double baseSize;
		//权且直接基宽，等后续完善(finished)
		//baseSize=(double)canvasSize()[CANVAS_WIDTH]/1000.0;
		
		ItemCore baseItem = card.getItemFromName("cardface");
		
		if((double)baseItem.getWidth()/(double)baseItem.getHeight()
		>(double)canvasSize()[CANVAS_WIDTH]/(double)canvasSize()[CANVAS_HEIGHT]){
			//屏幕比卡片宽，正常取高
			baseSize=(double)canvasSize()[CANVAS_WIDTH]/(double)baseItem.getWidth();
		}else{
			//Screen's longer than card.Get size from width.
			baseSize=(double)canvasSize()[CANVAS_HEIGHT]/(double)baseItem.getHeight();
		}
		SettingUtils.BASE_SIZE=baseSize;
		//
		int id;boolean isRedraw=false;
		//
		((ViewGroup)findViewById(R.id.cardview)).removeAllViews();
		idMap.clear();
		//
		for(int i=0;i<card.mCores.size();i++){
			String coreName=card.mCores.get(i).getName();
			if(idMap.containsKey(coreName)){
				id=idMap.get(coreName);
				isRedraw=true;
			}else{
				id=View.generateViewId();
				idMap.put(coreName,id);
				isRedraw=false;
				
			}
			card.mCores.get(i).drawView((AbsoluteLayout)findViewById(R.id.cardview)
	,this,baseSize,id,isRedraw);
		}
		if(!clickeeIdQueue.isEmpty()){
			//处理pictureloader的被点击者view
			String[] clickeeIds=clickeeIdQueue.trim().split("\n");
			for(int i=0;i<clickeeIds.length;i++){
				View clickEe=findViewById(Integer.parseInt(clickeeIds[i]));
				if(clickEe!=null){
					clickEe.bringToFront();
				}else{
					LogUtils.e("null clickee with id_"+clickeeIds[i]);
				}
			}
		}
		
		try{
			barAdapter.notifyDataSetChanged();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private String storeCurrentCard(){
		cardData.getDataFormCard(card);
		String cardDataStr=cardData.toString();
		cardSet.setCard(cardSetDex,cardDataStr);
		return cardDataStr;
	}
	private int[] canvasSize() {
        Display canvas = getWindowManager().getDefaultDisplay();
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch(Exception e1) {
            e1.printStackTrace();
        }
        TypedArray actionbarSizeTypedArray = obtainStyledAttributes(new int[1]);
        float h = actionbarSizeTypedArray.getDimension(0, 0);
       	int hei = (int)(((float)canvas.getHeight() - h) - (float)sbar);
        int wid = canvas.getWidth();
        return new int[]{hei,wid};
    }
	private void saveSetFile(boolean exitWhileSaved){
		Toast.makeText(this,"正在开始存档。",Toast.LENGTH_SHORT).show();
		String filename=drawerTitle.getText().toString();
		//String filepath=FileUtils.fixName(SettingUtils.PATH_SAVEDATA + "/" + (filename.isEmpty() ?"Untitled": filename),SettingUtils.SUFFIX_SAVEDATA);pp
		CardSetUtils.saveCardSet(this,cardSet,SettingUtils.PATH_SAVEDATA,filename,exitWhileSaved);
	}
	private int CANVAS_HEIGHT=0;
	private int CANVAS_WIDTH=1;
}
