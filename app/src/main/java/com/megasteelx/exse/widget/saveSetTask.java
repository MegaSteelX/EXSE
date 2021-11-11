package com.megasteelx.exse.widget;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import android.app.Activity;
import com.megasteelx.exse.utils.*;
import java.io.*;
import android.widget.*;

public class saveSetTask extends AsyncTask
{

	private boolean INSERT_STYLE;
	public interface isLoadDataListener {
		public void loadComplete();
	}
	public isLoadDataListener loadLisneter;
	public void setLoadDataComplete(isLoadDataListener dataComplete) {
        this.loadLisneter = dataComplete;
    }
	
private String filePath="";
private String setFile="";
private Context mContext;
private ProgressDialog mDialog;
private boolean mifEnd;
public saveSetTask(String[] names,Context context,boolean ifEnd){
	super();
	filePath=names[0];
	setFile=names[1];
	if(context!=null){
		mDialog = new ProgressDialog(context);
	}
	else{
		mDialog = null;
	}
	mContext = context;
	mifEnd=ifEnd;
};
	@Override
	protected Object doInBackground(Object[] p1)
	{
		try
		{	
			if(INSERT_STYLE){
				FilesUtils.zip(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE,SettingUtils.PATH_WORKSPACE+"/"+SettingUtils.CARD_SET_STYLE+SettingUtils.SUFFIX_STYLE);
			}
			//处理多余的图像文件
			String[]deletedImgs=new File(SettingUtils.PATH_WORKSPACE).list(new FilenameFilter(){

					@Override
					public boolean accept(File p1, String p2)
					{
						return !p2.endsWith("set");
					}
				});
			
			for(int i=0;i<deletedImgs.length;i++){
				if(!setFile.contains(deletedImgs[i])){
					//存档中没有提及该文件名，无用，删除
					new File(SettingUtils.PATH_WORKSPACE+"/"+deletedImgs[i]).delete();
				}
			}
			//图像处理完毕
			FilesUtils.zip(SettingUtils.PATH_WORKSPACE,filePath);
			
		}catch(Exception e){
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		// TO-DO Auto-generated method stub
		//super.onPostExecute(result);
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		if (loadLisneter != null) {
			loadLisneter.loadComplete();
		}
		if(mifEnd)((Activity)mContext).finish();
		if(isCancelled())
			return;
	}
	@Override
	protected void onPreExecute() {
		// TO-DO Auto-generated method stub
		//super.onPreExecute();
		if(mDialog!=null){
			mDialog.setTitle("Saving");
			mDialog.setMessage("正在存档");
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setCancelable(false);
			mDialog.show();
		}
	}
}
