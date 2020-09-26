package com.megasteelx.exse.items;
import android.widget.*;
import android.content.*;

public interface ItemInterface
{
	void addToParent(AbsoluteLayout parent,Context context,double baseSize);
	void returnData(Context context,String data);
	void reDraw(Context context,ItemCore core,double baseSize);
}
