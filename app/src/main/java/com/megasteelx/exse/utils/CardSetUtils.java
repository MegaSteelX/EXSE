package com.megasteelx.exse.utils;
import java.io.*;
import com.megasteelx.exse.other.*;
import java.util.*;
import com.megasteelx.exse.widget.*;
import android.content.*;

public class CardSetUtils
{
    public static boolean PrepareCardSet(String path,String cardSetHead,String cardSetStyle){
        if(!new File(path).exists()){
		try
		{
			FileWriter setWriter=new FileWriter(new File(path));
			String newCard=FileUtils.FileToStrings(SettingUtils.PATH_SOURCE+"/"+cardSetStyle+"/new_card.dfn");
			setWriter.write(cardSetHead+"\nstyle:"+cardSetStyle+"\n"+newCard);
			setWriter.close();
		}
		catch (IOException e)
		{return false;}
		}
		return true;
    }

	public static String[] getCardSetByFile(String path){
		String setFileString=FileUtils.FileToStrings(path);
		return setFileString.split("\ncard:\n");
	}
	public static void writeCardSetToFile(CardSet cSet,String path){
		FileUtils.saveStringToFile(path,cSet.savingCardSet(SettingUtils.SETFILE_HEAD),true);
	}
	public static void saveCardSet(Context context,CardSet cSet,String path,String name,boolean ifExit){
		writeCardSetToFile(cSet,SettingUtils.PATH_WORKSPACE+"/set");
		String realPath=StringUtils.fixName(path+"/"+(name.isEmpty()?"Untitled":name),SettingUtils.SUFFIX_SAVEDATA);
		String setFile=cSet.savingCardSet("");
		saveSetTask task=new saveSetTask(new String[]{realPath,setFile},context,ifExit);
		task.execute();
	}
}
