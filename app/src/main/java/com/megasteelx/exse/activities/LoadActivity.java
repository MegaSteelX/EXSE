package com.megasteelx.exse.activities;
import android.app.*;
import android.content.*;
import android.os.*;
import com.megasteelx.exse.utils.*;
import java.io.*;
import com.megasteelx.exse.*;
import android.view.View.*;

public class LoadActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	super.onCreate(savedInstanceState);
		
		if(SettingUtils.isFirstOpen(this)){
			SharedPreferences pref = this.getSharedPreferences("myActivityName", 0);  
			SharedPreferences.Editor editor = pref.edit();  
			editor.putBoolean("isFirstIn", false);  
			editor.commit();  
			
			//to do for first install
		}
		
		SettingUtils.makeDirs();
		//deal unsaved data
		File unClearedSet=new File(SettingUtils.PATH_WORKSPACE+"/set"),
		resFolder=new File(SettingUtils.PATH_SOURCE);
		String cardSetStyle="UNDIFINED";
		String[] res=resFolder.list();
		if(res.length<1){
			AlertDialog.Builder resNotFoundAlert=new AlertDialog.Builder(this);
			resNotFoundAlert.setTitle(R.string.error)
			.setMessage(R.string.res_not_found_alert)
				.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						startDownload();
					}
				})
				.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						quit();
					}
				})
			.show();
		}else{
			cardSetStyle=SettingUtils.getCurrentStyle(this);
			if(cardSetStyle.equals("UNDIFINED")){
				cardSetStyle=res[0];
				SettingUtils.setCurrentStyle(this,res[0]);
			}
		}
		final String cStyle=cardSetStyle;
		if(unClearedSet.exists()){
			AlertDialog.Builder setUnclearAlert=new AlertDialog.Builder(this);
			setUnclearAlert.setTitle(R.string.warning)
			.setMessage(R.string.unclear_set_alert)
				.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						startCardEdit();
					}
				})
				.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						FileUtils.ClearDir(SettingUtils.PATH_WORKSPACE);
						CardSetUtils.PrepareCardSet(SettingUtils.PATH_WORKSPACE+"/set",SettingUtils.SETFILE_HEAD,cStyle);
						startCardEdit();
					}
				})
			.show();
		}else{
			CardSetUtils.PrepareCardSet(SettingUtils.PATH_WORKSPACE+"/set",SettingUtils.SETFILE_HEAD,cStyle);
			startCardEdit();
		}
		
	}

	private void startCardEdit(){
		Intent intent=new Intent(LoadActivity.this,CardEditActivity.class);
		startActivity(intent);
		this.finish();
	}
	private void startDownload(){
		
	}
	private void quit(){
		finish();
	}
}
