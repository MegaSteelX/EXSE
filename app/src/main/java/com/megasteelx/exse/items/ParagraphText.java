package com.megasteelx.exse.items;
import android.content.*;
import android.widget.*;
import android.graphics.*;
import com.megasteelx.exse.activities.*;
import com.megasteelx.exse.utils.*;
import android.util.*;
import android.text.*;
import java.io.*;
import android.view.*;
import android.widget.ImageView.*;
import android.widget.RadioGroup.*;

public class ParagraphText extends EditText implements ItemInterface
{
	public int groupId = -1;//if is ItemGroup child then ItemGroup Id, else NULL.

	ItemCore mCore;
	int textSize=10,iconSize=10;
	//,icon1Size=10,icon2Size=10,icon1Width=20;
	Typeface mTypeface=null;
	String typeName="";
	int textColor=0xFFFFFF;
	float lineSpace=1.0f;
	boolean vertical=false;
	boolean withImgSpan=false,haveImgSpan=false;
	//with:是否显示,have:是否存在
	int imgChooserId;
	//文字超出范围的修正方式
	private enum textOOZmode{noFix,fixTextSize,fixViewTop,fixViewBottom,fixViewLeft,fixViewRight};
	textOOZmode mOOZmode=textOOZmode.fixTextSize;
	
	public ParagraphText(Context context,ItemCore core,int id){
		super(context);
		setBackground(null);
		setPadding(0,0,0,0);
		setPivotX(0);
		setPivotY(0);
		mCore=core;
		setId(id);
		setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		setSingleLine(false);
		setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		//获取字体
		File fontdir=new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/fonts");
		String namedfn="names.dfn";
		String[]namespair=FilesUtils.FileToLines(fontdir.getPath()+"/"+namedfn);//.trim().split("\n");
		boolean nameReadFlag=false;
		for(int i=0;i<namespair.length;i++){
			if(namespair[i].startsWith(mCore.getName())){
				namedfn=namespair[i].replace(mCore.getName()+"=","");
				nameReadFlag=true;
				break;
			}
		}
		if(nameReadFlag){
			typeName=namedfn;
			mTypeface=Typeface.createFromFile(fontdir.getPath()+"/"+namedfn);
		}
		dealExtStyle();
	}
	private void dealExtStyle(){
		//读取extstyle
		if(!(mCore.getExtStyle()==null||mCore.getExtStyle().isEmpty())){
			String tempExtStl=new String(mCore.getExtStyle());
			String[]extStls=tempExtStl.split(";");
			for(int i=0;i<extStls.length;i++){
				//注意此时tES代表的变量不同了
				tempExtStl=extStls[i];
				String[] extStlKVP=tempExtStl.split(":");
				if(extStlKVP.length!=2){
					LogUtils.e(tempExtStl+"_not expected K-V pair");
				}else{
					//此处处理extstl键值对（伪）
					if(extStlKVP[0].trim().equals("groupId")){
						groupId=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("textsize")){
						textSize=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("color")){
						textColor=Integer.parseInt(extStlKVP[1].trim().replace("#",""),16);
					}
					if(extStlKVP[0].trim().equals("gravity")){
						String temp=extStlKVP[1];
						setGravity((temp.contains("R")?Gravity.RIGHT:temp.contains("L")?Gravity.LEFT:Gravity.CENTER)|(temp.contains("T")?Gravity.TOP:temp.contains("B")?Gravity.BOTTOM:Gravity.CENTER));
					}
					if(extStlKVP[0].trim().equals("withicons")){
						String temp=extStlKVP[1];
						withImgSpan=Boolean.parseBoolean(temp.trim());
					}
					if(extStlKVP[0].trim().equals("iconsize")){
						String temp=extStlKVP[1];
						iconSize=Integer.parseInt(temp.trim());
					}
					if(extStlKVP[0].trim().equals("linespace")){
						lineSpace=Float.parseFloat(extStlKVP[1].trim());
						setLineSpacing(0,lineSpace);
					}
					if(extStlKVP[0].trim().equals("outofzone")){
						mOOZmode=textOOZmode.valueOf(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("vertical")){
						vertical=Boolean.parseBoolean(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("textstyle")){
						TextPaint tp=getPaint();
						if(extStlKVP[1].contains("N")){
							tp.setFlags(0);
							tp.setTextSkewX(0);
						}
						if(extStlKVP[1].contains("I"))tp.setTextSkewX(-0.25f);
						if(extStlKVP[1].contains("B"))tp.setFlags(tp.getFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
						if(extStlKVP[1].contains("U"))tp.setFlags(tp.getFlags()|Paint.UNDERLINE_TEXT_FLAG);
						if(extStlKVP[1].contains("S"))tp.setFlags(tp.getFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
					}
					//TODO auto-Traditional-Chinese
					//TODO auto-full/half char
					//done.
				}
			}
		}//extstl处理完毕。
	}
	private void changeSpan(double baseSize){
		OtherUtils.InsertIconToText(this,baseSize,SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+mCore.getName(),iconSize,iconSize,iconSize);
	}
	@Override
	public void addToParent(final AbsoluteLayout parent, final Context context, final double baseSize)
	{
		LogUtils.i("drawing ST@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));
		if(vertical){
			setRotation(90);
			if(!typeName.isEmpty()){
				File aiteTypeFile=new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/fonts/@"+typeName);
				if(aiteTypeFile.exists()){
					mTypeface=Typeface.createFromFile(aiteTypeFile);
					setTypeface(mTypeface);
				}else{
					LogUtils.e(typeName+"no @font for vertical text exists");
				}
			}
		}else{
			setRotation(0);
			if(!typeName.isEmpty()){
				File TypeFile=new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/fonts/"+typeName);
				mTypeface=Typeface.createFromFile(TypeFile);
				setTypeface(mTypeface);
			}		
		}
		setText(mCore.getData());
		if(mTypeface!=null){
			setTypeface(mTypeface);
		}else{
			LogUtils.w(mCore.getName()+"_using system typeface");
		}
		setTextColor(0xFF000000+textColor);
		//处理图文混排
		String imgPath=SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+mCore.getName();
		if(new File(imgPath).isDirectory()){
			haveImgSpan=true;
			imgChooserId=View.generateViewId();
			HorizontalScrollView imgChooserParent=new HorizontalScrollView(context);
			imgChooserParent.setId(imgChooserId);
			if(groupId==-1){
				parent.addView(imgChooserParent,new AbsoluteLayout.LayoutParams(
								   (int)(baseSize*mCore.width),
								   100,//(int)(baseSize*mCore.height),
								   (int)(baseSize*mCore.left),
								   (int)(baseSize*(mCore.top)-100)//mCore.height))
							   ));
			}else{
				ImageView group=parent.findViewById(groupId);
				if(group!=null){
					AbsoluteLayout.LayoutParams pam=(AbsoluteLayout.LayoutParams) group.getLayoutParams();
					parent.addView(imgChooserParent,new AbsoluteLayout.LayoutParams(
									   (int)(baseSize*mCore.width),
									   100,//(int)(baseSize*mCore.height),
									   (int)(baseSize*mCore.left),
									   pam.y-100//mCore.height))
								   ));
				}else{
					LogUtils.e("group error");
					parent.addView(imgChooserParent,new AbsoluteLayout.LayoutParams(
									   (int)(baseSize*mCore.width),
									   100,//(int)(baseSize*mCore.height),
									   (int)(baseSize*mCore.left),
									   (int)(baseSize*(mCore.top)-100)//mCore.height))
								   ));
				}
			}
			imgChooserParent.setBackgroundColor(0xFFFFFFFF);
			LinearLayout imgChooser=new LinearLayout(context);
			imgChooserParent.addView(imgChooser);
			imgChooser.setOrientation(LinearLayout.HORIZONTAL);
			String[]ImgNames=new File(imgPath).list(new FilenameFilter(){
					@Override
					public boolean accept(File p1, String p2)
					{
						return p2.endsWith(".png")&&p2.length()==5;
					}
				});
			ImageView.OnClickListener imgClickListener=new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					String span="<"+p1.getTag()+">";
					int index = getSelectionStart();
					if(getText().toString().length()>index&&
					 getText().toString().charAt(index)=='>')index++;
					Editable editable = getText();  
					editable.insert(index, span);
					changeSpan(baseSize);
					setSelection(index+3);
				}
			};
			for(int i=0;i<ImgNames.length;i++){
				ImageView oneImg=new ImageView(context);
				oneImg.setImageBitmap(BitmapFactory.decodeFile(imgPath+"/"+ImgNames[i]));
				oneImg.setScaleType(ScaleType.FIT_CENTER);
				oneImg.setTag(ImgNames[i].charAt(0)+"");
				oneImg.setOnClickListener(imgClickListener);
				imgChooser.addView(oneImg);
				ViewGroup.LayoutParams imgparams=oneImg.getLayoutParams();
				imgparams.height=(baseSize*mCore.height<100?(int)(baseSize*mCore.height):100);
				imgparams.width=(int)(imgparams.height*1.125);
				oneImg.setLayoutParams(imgparams);
			}
			imgChooserParent.setVisibility(View.GONE);
		}else if(withImgSpan){
			LogUtils.w(mCore.getName()+"_no icon(s) found to add");
		}
		//finished.
		fixWandH(baseSize);
		if(withImgSpan){
			changeSpan(baseSize);
		}
		
		addTextChangedListener(new TextWatcher(){

				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TO-DO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					fixWandH(baseSize);
					/*if(withImgSpan){
						changeSpan(baseSize);
					}*/
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					//returnData(context,getText().toString().isEmpty()?"NULL":getText().toString());
				}
			});
		setOnFocusChangeListener(new OnFocusChangeListener(){

				@Override
				public void onFocusChange(View p1, boolean p2)
				{
					if(haveImgSpan){
						View v=parent.findViewById(imgChooserId);
						if(p2&&withImgSpan){
							v.setVisibility(View.VISIBLE);
							v.bringToFront();
						}else{
						v.setVisibility(View.GONE);
						}
					}
					if(!p2)
						returnData(context,getText().toString());
				}
			});
	}

	@Override
	public void returnData(Context context, String data)
	{
		mCore.data=data;
		((CardEditActivity)context).onReturnData(mCore.getName(),data,groupId);
	}
	@Override
	public void reDraw(Context context,ItemCore mCore, double baseSize)
	{
		this.mCore=mCore;
		setLayoutParams(new AbsoluteLayout.LayoutParams(
							(int)(baseSize*mCore.width),
							(int)(baseSize*mCore.height),
							(int)(baseSize*mCore.left),
							(int)(baseSize*mCore.top)
						));
		dealExtStyle();
		if(vertical){
			setRotation(90);
			if(!typeName.isEmpty()){
				File aiteTypeFile=new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/fonts/@"+typeName);
				if(aiteTypeFile.exists()){
					mTypeface=Typeface.createFromFile(aiteTypeFile);
					setTypeface(mTypeface);
				}else{
					LogUtils.e(typeName+"no @font for vertical text exists");
				}
			}
		}else{
			setRotation(0);
			if(!typeName.isEmpty()){
				File TypeFile=new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/fonts/"+typeName);
				mTypeface=Typeface.createFromFile(TypeFile);
				setTypeface(mTypeface);
			}		
		}
		if(haveImgSpan){
			View v=((AbsoluteLayout)getParent()).findViewById(imgChooserId);
			v.setLayoutParams(new AbsoluteLayout.LayoutParams(
										  (int)(baseSize*mCore.width),
										  (int)(baseSize*mCore.height),
										  (int)(baseSize*mCore.left),
										  (int)(baseSize*(mCore.top+mCore.height))
									  ));
		}
		setTextColor(0xFF000000+textColor);
		fixWandH(baseSize);
		if(withImgSpan){
			changeSpan(baseSize);
		}
	}
	private void fixWandH(double baseSize){
		setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)(baseSize*(double)textSize));
		setScaleX(1);setScaleY(1);
		//调整宽度和高度。
	while(true){
		//行高
		int lineHeight=getLineHeight();
		//以第一个字符算的字符宽度
		float fullCharsize=getPaint().measureText(getText().toString().isEmpty()?"正":getText().charAt(0)+"");
		//整个view的高度和宽度
		int frameHeight=getLayoutParams().height;
		int frameWidth=getLayoutParams().width;
		//每行的字符数
		int charPerLine=(int)((float)frameWidth/fullCharsize);//-0.05f);
		//可容纳的行数
		int linesPerText=frameHeight/lineHeight;
		//最大可容纳字符数量
		int maxFullCharNum=charPerLine*linesPerText;
		//将换行算作整行得到的实际字符数
		int realTextLength=charPerLine*StringUtils.longTextLines(getText().toString(),charPerLine);
		Log.e("",getTextSize()+"|"+realTextLength+"|"+maxFullCharNum);
		if(realTextLength>maxFullCharNum && mOOZmode!=textOOZmode.noFix){
			//操作edittext：减小字号或增加一行的高度
			AbsoluteLayout.LayoutParams params=(AbsoluteLayout.LayoutParams) getLayoutParams();
			switch(mOOZmode){
				case fixTextSize:
					setTextSize(TypedValue.COMPLEX_UNIT_PX,getTextSize()-1);
					LogUtils.i(getTextSize()+"|"+realTextLength+"|"+maxFullCharNum);
					break;
				case fixViewTop:
					params.y-=1;
					params.height+=1;
					setLayoutParams(params);
					break;
				case fixViewBottom:
					params.height+=1;
					setLayoutParams(params);
					break;
				case fixViewLeft:
					params.x-=1;
					params.width+=1;
					setLayoutParams(params);
					break;
				case fixViewRight:
					params.width+=1;
					setLayoutParams(params);
					break;
				default:
					LogUtils.e(mOOZmode+"_is not analyzable OOZmode");
					break;
			}
		}else{
			//收刀式：拉伸字符区域适配
			float scaleLimit=0.85f;
			float scalex=(float)frameWidth/(charPerLine*fullCharsize);
			float scaley=(float)frameHeight/((float)(frameHeight/lineHeight)*lineHeight);
			if(scalex>scaleLimit&&scalex<1.0/scaleLimit)setScaleX(scalex);
			if(scaley>scaleLimit&&scaley<1.0/scaleLimit)setScaleY(scaley);
			break;
		}
	}
	}
}
