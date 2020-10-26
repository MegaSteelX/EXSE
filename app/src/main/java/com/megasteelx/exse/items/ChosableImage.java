package com.megasteelx.exse.items;
import android.widget.ImageView;
import android.content.Context;
import android.widget.ActionMenuView.LayoutParams;
import android.graphics.Canvas;
import android.widget.LinearLayout;
import android.widget.AbsoluteLayout;
import android.view.View;
import java.io.File;
import java.io.FilenameFilter;
import android.graphics.BitmapFactory;
import com.megasteelx.exse.utils.*;
import android.widget.*;
import android.view.*;
import java.util.*;
import android.widget.PopupMenu.*;
import com.megasteelx.exse.*;
import android.util.*;
import java.lang.reflect.*;
import com.megasteelx.exse.activities.*;
import android.graphics.*;

public class ChosableImage extends ImageView implements ItemInterface
{
	public int groupId = -1;//if is ItemGroup child then ItemGroup Id, else NULL.

    ItemCore mCore;
    private int mChoice=1;
    private boolean ShowVoid=false;
    ArrayList<String> realFileName=new ArrayList<String>();
	ArrayList<String> virtualFileName=new ArrayList<String>();
	String childFolder=".";
	View popPointer;int pointerId;
	PopupMenu imgChooseMenu;
    
    
    public ChosableImage(Context context,ItemCore core,int id){
        super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
        setId(id);
		setScaleType(ScaleType.FIT_XY);
		mCore=core;
		if(!mCore.mType.equals("ChosableImage")){
			LogUtils.e(mCore.name+"_request "+mCore.mType+",returns ChosableImage");
		}
		String[] names=getNameList(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE);
		String[] temp;
		for(int i=0;i<names.length;i++){
			temp=names[i].split("=");
			if(temp.length<2){
				LogUtils.w(names[i]+"_not a name pair");
			}else{
				virtualFileName.add(temp[0]);
				realFileName.add(temp[1]);
				//attention!:itemId=1>>rfn[0],not [1]
			}
		}
		mChoice=virtualFileName.indexOf(mCore.getData().trim());
		if(mChoice>=0){
			mChoice++;
		}else{
			if(!mCore.getData().equals("NullImage"))LogUtils.e("loading carddata_"+mCore.getData()+"_failed");
			mChoice=0;
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
					LogUtils.e(tempExtStl+"_not expected name K-V pair");
				}else{
					//此处处理extstl键值对（伪）
					if(extStlKVP[0].trim().equals("groupId")){
						groupId=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("showvoid")){
						setShowVoid(Boolean.parseBoolean(extStlKVP[1].trim()));
					}
					if(extStlKVP[0].trim().equals("childfolder")){
						childFolder=extStlKVP[1].trim();
					}
					//done.
				}
			}
		}//extstl处理完毕。
	}
	/*
    public ChosableImage(Context context,ItemCore core,int choice){
        super(context);
        setScaleType(ScaleType.FIT_XY);
        mCore=core;
        mChoice=choice;
		if(!mCore.mType.equals("ChosableImage")){
			LogUtils.e(mCore.name+"_request "+mCore.mType+",returns ChosableImage");
		}
    }
	*/
    public void setShowVoid(boolean showVoid)
    {
        ShowVoid = showVoid;
    }

    public boolean isShowVoid()
    {
        return ShowVoid;
    }

    public void setChoice(int choice)
    {
        mChoice = choice;
    }

    public int getChoice()
    {
        return mChoice;
    }
    private String[] getNameList(String rootPath){
		String[] nameFile=FileUtils.FileToLines(rootPath+"/"+mCore.name+"/names.dfn");
		return nameFile;//.trim().split("\n");
	}
    public boolean checkImage(){
		
		//redo this
		if(mChoice==0){
			setImageResource(R.drawable.nu);
		}else{
			setImage(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+mCore.getName()+"/"+childFolder+"/"+realFileName.get(mChoice-1),false);
		/*
        try{
			File fileName=(new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE + "/" + mCore.name).listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File p1, String p2)
                {
                    return (p2.contains(".jpg")||p2.contains(".bmp")||p2.contains(".png"));
                }
            }))[mChoice-1];
        setImageBitmap(BitmapFactory.decodeFile(fileName.getPath()));
        return true;
        }catch(Exception e){
			Log.e("",e.toString());
            return false;
        }*/
		}
		return true;
    }
	private void setImage(String path,boolean isVoid){
		if(isVoid){
			setImageResource(R.drawable.nu);
		}else{
			setImageBitmap(BitmapFactory.decodeFile(path));
		}
	}
    public void addToParent(AbsoluteLayout parent,final Context context,double baseSize){
        
		LogUtils.i("drawing CI@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		parent.addView(this,new AbsoluteLayout.LayoutParams(
            (int)(baseSize*mCore.width),
            (int)(baseSize*mCore.height),
            (int)(baseSize*mCore.left),
            (int)(baseSize*mCore.top)
        ));
		popPointer=new View(context);
		pointerId=View.generateViewId();
		popPointer.setId(pointerId);
		parent.addView(popPointer,new AbsoluteLayout.LayoutParams(
			0,0,
			(int)(baseSize*mCore.top),
			(int)(baseSize*mCore.left)
		));
		checkImage();
		imgChooseMenu=new PopupMenu(context,popPointer);
		Menu imgMenu= imgChooseMenu.getMenu();
		if(ShowVoid)imgMenu.add(0,0,0,"null");
		
		for(int i=0;i<virtualFileName.size();i++){
			
				imgMenu.add(i+1,i+1,i+1,virtualFileName.get(i));
				//attention!:itemId=1>>rfn[0],not [1]
			
		}
		
		
		final PopupMenu.OnMenuItemClickListener imageMenuListener=new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem p1)
			{
				int dex=p1.getItemId();
				mChoice=dex;
				//mCore.setData(""+dex);
				if(dex<=0){
					setImage(null,true);
					returnData(context,"NullImage");
				}else{
					setImage(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+mCore.getName()+"/"+childFolder+"/"+realFileName.get(dex-1),false);
					returnData(context,virtualFileName.get(dex-1));
				}
				return false;
			}
		};
        setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
					imgChooseMenu.setOnMenuItemClickListener(imageMenuListener);
					imgChooseMenu.show();
				
                }
            });
		
    }
	@Override
	public void returnData(Context context, String data)
	{
		mCore.data=data;
		((CardEditActivity)context).onReturnData(mCore.getName(),data,groupId);
		LogUtils.w(data);
	}
	@Override
	public void reDraw(Context context,ItemCore more,double baseSize){
		LogUtils.i("reDraw CI@"+baseSize+more.extStyle+more.width+more.height+more.left+more.top);
		mCore=more;
		if(!more.mType.equals("ChosableImage")){
			LogUtils.e(more.name+"_request "+more.mType+",returns ChosableImage");
		}
		setLayoutParams(new AbsoluteLayout.LayoutParams(
            (int)(baseSize*more.width),
            (int)(baseSize*more.height),
            (int)(baseSize*more.left),
            (int)(baseSize*more.top)
        ));
		popPointer=((AbsoluteLayout)getParent()).findViewById(pointerId);
		popPointer.setLayoutParams(new AbsoluteLayout.LayoutParams(
			0,0,
			(int)(baseSize*more.top),
			(int)(baseSize*more.left)
		));
		dealExtStyle();
		checkImage();
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect)
	{
		// TODO: Implement this method
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if(gainFocus)performClick();
	}
	
}
