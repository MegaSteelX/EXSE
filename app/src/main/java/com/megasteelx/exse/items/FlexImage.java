package com.megasteelx.exse.items;
import android.view.*;
import android.content.*;
import android.widget.*;
import com.megasteelx.exse.utils.*;
import com.megasteelx.exse.activities.*;

public class FlexImage extends RelativeLayout implements ItemInterface
{
	public int groupId = -1;//if is ItemGroup child then ItemGroup Id, else NULL.

	ItemCore mCore;
	ImageView[][] imagePics;
	int strechWidth,strechHeight;
	int[] cropZone;
	String srcFileName;
	boolean isSrcDot9;

	public FlexImage(Context context,ItemCore core,int id){
		super(context);
		mCore=core;
		setFocusable(true);
		setFocusableInTouchMode(true);

		setId(id);

		dealExtStyle();
		imagePics=new ImageView[3][3];
		cropZone=new int[]{1,1,2,2};
		srcFileName=core.name;
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
					if(!(isStrechWdfned&&isStrechHdfned)){
						LogUtils.w(mCore.getName()+"not define origin size");
						strechHeight=mCore.height;
						strechWidth=mCore.width;
					}
				}
			}
		}//extstl处理完毕。
	}

	@Override
	public void addToParent(AbsoluteLayout parent, Context context, double baseSize)
	{
		LogUtils.i("drawing FI@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));
		
		//todo
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
