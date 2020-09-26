package com.megasteelx.exse.widget;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

/**
 * Created by smyhvae on 2015/5/4.
 * 通用的ListView的BaseAdapter，所有的ListView的自定义adapter都可以继承这个类哦
 */
public abstract class ListViewAdapter<T> extends BaseAdapter {

    //为了让子类访问，于是将属性设置为protected
    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private int layoutId; //不同的ListView的item布局肯能不同，所以要把布局单独提取出来

    public ListViewAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
		//Log.e("","getcount"+mDatas.size());
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
		//Log.e("","posion"+position);
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       	//Log.e("","getview");
		//初始化ViewHolder,使用通用的ViewHolder，一行代码就搞定ViewHolder的初始化咯
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);//layoutId就是单个item的布局

        convert(holder, getItem(position),position);
        return holder.getConvertView(); //这一行的代码要注意了
    }

    //将convert方法公布出去
    public abstract void convert(ViewHolder holder, T t, int position);

}
