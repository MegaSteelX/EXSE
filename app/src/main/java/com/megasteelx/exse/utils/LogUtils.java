package com.megasteelx.exse.utils;
import android.graphics.*;
import java.util.*;
import android.widget.*;
import java.io.*;
import android.os.*;
import android.util.*;
import org.xml.sax.*;
import android.view.*;
import android.content.*;

public class LogUtils
{
	private static String LOG_FILE_NAME="log.txt";
	private static ArrayList<String>logger=new ArrayList<String>();
	private static void publishLog(char type,String log){
		//Log.e(type+"",log);
		logger.add(type+"\t:\t"+log);
	}
	public static void displayLog(int dex,TextView logView){
		String oneLog=logger.get(dex);
		char type=oneLog.charAt(0);
		logView.setText(oneLog);
		switch(type){
			case 'i':logView.setTextColor(Color.GREEN);break;
			case 'w':logView.setTextColor(Color.YELLOW);break;
			case 'e':logView.setTextColor(Color.RED);break;
			case 'd':logView.setTextColor(Color.GRAY);break;
				
		}
		
	}
	public static void displayWholeLog(Context context,ViewGroup parent){
		TextView oneLogText;
		for(int i=0;i<logger.size();i++){
			oneLogText=new TextView(context);
			parent.addView(oneLogText);
			//oneLogText.setTextAppearance(android.R.attr.textAppearanceSmall);
			displayLog(i,oneLogText);
		}
	}
	public static void i(String information){
		publishLog('i',information);
		
	}
	public static void w(String warning){
		publishLog('w',warning);
	}
	public static void e(String error){
		publishLog('e',error);
	}
	public static void d(String debug){
		publishLog('d',debug);
	}
	public static void printLogFile(String folderPath){
		try{
			File logFile=new File(folderPath+System.currentTimeMillis()+LOG_FILE_NAME);
			if(logFile.exists())logFile.delete();
			FileWriter logWriter=new FileWriter(logFile);
			for(int i=0;i<logger.size();i++){
				logWriter.write(logger.get(i));
			}
			logWriter.close();
		}catch(Exception e){}
	}
}
