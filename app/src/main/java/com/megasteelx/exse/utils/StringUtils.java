package com.megasteelx.exse.utils;
import java.io.*;

public class StringUtils
{
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
