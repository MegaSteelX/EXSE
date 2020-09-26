package com.megasteelx.exse.utils;
import android.os.*;
import android.content.*;
import java.io.*;

public class SettingUtils
{
	public static String APP_TAG="EXSE";

	public static double BASE_SIZE=0;

	public static String PATH_FOLDER=Environment.getExternalStorageDirectory().getPath()+"/exse";
	public static String PATH_SOURCE=PATH_FOLDER+"/res";
	public static String PATH_SAVEDATA=PATH_FOLDER+"/savedata";
	public static String PATH_PICTURE=PATH_FOLDER+"/picture";
	public static String PATH_WORKSPACE=PATH_FOLDER+"/temp";
	
	public static String SUFFIX_SAVEDATA=".exst";
	public static String SUFFIX_STYLE=".xtl";
	
	public static String SETFILE_HEAD="exse 0.0.0 savedata\ncopyright by MegaSteelX\n";
	public static String CARD_SET_STYLE="Unknown_style";
	
	public static boolean isFirstOpen(Context context){
		Boolean isFirstIn = false;  
		SharedPreferences pref = context.getSharedPreferences(APP_TAG, 0);  
		isFirstIn = pref.getBoolean("isFirstIn", true);
		return isFirstIn;
	}
	public static boolean isDonated(Context context){
		Boolean isDonated = false;  
		SharedPreferences pref = context.getSharedPreferences(APP_TAG, 0);  
		isDonated = pref.getBoolean("isDonated", true);
		return isDonated;
	}
	public static void performDonate(Context context,Boolean isdonated){
		SharedPreferences pref = context.getSharedPreferences(APP_TAG, 0);  
		pref.edit().putBoolean("isDonated",isdonated).commit();
	}
	public static String getCurrentStyle(Context context){
		String currentStyle;  
		SharedPreferences pref = context.getSharedPreferences(APP_TAG, 0);  
		currentStyle = pref.getString("currentStyle", "UNDIFINED");
		return currentStyle;
	}
	public static void setCurrentStyle(Context context,String style){
		SharedPreferences pref = context.getSharedPreferences(APP_TAG, 0);  
		pref.edit().putString("currentStyle",style).commit();
		CARD_SET_STYLE=style;
	}
	public static void makeDirs(){
		File f=new File(PATH_FOLDER);
		String [] paths=new String[]{PATH_WORKSPACE,PATH_PICTURE,PATH_SOURCE,PATH_SAVEDATA};
		for(int i=0;i<paths.length;i++){
			f=new File(paths[i]);
			if(!f.exists())f.mkdirs();
		}
	}
}
