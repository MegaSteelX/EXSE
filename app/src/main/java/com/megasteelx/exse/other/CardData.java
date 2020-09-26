package com.megasteelx.exse.other;
import com.megasteelx.exse.utils.*;
import com.megasteelx.exse.items.*;
import android.util.*;

public class CardData
{
	String baseString=null;
	String[] items=null;
	public CardData(String base){
		baseString=base;
		items=baseString.trim().split("\n\t");
	}
	public String getItemData(String itemName){
		String[]tempItem;
		for(int i=0;i<items.length;i++){
			if(items[i].endsWith(":")){
				tempItem=new String[]{items[i].replace(":",""),""};
			}else{
				tempItem=items[i].trim().split(":");
			}
			if(tempItem.length!=2){
				LogUtils.e(items[i]+"_cannot read data correctly");
				continue;
			}else if(tempItem[0].trim().equals(itemName.trim())){
				return tempItem[1];
			}
		}
		LogUtils.w(itemName+"_no data read");
		return "";
	}
	public void setItemData(String itemName,String data){
		String[]tempItem;boolean flag=true;
		for(int i=0;i<items.length;i++){
			if(items[i].endsWith(":")){
				//deal null data:
				tempItem=new String[]{items[i].replace(":",""),""};
			}else{
				tempItem=items[i].trim().split(":");
			}
			if(tempItem.length!=2){
				LogUtils.e(items[i]+"_cannot read data correctly");
			}else if(tempItem[0].trim().equals(itemName)){
				items[i]=itemName+":"+data;
				flag=false;
			}
		}
		if(flag)LogUtils.w(itemName+"_no data setted");
	}
	public void getDataFormCard(Card c){
		for(int i=0;i<items.length;i++){
			for(int j=0;j<c.mCores.size();j++){	
				if(c.mCores.get(j).getName()==items[i].split(":")[0]){
					items[i]=c.mCores.get(j).getName()+":"+c.mCores.get(j).getData();
				}
			}
		}
	}
	public String[] getDatas(){
		return items;
	}
	public void dataToString(){
		if(items.length==0){
			LogUtils.e("null card");
			return;
		}
		StringBuilder databuilder=new StringBuilder(items[0]);
		for(int i=1;i<items.length;i++){
			databuilder.append("\n\t");
			databuilder.append(items[i]);
		}
		baseString=databuilder.toString();
	}
	@Override
	public String toString()
	{
		dataToString();
		return baseString;
	}
	
}
