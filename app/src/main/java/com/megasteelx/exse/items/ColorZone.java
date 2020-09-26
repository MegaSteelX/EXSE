package com.megasteelx.exse.items;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import com.megasteelx.exse.utils.*;
import android.view.*;
import android.app.*;
import com.megasteelx.exse.activities.*;
import android.util.*;
import wang.relish.colorpicker.*;

public class ColorZone extends ImageView implements ItemInterface
{

	ItemCore mCore;
	int clickeeId;//,id;
	int[] clickZone;
	View clickEe;

	public ColorZone(Context context,ItemCore core,int id){
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);

		setScaleType(ScaleType.FIT_XY);
		mCore=core;
		
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
		LogUtils.i("drawing CZ@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));
		setBackgroundColor(Color.parseColor(mCore.getData()));

		//响应点击事件用的view
		clickEe=new View(context);
		clickEe.setId(clickeeId);

		parent.addView(clickEe,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*clickZone[0]),
						   (int)(baseSize*clickZone[1]),
						   (int)(baseSize*clickZone[2]),
						   (int)(baseSize*clickZone[3])
					   ));
		View.OnClickListener mOnClick=new OnClickListener(){

			@Override
			public void onClick(View p1)
			{
				ColorPickerDialog.OnColorChangedListener lis=new ColorPickerDialog.OnColorChangedListener(){

					@Override
					public void onColorChanged(int color)
					{
						ColorZone.this.setBackgroundColor(color);
						mCore.setData(OtherUtils.getHexString(color));
						returnData(context,OtherUtils.getHexString(color));
					}
					
				};
				new ColorPickerDialog.Builder(context, Color.parseColor(mCore.getData()))   
					.setHexValueEnabled(true)               //是否显示颜色值
					.setOnColorChangedListener(lis) //设置监听颜色改变的监听器
					.build()
					.show();
			}
		};
		clickEe.setOnClickListener(mOnClick);
	}

	@Override
	public void returnData(Context context, String data)
	{
		((CardEditActivity)context).onReturnData(mCore.getName(),data);
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
			setBackgroundColor(Color.parseColor(mCore.getData()));
			
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
