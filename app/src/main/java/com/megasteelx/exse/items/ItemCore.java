package com.megasteelx.exse.items;
import android.content.*;
import com.megasteelx.exse.utils.*;
import java.lang.reflect.*;
import android.widget.*;

public class ItemCore implements Cloneable
{
    //允许克隆类
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    int height;
    int width;
    int top;
    int left;
	String data="not defined";
	String mType;
	String extStyle="";
	String extDefine="";
    String name;
	String filePath=SettingUtils.PATH_SOURCE;

	public void setExtDefine(String extDefine)
	{
		this.extDefine = extDefine;
	}

	public String getExtDefine()
	{
		return extDefine;
	}

	public void setExtStyle(String Style)
	{
		extStyle = Style;
	}

	public String getExtStyle()
	{
		return extStyle;
	}

	public void setType(String type)
	{
		mType = type;
	}

	public String getType()
	{
		return mType;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public String getData()
	{
		return data;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getHeight()
	{
		return height;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getWidth()
	{
		return width;
	}

	public void setTop(int top)
	{
		this.top = top;
	}

	public int getTop()
	{
		return top;
	}

	public void setLeft(int left)
	{
		this.left = left;
	}

	public int getLeft()
	{
		return left;
	}
    public void drawView(AbsoluteLayout Parent,Context context,double baseSize,int id,boolean reDraw){
		try{
		LogUtils.i("start drawing"+mType);
		String className="com.megasteelx.exse.items."+mType;
		String methodName="addToParent",redrawMethod="reDraw";
		//获取方法
		Method m;
		if(reDraw){
			Object obj=Parent.findViewById(id);
			m = obj.getClass().getDeclaredMethod(redrawMethod,Context.class,ItemCore.class,Double.TYPE);
			m.invoke(obj,context,this,baseSize);
		}else{
			Class clz = Class.forName(className);
			Class<?>[] params={Context.class,ItemCore.class,Integer.TYPE};
			Object[] values={context,this,id};
			//构造有参数的构造函数  
			Constructor<?> constructor = clz.getDeclaredConstructor(params);  
			//
			//根据构造函数，传入值生成实例
			Object obj = constructor.newInstance(values);
			
			m = obj.getClass().getDeclaredMethod(methodName, AbsoluteLayout.class,Context.class,Double.TYPE);
			m.invoke(obj,Parent,context,baseSize);
		}
	}catch(Exception e){
		LogUtils.e(mType+"_is not a defined item"+e.toString());
	}
	}
    public void rePlace(int mwidth,int mheight,int mleft,int mtop){
        left=mleft;
        width=mwidth;
        top=mtop;
        height=mheight;
    }
    public ItemCore(){}
    public ItemCore(String type){
		mType=type;
	}
    public ItemCore(String type,String mname,int mwidth,int mheight,int mleft,int mtop){
        mType=type;
		name=mname;
        left=mleft;
        width=mwidth;
        top=mtop;
        height=mheight;
    }

	@Override
	public boolean equals(Object obj)
	{
		ItemCore other=(ItemCore)obj;
		return other.mType.equals(this.mType)&&
		other.data.equals(this.data)&&
		other.height==this.height&&
		other.width==this.width&&
		other.top==this.top&&
		other.left==this.left;
	}
	
}
