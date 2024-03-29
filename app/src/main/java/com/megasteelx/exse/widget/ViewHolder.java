package com.megasteelx.exse.widget;


import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.megasteelx.exse.*;

/**
 * Created by smyhvae on 2015/5/4.
 */
public class ViewHolder
{

	private SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	public ViewHolder(Context context, ViewGroup parent, int layoutId, int position)
	{
		this.mPosition = position;
		this.mViews = new SparseArray<View>();

		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);

		mConvertView.setTag(R.id.tag_self,this);

	}

	public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position)
	{
		if (convertView == null)
		{
			return new ViewHolder(context, parent, layoutId, position);
		}
		else
		{
			ViewHolder holder = (ViewHolder) convertView.getTag(R.id.tag_self);
			holder.mPosition = position; //即使ViewHolder是复用的，但是position记得更新一下
			return holder;
		}
	}

	/*
	 通过viewId获取控件
	 */
	//使用的是泛型T,返回的是View的子类
	public <T extends View> T getView(int viewId)
	{
		View view = mViews.get(viewId);

		if (view == null)
		{
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}

		return (T) view;
	}

	public View getConvertView()
	{
		return mConvertView;
	}

}

