package com.megasteelx.exse.items;
import android.view.*;
import android.content.*;
import android.widget.*;
import com.megasteelx.exse.utils.*;
import com.megasteelx.exse.activities.*;
import com.megasteelx.exse.*;

public class TouchSwitch extends ImageView implements ItemInterface
{
	ItemCore mCore;
	boolean sWitch;
	
	public TouchSwitch(Context context,ItemCore core,int id){
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		mCore=core;
		setId(id);
		setImageResource(R.drawable.nu);
		try{
			sWitch=Boolean.parseBoolean(mCore.getData());
		}catch(Exception e){
			LogUtils.e(mCore.getData()+"_cannot parse boolean");
		}
	}
	
	@Override
	public void addToParent(AbsoluteLayout parent, final Context context, double baseSize)
	{
		LogUtils.i("drawing TS@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));
		setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					sWitch=!sWitch;
					mCore.data=sWitch?"true":"false";
					returnData(context,sWitch?"true":"false");
				}
			});
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
		if(
			sWitch!=Boolean.parseBoolean(mCore.getData())
		){
			LogUtils.e(mCore.getData()+"_bad data with inner data");
			//mCore.data=sWitch+"";
		}
	}
}
