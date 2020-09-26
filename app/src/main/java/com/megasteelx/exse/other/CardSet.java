package com.megasteelx.exse.other;
import com.megasteelx.exse.utils.*;
import java.io.*;
import java.util.*;

public class CardSet
{
	private String mSetStyle=null;
	private ArrayList<String>mCards=new ArrayList<String>();
	private String mSetHead=null;
	public CardSet(String path){
		createCardSet(path);
	};
	
	public void createCardSet(String path){
		for(int i=0;i<mCards.size();){
			mCards.remove(0);
		}
		String[]rawCardSet=CardSetUtils.getCardSetByFile(path);
		if(rawCardSet.length==0){
			LogUtils.e("reading savedata:no setfile aviliable");
		}else{
			//mCards=new String[rawCardSet.length-1];
			for(int i=0;i<rawCardSet.length-1;
				mCards.add(rawCardSet[++i])){}
			String[]rawHead=rawCardSet[0].split("\nstyle:");
			mSetHead=rawHead[0];
			if(rawHead.length!=2){
				LogUtils.e("cannot get style info");
			}else{
			mSetStyle=rawHead[1];
			}
		}
	}
	public ArrayList<String>getCards(){
		return mCards;
	}
	public String getStyle(){
		return mSetStyle;
	}
	public void makeStyle(String style){
		mSetStyle=style;
	}
	public String savingCardSet(String setHead){
		String settext=setHead;
		StringBuilder result=new StringBuilder(settext);
		result.append("\nstyle:").append(mSetStyle);
		for(int i=0;i<mCards.size();i++){
			result.append("\ncard:\n\t").append(mCards.get(i));
		}
		return result.toString();
	}
	public String getCard(int dex){
		if(dex>=mCards.size()){
			LogUtils.e(dex+"_dex out of set size_"+mCards.size());
			return null;//mCards.get(0);
		}else return mCards.get(dex);
	}
	public void setCard(int dex,String set){
		if(dex>=mCards.size()){
			LogUtils.e(dex+"_setcard dex out of set size_"+mCards.size());
		}else{
			mCards.set(dex,set);
		}
	}
	public void addCard(String set){
		mCards.add(set);
	}
	public void copyCard(int dex){
		if(dex>=mCards.size()){
			LogUtils.e(dex+"_copycard dex out of set size_"+mCards.size());
		}else{
			mCards.add(getCard(dex));
		}
	}
	public void delCard(int dex){
		if(dex>=mCards.size()){
			LogUtils.e(dex+"_deletecard dex out of set size_"+mCards.size());
		}else{
			mCards.remove(dex);
		}
	}
	/*
	public static String[] getCardSetByFile(String path){
		String setFileString=FileUtils.FileToString(path);
		return setFileString.split("\ncard:\n");
	}
	public static void writeCardSetToFile(CardSet cSet,String path){
		FileUtils.saveStringToFile(path,cSet.savingCardSet(SettingUtils.SETFILE_HEAD),true);
	}
	*/
}
