package com.megasteelx.exse.utils;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.megasteelx.exse.widget.*;
import java.io.*;

public class OtherUtils
{
	public static String getHexString(int color) {
		String s = "#";
		int colorStr = (color & 0xff000000) | (color & 0x00ff0000) | (color & 0x0000ff00) | (color & 0x000000ff);
		s = s + Integer.toHexString(colorStr);
		return s;
	}
	public static Bitmap convertViewToBitmap(View view){
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int wid=view.getWidth()<view.getMeasuredWidth()?view.getWidth():view.getMeasuredWidth();
		int hei=view.getHeight()<view.getMeasuredHeight()?view.getHeight():view.getMeasuredHeight();
		Bitmap bitmap = Bitmap.createBitmap(wid,hei, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }
	/**
     * 添加媒体库
     * @param context
     * @param path
     */
    public static void updateMediaLibraryInsert(Context context,String path){
        MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
    }
	
	public static boolean SetImageToView(String path,ImageView imgView){
		if(!new File(path).exists())return false;
		imgView.setImageBitmap(BitmapFactory.decodeFile(path));
		return true;
	}
	public static boolean SetImageToView(File file,ImageView imgView){
		return SetImageToView(file.getPath(),imgView);
	}
	public static void InsertIconToText(TextView textView,double bas,String imgPath,int starSize,int levelWidth,int iconSize) {
		String str=textView.getText().toString();
		if(str==null||"".equals(str)) {
			textView.setText("");
			return;
		}
        //预先加入零宽空字符防止全图片位置混乱
        //if(str.replaceAll("<.>","").isEmpty()){
        //    str="\u200C"+str;
        //}
		try{
			//InputStream bitmap=null;
			SpannableString ss = new SpannableString(str);
			Bitmap bit=null;
			//处理显示表情
			String content = str;
			int len = 0;
			int starts = 0;
			int end = 0;
			while(len < content.length()){
				if(content.indexOf("<", starts) != -1 && content.indexOf(">", end) != -1){
					starts = content.indexOf("<", starts);
					end = content.indexOf(">", end);
					String phrase = content.substring(starts,end + 1);
					char pic_name = phrase.charAt(1);

					bit=BitmapFactory.decodeFile(imgPath+"/"+pic_name+".png");
					//	 bitmap=MyApp.getInstance().getAssets().open(face_pic_name);
					//bit=BitmapFactory.decodeStream(bitmap);
					////根据Bitmap对象创建ImageSpan对象						//                      ImageSpan imageSpan=new ImageSpan(this,bit);
					////创建一个SpinnableString对象，以便插入ImageSpan对象封装的图像
					//                      spannableString=new SpannableString("replace");
					////用ImageSpan对象替换replace字符串
					//                      spannableString.setSpan(imageSpan, 0, 15, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					//将图像显示在TextView上

					Drawable drawable = new BitmapDrawable(bit);
					try {
						if (drawable != null) {
							int h=pic_name<'z'&&pic_name>'a'?iconSize:starSize;
							int w=pic_name<'z'&&pic_name>'a'?0:levelWidth-starSize;
							drawable.setBounds((int)(bas*w), 0, (int)(bas*(w+h)), (int)(bas*h));
							CenterAlignImageSpan span = new CenterAlignImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                            //pic_name<'z'&&pic_name>'a'?ImageSpan.ALIGN_BASELINE:ImageSpan.ALIGN_BOTTOM);
							ss.setSpan(span, starts,end + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					starts = end;
					len = end;
					end++;
				}else{
					starts++;
					end++;
					len = end;
				}
			}
			textView.setText(ss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
