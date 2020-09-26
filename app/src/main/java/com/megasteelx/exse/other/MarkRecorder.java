package com.megasteelx.exse.other;
import java.util.*;
import com.megasteelx.exse.utils.*;

public class MarkRecorder
{
	private ArrayList<String>mark=new ArrayList<String>();
	private boolean isMarkChangedFlag=false;
	
	public void addMark(String oneMark){
		if(mark.indexOf(oneMark)<0){
			mark.add(oneMark);
		}else{
			LogUtils.e(oneMark+"_cant add mark 'cause'f been added");
		}
	}
	public void removeMark(String oneMark){
		if(mark.indexOf(oneMark)>=0){
			mark.remove(mark.indexOf(oneMark));
		}else{
			LogUtils.e(oneMark+"_cant remove mark 'cause'f not added");
		}
	}
	public ArrayList<String>getMarks(){
		return mark;
	}
	public boolean isMarked(String oneMark){
		return mark.indexOf(oneMark)>=0;
	}
	public boolean isChanged(){
		boolean changed=isMarkChangedFlag;
		isMarkChangedFlag=false;
		return changed;
	}
	public void checkMarks(CardData cData,String markFilePath){
		String[] tempStrings=FileUtils.FileToString(markFilePath).trim().split("\n");
		String markName="",judge="";
		for(int i=0;i<tempStrings.length;i++){
			try{
				markName=tempStrings[i].trim().split(":")[0];
				judge=tempStrings[i].trim().split(":")[1];
				boolean isJudgeTrue=isAllTrue(judge,cData);
				if(isJudgeTrue&&!isMarked(markName)){
					addMark(markName);
					isMarkChangedFlag=true;
				}else if(!isJudgeTrue&&isMarked(markName)){
					removeMark(markName);
					isMarkChangedFlag=true;
				}
			}catch(Exception e){
				LogUtils.e("mark_"+tempStrings[i]+"_cannot read"+e.toString());
				continue;
			}
		}
	}
	private boolean isAllTrue(String judges,CardData cData){
		Stack<String> opreateStack=new Stack<String>();
		int andIndex=0,orIndex=0,currentIndex=0,lastIndex=0;
		boolean result=true,isNot=false,flag=true;String oneJudge;
		while(flag){
			lastIndex=currentIndex;
			andIndex=judges.indexOf('&',currentIndex);
			orIndex=judges.indexOf('|',currentIndex);
			currentIndex=andIndex<0?orIndex:orIndex<0?andIndex:andIndex<orIndex?andIndex:orIndex;
			if(currentIndex<0){
				currentIndex=judges.length()-1;
				flag=false;
			}
			currentIndex=currentIndex+1;
			andIndex=currentIndex;
			orIndex=currentIndex;
			opreateStack.push(judges.substring(lastIndex,currentIndex));
		}
		while(0<opreateStack.size()){
			oneJudge=opreateStack.pop();
			if(oneJudge.startsWith("!")){
				isNot=true;
				oneJudge=oneJudge.substring(1,oneJudge.length());
			}
			if(oneJudge.endsWith("&")){
				oneJudge=oneJudge.substring(0,oneJudge.length()-1);
				result=(isNot?!isTrue(oneJudge,cData):isTrue(oneJudge,cData))&result;
			}else if(oneJudge.endsWith("|")){
				oneJudge=oneJudge.substring(0,oneJudge.length()-1);
				result=(isNot?!isTrue(oneJudge,cData):isTrue(oneJudge,cData))|result;
			}else{
				result=(isNot?!isTrue(oneJudge,cData):isTrue(oneJudge,cData));
			}
		}
		return result;
	}
	
	private boolean isTrue(String oneJudge,CardData cData){
		String[] tempStr=oneJudge.trim().split("=");
		if(tempStr.length!=2){
			LogUtils.e("mark illegal_"+oneJudge);
		}
		String data=cData.getItemData(tempStr[0]),datax=tempStr[1];
		if(datax.startsWith("<")){
			return Integer.parseInt(data.trim())<Integer.parseInt(datax.replaceFirst("<","").trim());
		}else if(datax.startsWith(">")){
			return Integer.parseInt(data.trim())<Integer.parseInt(datax.replaceFirst(">","").trim());
		}else{
			return data.equals(datax);
		}
	}
	
	public MarkRecorder(){}
	public MarkRecorder(CardData cData,String markFile){
		checkMarks(cData,markFile);
	}
}
