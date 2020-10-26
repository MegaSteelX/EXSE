package com.megasteelx.exse.utils;
import java.io.*;

public class StringUtils
{
	public static String reverse(String src){
		return new StringBuffer(src).reverse().toString();
	}
	public static String replaceLast(String src,String regx,String result){
		String rep=reverse(src);
		rep=rep.replaceFirst(reverse(regx),reverse(result));
		return reverse(rep);
	}
	public static String fixName(String filename, String suffix)
	{
		String filesname=filename;
		while(new File(filesname+suffix).exists()){
			filesname+="-";
		}
		return filesname+suffix;
	}
	public static int longTextLines(String text,int charPerLine){
		String[]textParagraphs=text.split("\n");
		int lines=0;
		for(int i=0;i<textParagraphs.length;i++){
			lines+=1+textParagraphs[i].length()/charPerLine;
		}
		return lines;
	}
}
