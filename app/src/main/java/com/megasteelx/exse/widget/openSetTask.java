package com.megasteelx.exse.widget;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import java.io.File;
import android.app.AlertDialog;
import com.megasteelx.exse.utils.*;

public class openSetTask extends AsyncTask
{
	public interface isLoadDataListener {
		public void loadComplete();
	}
	public isLoadDataListener loadLisneter;
	public void setLoadDataComplete(isLoadDataListener dataComplete) {
        this.loadLisneter = dataComplete;
    }
	private String mPath="";
	private Context mContext;
	private ProgressDialog mDialog;
	public openSetTask(String path,Context context){
		super();
		mPath=path;
		if(context!=null){
			mDialog = new ProgressDialog(context);
		}
		else{
			mDialog = null;
		}
		mContext = context;
	};
	@Override
	protected Object doInBackground(Object[] p1)
	{
		try{
			FilesUtils.upZipFile(new File(mPath),SettingUtils.PATH_WORKSPACE);
				File[] fl=new File(SettingUtils.PATH_WORKSPACE).listFiles();
				for(int i=0;i<fl.length;i++){
					if(fl[i].getName().contains(SettingUtils.SUFFIX_STYLE)){
						new File(SettingUtils.PATH_SOURCE+"/"+fl[i].getName().replace(SettingUtils.SUFFIX_STYLE,"").trim()+"/").mkdir();
						FilesUtils.upZipFile(fl[i],SettingUtils.PATH_SOURCE+"/"+fl[i].getName().replace(SettingUtils.SUFFIX_STYLE,"").trim()+"/");
						SettingUtils.CARD_SET_STYLE=fl[i].getName().replace(SettingUtils.SUFFIX_STYLE,"").trim();
						fl[i].delete();
					
				}
			}
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
		if(isCancelled())
			return;
	}
	@Override
	protected void onPreExecute() {
		// TO-DO Auto-generated method stub
		//super.onPreExecute();
		if(mDialog!=null){
			mDialog.setTitle("Loading");
			mDialog.setMessage("读取存档文件");
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setCancelable(false);
			mDialog.show();
		}
	}
}
