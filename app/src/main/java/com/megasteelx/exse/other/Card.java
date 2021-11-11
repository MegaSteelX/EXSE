package com.megasteelx.exse.other;
import com.megasteelx.exse.items.*;
import java.util.*;
import com.megasteelx.exse.utils.*;
import java.io.*;
import android.util.*;

public class Card
{
	public ArrayList<ItemCore> mCores=new ArrayList<ItemCore>();
	public Card(){}
	public ItemCore getItemFromName(String name){

		for(int i=0;i<mCores.size();i++){
			Log.e(mCores.get(i).getName(),mCores.get(i).getData());
			if(mCores.get(i).getName().equals(name))
				return mCores.get(i);
		}
		LogUtils.w(name+"_no item named this");
		return null;
	}
	public void removeItemFromName(String name){
		for(int i=0;i<mCores.size();i++){
			Log.e(mCores.get(i).getName(),mCores.get(i).getData());
			if(mCores.get(i).getName().equals(name))
				mCores.remove(i);
		}
		LogUtils.w(name+"_no item named this");
	}
	public String getStyleFilePath(String resPath){
		return resPath+"/common.mdl";
	}
	public void clearAll(){
		/*for(int i=0;i<mCores.size();i++){
			mCores.remove(0);}
		*/
		mCores.clear();
	}
	public void readCardStyle(String path){
		//path:*.mdl
		if(!new File(path).exists())LogUtils.e(path+"_is not avaliable file");
		//Log.e("",FileUtils.FileToString(path));
		String []styleDescs=FilesUtils.FileToLines(path);//.split("\n");
		String []tempString=null;
		for(int i=0;i<styleDescs.length;i++){
			ItemCore tempItem=new ItemCore();
			try{//Log.e("",styleDescs[i]);
				tempString=styleDescs[i].split("/");
				tempItem=getItemFromName(tempString[0].trim());
				if(!tempString[2].equals("!"))tempItem.setHeight(Integer.parseInt(tempString[2].trim()));
				if(!tempString[1].equals("!"))tempItem.setWidth(Integer.parseInt(tempString[1].trim()));
				if(!tempString[4].equals("!"))tempItem.setTop(Integer.parseInt(tempString[4].trim()));
				if(!tempString[3].equals("!"))tempItem.setLeft(Integer.parseInt(tempString[3].trim()));
				if(path.endsWith("common.mdl")){
					tempItem.setExtStyle(tempString[5]);
				}else{
					tempItem.setExtStyle(tempItem.getExtStyle()+";"+tempString[5]);
				}
			}catch(ArrayIndexOutOfBoundsException e){
				LogUtils.w("reading style_"+e.toString());
			}catch(NumberFormatException e){
				LogUtils.e("not a number_"+e.getMessage());
			}catch(NullPointerException e){
				
			}finally{
				mCores.set(mCores.indexOf(getItemFromName(tempItem.getName())),tempItem);
				//if(!(mCores.indexOf(tempItem)<0))
				//mCores.remove(getItemFromName(tempString[0]));
				//mCores.add(tempItem);
			}
		}
	}
	public void readCardItems(String path){
		//path:*.dfn
		if(!new File(path).exists())LogUtils.e(path+"_is not avaliable file");
		String []styleDescs=FilesUtils.FileToLines(path);//.split("\n");
		String []tempString;
		for(int i=0;i<styleDescs.length;i++){
			ItemCore tempItem=new ItemCore();
			try{
				tempString=styleDescs[i].split("/");
				tempItem.setType(tempString[0].trim());
				tempItem.setName(tempString[1].trim());
				tempItem.setExtDefine(tempString[2]);
			}catch(ArrayIndexOutOfBoundsException e){
				LogUtils.e("reading items_"+e.toString());
			}finally{
				if(getItemFromName(tempItem.getName())==null){
					mCores.add(tempItem);
				}
				LogUtils.i("item named_"+mCores.get(i).getName());
			}
		}
	}
	public void linkData(CardData cData){
		ItemCore tempCore;
		for(int i=0;i<mCores.size();i++){
			tempCore= mCores.get(i);
			tempCore.setData(cData.getItemData(mCores.get(i).getName()));
			mCores.set(i,tempCore);
		}
	}
}
