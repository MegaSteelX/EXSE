package com.megasteelx.exse.items;
import android.annotation.SuppressLint;
import android.widget.*;
import android.content.*;
import com.megasteelx.exse.activities.*;
import com.megasteelx.exse.utils.*;
import android.view.*;
import java.io.*;
import android.graphics.*;

@SuppressLint("AppCompatCustomView")
public class ExchangeImage extends ImageView implements ItemInterface
{
	ItemCore mCore;
	int dexNumber=0;
	String folderpath="exchangeimgs";
	
	public ExchangeImage(Context context,ItemCore core,int id){
		super(context);
		mCore=core;
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		setScaleType(ScaleType.FIT_XY);
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
					if(extStlKVP[0].trim().equals("dex")){
						dexNumber=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("path")){
						folderpath=extStlKVP[1].trim();
					}
					//done.
				}
			}
		}//extstl处理完毕。
	}
	
	@Override
	public void addToParent(AbsoluteLayout parent, final Context context, double baseSize)
	{
		LogUtils.i("drawing EI@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));
		solveImage();
		setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					dexNumber++;
					String path=SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+folderpath+"/"+mCore.getName()+dexNumber+".png";
					if(!(new File(path).exists())){
						dexNumber=0;
					}
					solveImage();
					returnData(context,dexNumber+"");
				}
			});
	}
	private void solveImage(){
		String path=SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+folderpath+"/"+mCore.getName()+dexNumber+".png";
		setImageBitmap(BitmapFactory.decodeFile(path));
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
		solveImage();
	}
}
