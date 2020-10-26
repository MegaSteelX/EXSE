package com.megasteelx.exse.items;
import android.view.*;
import android.content.*;
import android.widget.*;
import com.megasteelx.exse.utils.*;
import com.megasteelx.exse.activities.*;
import android.graphics.*;
import android.view.ViewGroup.*;
import android.graphics.drawable.*;
import android.widget.ImageView.*;

public class FlexImage extends RelativeLayout implements ItemInterface
{
	public int groupId = -1;//if is ItemGroup child then ItemGroup Id, else NULL.

	ItemCore mCore;
	ImageView[][] imagePics;
	int[][] imgPicId;
	int strechWidth,strechHeight;
	int[] cropZone;
	String srcFileName="";
	boolean isSrcDot9=false;
	boolean inited=false;

	public FlexImage(Context context,ItemCore core,int id){
		super(context);
		mCore=core;
		setFocusable(true);
		setFocusableInTouchMode(true);

		setId(id);

		dealExtStyle();
		imgPicId= new int[3][3];
		imagePics=new ImageView[3][3];
		cropZone=new int[]{1,1,2,2};
		//srcFileName=core.name;
	}
	private void dealExtStyle(){
		//读取extstyle
		if(!(mCore.getExtStyle()==null||mCore.getExtStyle().isEmpty())){
			String tempExtStl=new String(mCore.getExtStyle());
			String[]extStls=tempExtStl.split(";");
			boolean isStrechWdfned=false,isStrechHdfned=false;
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
					if(extStlKVP[0].trim().equals("source")){
						srcFileName=extStlKVP[1].trim();
					}
					if(extStlKVP[0].trim().equals("width")){
						strechWidth=Integer.parseInt(extStlKVP[1].trim());
						isStrechWdfned=true;
					}
					if(extStlKVP[0].trim().equals("height")){
						strechHeight=Integer.parseInt(extStlKVP[1].trim());
						isStrechHdfned=true;
					}
					if(extStlKVP[0].trim().equals("flexzone")){
						String[] tstr=extStlKVP[1].trim().split("×");
						if(tstr.length!=4){
							LogUtils.e("illegal flex zone");
						}else{
							for(int j=0;j<4;j++){
								cropZone[j]=Integer.parseInt(tstr[j].trim());
							}
							if(cropZone[2]<=cropZone[0]){
								LogUtils.e("flex zone wid <= 0");
								cropZone[2]=cropZone[0]+1;
							}
							if(cropZone[3]<=cropZone[1]){
								LogUtils.e("flex zone hei <= 0");
								cropZone[3]=cropZone[1]+1;
							}
						}
					}
					//done.
				}
			}
			if(!(isStrechWdfned&&isStrechHdfned)){
				LogUtils.w(mCore.getName()+"not define origin size");
				strechHeight=mCore.height;
				strechWidth=mCore.width;
			}
		}//extstl处理完毕。
	}

	@Override
	public void addToParent(AbsoluteLayout parent, Context context, double baseSize)
	{
		try{
		LogUtils.i("drawing FI@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));
		Bitmap res=BitmapFactory.decodeFile(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+mCore.getName()+"/"+(srcFileName.equals("")?"src.9.png":srcFileName));
		if(srcFileName.endsWith(".9.png"))isSrcDot9=true;
		else isSrcDot9=false;
		int hei=res.getHeight(),wid=res.getWidth();
		int shei=strechHeight,swid=strechWidth;
		int s2hei=(int)(baseSize*mCore.height),s2wid=(int)(baseSize*mCore.width);
		//get crop data
		if(isSrcDot9){
			boolean flag=true;
			for(int i=0;i<wid;i++){
				if(res.getPixel(i,0)==Color.BLACK&&flag){
					cropZone[0]=(int)((i-1)*swid/(0d+wid));
					flag=false;
				}
				if(!flag&&res.getPixel(i,0)!=Color.BLACK){
					cropZone[2]=(int)((i-1)*swid/(0d+wid));
					break;
				}
				if(i==wid-1)cropZone[2]=(int)(i*swid/(0d+wid));
			}
			flag=true;
			for(int j=0;j<hei;j++){
				if(res.getPixel(0,j)==Color.BLACK&&flag){
					cropZone[1]=(int)((j-1)*shei/(0d+hei));
					flag=false;
				}
				if(!flag&&res.getPixel(0,j)!=Color.BLACK){
					cropZone[3]=(int)((j-1)*shei/(0d+hei));
					break;
				}
				if(j==hei-1)cropZone[3]=(int)(j*shei/(0d+hei));
			}
			res=Bitmap.createBitmap(res,1,1,wid-2,hei-2);
		}
		//scale
		res=Bitmap.createScaledBitmap(res,(int)(strechWidth*baseSize),(int)(strechHeight*baseSize),false);
		for(int i=0;i<4;i++)cropZone[i]=(int)(cropZone[i]*baseSize);
		//crop
		int[] cropx=new int[]{0,cropZone[0],cropZone[2],(int)(strechWidth*baseSize)};
		int[] cropy=new int[]{0,cropZone[1],cropZone[3],(int)(strechHeight*baseSize)};
			//LogUtils.d(0+","+cropZone[0]+","+cropZone[2]+","+(int)(strechWidth*baseSize)+"="+0+","+cropZone[1]+","+cropZone[3]+","+(int)(strechHeight*baseSize));
		//scale
			int[] strex=new int[]{0,cropZone[0],cropZone[2]+(int)(baseSize*mCore.width)-(int)(strechWidth*baseSize),(int)(baseSize*mCore.width)};
			int[] strey=new int[]{0,cropZone[1],cropZone[3]+(int)(baseSize*mCore.height)-(int)(strechHeight*baseSize),(int)(baseSize*mCore.height)};
			
		RelativeLayout.LayoutParams params;
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				imgPicId[i][j]=View.generateViewId();
				imagePics[i][j]=new ImageView(context);
				ImageView imgv=imagePics[i][j];
				imgv.setId(imgPicId[i][j]);
				addView(imgv);
				params=new RelativeLayout.LayoutParams(strex[i+1]-strex[i],strey[j+1]-strey[j]);//xscales[i+1]-xscales[i],yscales[j+1]-yscales[j]);
				if(i==0){
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				}else{
					params.addRule(RelativeLayout.RIGHT_OF,imgPicId[i-1][j]);
				}
				if(j==0){
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				}else{
					params.addRule(RelativeLayout.BELOW,imgPicId[i][j-1]);
				}
				imgv.setLayoutParams(params);
				imgv.setImageBitmap(Bitmap.createBitmap(res,cropx[i],cropy[j],cropx[i+1]-cropx[i],cropy[j+1]-cropy[j]));
				imgv.setScaleType(ScaleType.FIT_XY);
				imgv.setOnTouchListener(new OnTouchListener(){

						@Override
						public boolean onTouch(View p1, MotionEvent p2)
						{
							return true;
						}
					});
			}
		}
		inited=true;

		//todo
		}catch(Exception e){
			//LogUtils.d(e.toString());
		}
	}

	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params)
	{
		if(inited){
		int hei=params.height;
		int wid=params.width;
		int changedH=hei-imagePics[0][2].getLayoutParams().height-imagePics[0][0].getLayoutParams().height;
		int changedW=wid-imagePics[2][0].getLayoutParams().width-imagePics[0][0].getLayoutParams().width;
		RelativeLayout.LayoutParams pams;
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				if(i==1){
					pams=(RelativeLayout.LayoutParams)imagePics[i][j].getLayoutParams();
					pams.width=changedW;
					imagePics[i][j].setLayoutParams(pams);
				}
				if(j==1){
					pams=(RelativeLayout.LayoutParams)imagePics[i][j].getLayoutParams();
					pams.height=changedH;
					imagePics[i][j].setLayoutParams(pams);
				}
			}
		}
		}
		super.setLayoutParams(params);
	}

	@Override
	public void returnData(Context context, String data)
	{
		mCore.data=data;
		((CardEditActivity)context).onReturnData(mCore.getName(),data,groupId);
	}

	@Override
	public void reDraw(Context context,ItemCore more, double baseSize)
	{
		mCore=more;
		setLayoutParams(new AbsoluteLayout.LayoutParams(
							(int)(baseSize*more.width),
							(int)(baseSize*more.height),
							(int)(baseSize*more.left),
							(int)(baseSize*more.top)
						));
		dealExtStyle();
	}
}
