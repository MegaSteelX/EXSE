package com.megasteelx.exse.items;
import android.view.*;
import android.content.*;
import android.widget.*;
import com.megasteelx.exse.utils.*;
import com.megasteelx.exse.activities.*;
//YOU SHOULD NEVER USE OR EXTEND THIS CLASS DIRECTLY.
//不要直接引用或者继承这个类。
public class SampleItem extends View implements ItemInterface
{
	ItemCore mCore;
	
	public SampleItem(Context context,ItemCore core,int id){
		super(context);
		mCore=core;
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		setId(id);
		
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
					if(extStlKVP[0].trim().equals("SIMPLE")){
						//Do something.
					}
					//done.
				}
			}
		}//extstl处理完毕。
	}
	
	@Override
	public void addToParent(AbsoluteLayout parent, Context context, double baseSize)
	{
		LogUtils.i("drawing SI@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));
	}

	@Override
	public void returnData(Context context, String data)
	{
		((CardEditActivity)context).onReturnData(mCore.getName(),data);
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
