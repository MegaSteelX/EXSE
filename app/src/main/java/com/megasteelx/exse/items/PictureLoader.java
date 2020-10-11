package com.megasteelx.exse.items;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import com.megasteelx.exse.utils.*;
import android.view.*;
import android.app.*;
import com.megasteelx.exse.activities.*;
import android.util.*;

public class PictureLoader extends ImageView implements ItemInterface
{
	public int groupId = -1;//if is ItemGroup child then ItemGroup Id, else NULL.
	
	ItemCore mCore;
	int clickeeId;//,id;
	int[] clickZone;
	View clickEe;
	
	public PictureLoader(Context context,ItemCore core,int id){
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		setScaleType(ScaleType.FIT_XY);
		mCore=core;
		/*byte[] data=mCore.getData().getBytes();
		byte[] datas=new byte[]{data[0],data.length<2?0:data[1],data.length<3?1:data[2],data.length<4?2:data[3]};
		idValue = ((datas[0]&0xFF)   
            | ((datas[+1]<<8) & 0xFF00)  
            | ((datas[+2]<<16)& 0xFF0000)   
            | ((datas[+3]<<24) & 0xFF000000));
		
		this.id=id;*/
		setId(id);
		clickZone=new int[]{mCore.width,mCore.height,mCore.left,mCore.top};
		clickeeId=View.generateViewId();
		//=""+mCore.getExtStyle();
		dealExtStyle();
		((CardEditActivity)context).recordPicClickee(clickeeId);
	}
	private void dealExtStyle(){
		String extZone="";
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
					if(extStlKVP[0].trim().equals("clickzone")){
						extZone=extStlKVP[1];
					}
					//done.
				}
			}
		}//extstl处理完毕。
		if(!extZone.isEmpty()){
			if(extZone.equals("self")){
				clickZone[0]=mCore.width;
				clickZone[1]=mCore.height;
				clickZone[2]=mCore.left;
				clickZone[3]=mCore.top;
			}else{
				String[] clickZoneDfn=extZone.split("×");
				for(int i=0;i<clickZoneDfn.length;i++){
					clickZone[i]=Integer.parseInt(clickZoneDfn[i]);
				}
			}
		}
	}
	@Override
	public void addToParent(AbsoluteLayout parent, final Context context, double baseSize)
	{
		LogUtils.i("drawing PL@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));
		setImageBitmap(BitmapFactory.decodeFile(SettingUtils.PATH_WORKSPACE+"/"+mCore.getData()));
		
		//响应点击事件用的view
		clickEe=new View(context);
		clickEe.setId(clickeeId);
		
		parent.addView(clickEe,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*clickZone[0]),
						   (int)(baseSize*clickZone[1]),
						   (int)(baseSize*clickZone[2]),
						   (int)(baseSize*clickZone[3])
					   ));
		ImageView.OnClickListener mOnClick=new OnClickListener(){

			@Override
			public void onClick(View p1)
			{
				((CardEditActivity)context).startImgLoad(getId());
			}
		};
		clickEe.setOnClickListener(mOnClick);
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
		try{
		mCore=more;
		setLayoutParams(new AbsoluteLayout.LayoutParams(
			(int)(baseSize*mCore.width),
			(int)(baseSize*mCore.height),
			(int)(baseSize*mCore.left),
			(int)(baseSize*mCore.top)
		));
		dealExtStyle();
		clickEe=((AbsoluteLayout)getParent()).findViewById(clickeeId);
		clickEe.setLayoutParams(new AbsoluteLayout.LayoutParams(
			(int)(baseSize*clickZone[0]),
			(int)(baseSize*clickZone[1]),
			(int)(baseSize*clickZone[2]),
			(int)(baseSize*clickZone[3])
		));
		}catch(Exception e){Log.e("",e.toString());}
	}
}
