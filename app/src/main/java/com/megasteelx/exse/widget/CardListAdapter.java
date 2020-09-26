package com.megasteelx.exse.widget;

import android.content.*;
import android.graphics.*;
import android.widget.*;
import com.megasteelx.exse.*;
import com.megasteelx.exse.other.*;
import com.megasteelx.exse.utils.*;
import java.io.*;
import java.util.*;
import com.megasteelx.exse.activities.*;

public class CardListAdapter extends ListViewAdapter<String>
 {

    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public CardListAdapter(Context context, List<String> datas) {
        super(context, datas, R.layout.card_list_item);
    }

    @Override
    public void convert(ViewHolder holder, String set,int position) {

		CardData listData=new CardData(set);
		
		ImageView listImage=holder.getView(R.id.list_img);
		TextView listTitle=holder.getView(R.id.list_title);
		TextView listText=holder.getView(R.id.list_text);
		
		File imageFile=new File(SettingUtils.PATH_WORKSPACE+"/"+listData.getItemData("image"));
			
		if(imageFile.exists()){
			listImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getPath()));
		}
        listTitle.setText(listData.getItemData("name"));
		listText.setText(listData.getItemData("text"));
		
		if(listTitle.getText().toString().isEmpty()){
			listTitle.setText("未命名卡片");
		}
		holder.getView(R.id.list_item).setTag(R.id.tag_position,position);
		holder.getView(R.id.list_del).setTag(R.id.tag_position,position);
		holder.getView(R.id.list_copy).setTag(R.id.tag_position,position);
		
		holder.getView(R.id.list_item).setOnClickListener(((CardEditActivity)mContext).barListClickListener);
		holder.getView(R.id.list_del).setOnClickListener(((CardEditActivity)mContext).barListClickListener);
		holder.getView(R.id.list_copy).setOnClickListener(((CardEditActivity)mContext).barListClickListener);
		}
}
