package com.megasteelx.exse.items;
import android.annotation.SuppressLint;
import android.content.*;
import android.widget.*;
import android.graphics.*;
import com.megasteelx.exse.activities.*;
import com.megasteelx.exse.utils.*;
import android.util.*;
import android.text.*;
import java.io.*;
import android.view.*;
import android.widget.ImageView.*;
import android.widget.RadioGroup.*;
import java.lang.reflect.*;
import android.view.ViewGroup.*;

@SuppressLint("AppCompatCustomView")
public class SinglelineText extends EditText implements ItemInterface
{
	public int groupId = -1;//if is ItemGroup child then ItemGroup Id, else NULL.

	ItemCore mCore;
	int textSize=10,icon1Size=10,icon2Size=10,icon1Width=20;
	Typeface mTypeface=null;
	String typeName="";
	int textColor=0xFFFFFF;
	int vertical=0;
	int stokeColor=0x000000;
	int stokeWidth=0,stokeTextId;
	boolean withStoke=false;
	TextView stokeText;
	
	boolean withShadow=false;
	int shadowColor=0x000000;
	float shadowDx=0f;
	float shadowDy=0f;
	float shadowRidus=0f;
	//boolean m_bDrawSideLine=true;//false;
	boolean withImgSpan=false,haveImgSpan=false;
	//with:是否显示,have:是否存在
	int imgChooserId;
	public SinglelineText(Context context,ItemCore core,int id){
		super(context);
		setBackground(null);
		setPadding(0,0,0,0);
		mCore=core;
		setId(id);
		//获取字体
		File fontdir=new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/fonts");
		String namedfn="names.dfn";
		String[]namespair=FileUtils.FileToLines(fontdir.getPath()+"/"+namedfn);//.trim().split("\n");
		boolean nameReadFlag=false;
		for(int i=0;i<namespair.length;i++){
			if(namespair[i].startsWith(mCore.getName())){
				namedfn=namespair[i].replace(mCore.getName()+"=","");
				nameReadFlag=true;
				break;
			}
		}
		if(nameReadFlag){
			typeName=namedfn;
			mTypeface=Typeface.createFromFile(fontdir.getPath()+"/"+namedfn);
		}
		dealExtStyle();
	}
	private void dealExtStyle(){
		//读取extstyle
		if(!(mCore.getExtStyle()==null||mCore.getExtStyle().isEmpty())){
			String tempExtStl=new String(mCore.getExtStyle());
			String[]extStls=tempExtStl.split(";");
			for(int i=0;i<extStls.length;i++){
				//注意此时tES代表的变量不同了
				tempExtStl=extStls[i];
				String[] extStlKVP=tempExtStl.split(":");
				if(extStlKVP.length!=2){
					LogUtils.e(tempExtStl+"_not expected K-V pair");
				}else{
					//此处处理extstl键值对（伪）
					LogUtils.d(tempExtStl);
					if(extStlKVP[0].trim().equals("groupId")){
						groupId=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("textsize")){
						textSize=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("color")){
						textColor=Integer.parseInt(extStlKVP[1].trim().replace("#",""),16);
					}
					if(extStlKVP[0].trim().equals("gravity")){
						String temp=extStlKVP[1];
						setGravity((temp.equals("R")?Gravity.RIGHT:temp.equals("L")?Gravity.LEFT:Gravity.CENTER)|Gravity.CENTER_VERTICAL);
					}
					if(extStlKVP[0].trim().equals("withicons")){
						String temp=extStlKVP[1];
						withImgSpan=Boolean.parseBoolean(temp.trim());
					}
					if(extStlKVP[0].trim().equals("iconsize1")){
						String temp=extStlKVP[1];
						icon1Size=Integer.parseInt(temp.trim());
					}
					if(extStlKVP[0].trim().equals("iconsize2")){
						String temp=extStlKVP[1];
						icon2Size=Integer.parseInt(temp.trim());
					}
					if(extStlKVP[0].trim().equals("icon1width")){
						String temp=extStlKVP[1];
						icon1Width=Integer.parseInt(temp.trim());
					}
					if(extStlKVP[0].trim().equals("withstoke")){
						String temp=extStlKVP[1];
						withStoke=Boolean.parseBoolean(temp.trim());
					}
					if(extStlKVP[0].trim().equals("stokewidth")){
						String temp=extStlKVP[1];
						stokeWidth=Integer.parseInt(temp.trim());
					}
					if(extStlKVP[0].trim().equals("stokecolor")){
						String temp=extStlKVP[1];
						stokeColor=Integer.parseInt(temp.replace("#","").trim(),16);
					}
					if(extStlKVP[0].trim().equals("vertical")){
						vertical=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("withshadow")){
						withShadow=Boolean.parseBoolean(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("shadowx")){
						shadowDx=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("shadowy")){
						shadowDy=Integer.parseInt(extStlKVP[1].trim());
					}
					if(extStlKVP[0].trim().equals("shadowr")){
						shadowRidus=Integer.parseInt(extStlKVP[1].trim());
					}
					//done.
				}
			}
		}//extstl处理完毕。
	}
	private void changeSpan(double baseSize){
		OtherUtils.InsertIconToText(this,baseSize,SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+mCore.getName(),icon1Size,icon1Width,icon2Size);
		if(withStoke){
			stokeText=(EditText)((AbsoluteLayout)getParent()).findViewById(stokeTextId);
			OtherUtils.InsertIconToText(stokeText,baseSize,SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+mCore.getName(),icon1Size,icon1Width,icon2Size);
		}
	}
	@Override
	public void addToParent(final AbsoluteLayout parent, final Context context, final double baseSize)
	{
		LogUtils.i("drawing ST@"+baseSize+mCore.width+mCore.height+mCore.left+mCore.top+"@"+parent);
		
		if(withStoke){
			stokeTextId=View.generateViewId();
			stokeText=new TextView(context);
			stokeText.setId(stokeTextId);
			stokeText.setText(mCore.getData());
			TextPaint stokePaint=stokeText.getPaint();
			stokePaint.setStyle(Paint.Style.STROKE);
			stokeWidth=(int)(stokeWidth*baseSize);
			stokePaint.setStrokeWidth(stokeWidth);
			parent.addView(stokeText,new AbsoluteLayout.LayoutParams(
							   (int)(baseSize*mCore.width),
							   (int)(baseSize*mCore.height),
							   (int)(baseSize*mCore.left),
							   (int)(baseSize*mCore.top)
						   ));
			if(mTypeface!=null){
				stokeText.setTypeface(mTypeface);
			}
			stokeText.setSingleLine(true);
			stokeText.setGravity(getGravity());
			stokeText.setTextSize(TypedValue.COMPLEX_UNIT_PX,getTextSize());
			stokeText.setTextColor(0xFF000000+stokeColor);
		}
		parent.addView(this,new AbsoluteLayout.LayoutParams(
						   (int)(baseSize*mCore.width),
						   (int)(baseSize*mCore.height),
						   (int)(baseSize*mCore.left),
						   (int)(baseSize*mCore.top)
					   ));


		setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(textSize*baseSize));
		setText(mCore.getData());
		
		if(mTypeface!=null){
			setTypeface(mTypeface);
		}else{
			LogUtils.w(mCore.getName()+"_using system typeface");
		}
		setTextColor(0xFF000000+textColor);
		setSingleLine(true);
		
		//deal shadow
		if(withShadow){
			shadowDx*=baseSize;
			shadowDy*=baseSize;
			shadowRidus*=baseSize;
			shadowColor+=0xFF000000;
			setShadowLayer(shadowRidus,shadowDx,shadowDy,shadowColor);
		}
		//处理图文混排
		
		String imgPath=SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/"+mCore.getName();
		if(new File(imgPath).isDirectory()){
			haveImgSpan=true;
			imgChooserId=View.generateViewId();
			HorizontalScrollView imgChooserParent=new HorizontalScrollView(context);
			imgChooserParent.setId(imgChooserId);
			parent.addView(imgChooserParent,new AbsoluteLayout.LayoutParams(
							   (int)(baseSize*mCore.width),
							   100,//(int)(baseSize*mCore.height),
							   (int)(baseSize*mCore.left),
							   (int)(baseSize*(mCore.top+mCore.height))
						   ));
			imgChooserParent.setBackgroundColor(0xFFFFFFFF);
			LinearLayout imgChooser=new LinearLayout(context);
			imgChooserParent.addView(imgChooser);
			imgChooser.setOrientation(LinearLayout.HORIZONTAL);
			imgChooser.setGravity(Gravity.LEFT|Gravity.CENTER);
			String[]ImgNames=new File(imgPath).list(new FilenameFilter(){
					@Override
					public boolean accept(File p1, String p2)
					{
						return p2.endsWith(".png")&&p2.length()==5;
					}
				});
			ImageView.OnClickListener imgClickListener=new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					String span="<"+p1.getTag()+">";
					int index = getSelectionStart();
					if(getText().toString().length()>index&&
					 getText().toString().charAt(index)=='>')index++;
					Editable editable = getText();  
					editable.insert(index, span);
					changeSpan(baseSize);
					setSelection(index+3);
				}
			};
			for(int i=0;i<ImgNames.length;i++){
				ImageView oneImg=new ImageView(context);
				oneImg.setImageBitmap(BitmapFactory.decodeFile(imgPath+"/"+ImgNames[i]));
				oneImg.setScaleType(ScaleType.FIT_CENTER);
				oneImg.setTag(ImgNames[i].charAt(0)+"");
				oneImg.setOnClickListener(imgClickListener);
				imgChooser.addView(oneImg);
				ViewGroup.LayoutParams imgparams=oneImg.getLayoutParams();
				imgparams.height=(baseSize*mCore.height<100?(int)(baseSize*mCore.height):100);
				imgparams.width=(int)(imgparams.height*1.2);
				oneImg.setLayoutParams(imgparams);
			}
			imgChooserParent.setVisibility(View.GONE);
		}else if(withImgSpan){
			LogUtils.w(mCore.getName()+"_no icon(s) found to add");
		}
		
		//finished.
		//deal rotation
		if(!withImgSpan)fixWidth(baseSize);
		else changeSpan(baseSize);
		if(vertical>0){
			if(vertical==1)setRotation(90);
			else{
			setSingleLine(false);
			setMaxEms(1);
			}
			if(withStoke){
				if(vertical==1)setRotation(90);
				else{
					stokeText.setSingleLine(false);
					stokeText.setMaxEms(1);
				}
				if(!typeName.isEmpty()){
					File aiteTypeFile=new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/fonts/@"+typeName);
					if(aiteTypeFile.exists()){
						mTypeface=Typeface.createFromFile(aiteTypeFile);
						setTypeface(mTypeface);
						if(withStoke){
							stokeText.setTypeface(mTypeface);
						}
					}else{
						LogUtils.e(typeName+"no @font for vertical text exists");
					}
				}
			}else{
				setRotation(0);
				if(withStoke){
					stokeText.setRotation(0);
				}
			}
			if(!typeName.isEmpty()){
				File TypeFile=new File(SettingUtils.PATH_SOURCE+"/"+SettingUtils.CARD_SET_STYLE+"/fonts/"+typeName);
				mTypeface=Typeface.createFromFile(TypeFile);
				setTypeface(mTypeface);
				if(withStoke){
					stokeText.setTypeface(mTypeface);
				}
			}		
		}
		addTextChangedListener(new TextWatcher(){

				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TO-DO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					if(!p1.equals("")&&!withImgSpan){fixWidth(baseSize);}
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					if(withStoke){
						stokeText=(TextView) ((AbsoluteLayout)getParent()).findViewById(stokeTextId);
						stokeText.setText(getText());
					}
					returnData(context,getText().toString().isEmpty()?"NULL":getText().toString());
				}
			});
		setOnFocusChangeListener(new OnFocusChangeListener(){

				@Override
				public void onFocusChange(View p1, boolean p2)
				{
					if(haveImgSpan){
						View v=parent.findViewById(imgChooserId);
						if(p2&&withImgSpan){
							v.setVisibility(View.VISIBLE);
							v.bringToFront();
						}else{
						v.setVisibility(View.GONE);
						}
					}
					returnData(context,getText().toString());
				}
			});
		
	}

	@Override
	public void returnData(Context context, String data)
	{
		mCore.data=data;
		((CardEditActivity)context).onReturnData(mCore.getName(),data,groupId);
	}
	@Override
	public void reDraw(Context context,ItemCore mCore, double baseSize)
	{
		this.mCore=mCore;
		setLayoutParams(new AbsoluteLayout.LayoutParams(
							(int)(baseSize*mCore.width),
							(int)(baseSize*mCore.height),
							(int)(baseSize*mCore.left),
							(int)(baseSize*mCore.top)
						));
		dealExtStyle();
		setTextColor(0xFF000000+textColor);
		setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(textSize*baseSize));
		if(withStoke){
			stokeText=(TextView) ((AbsoluteLayout)getParent()).findViewById(stokeTextId);
			stokeText.setLayoutParams(new AbsoluteLayout.LayoutParams(
								(int)(baseSize*mCore.width),
								(int)(baseSize*mCore.height),
								(int)(baseSize*mCore.left),
								(int)(baseSize*mCore.top)
							));
			stokeText.setTextSize(TypedValue.COMPLEX_UNIT_PX,getTextSize());
		}
		if(haveImgSpan){
			View v=((AbsoluteLayout)getParent()).findViewById(imgChooserId);
			v.setLayoutParams(new AbsoluteLayout.LayoutParams(
										  (int)(baseSize*mCore.width),
										  (int)(baseSize*mCore.height),
										  (int)(baseSize*mCore.left),
										  (int)(baseSize*(mCore.top+mCore.height))
									  ));
		}
		if(!withImgSpan){fixWidth(baseSize);}
	}
	private void fixWidth(double baseSize){
		//setBackgroundColor(Color.MAGENTA);
		setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)(baseSize*(double)textSize));
		if(vertical<2){
		//调整横向宽度(先缩小字号，再纵向拉伸)
			while(getLayoutParams().width<getPaint().measureText(getText().toString()))
				setTextSize(TypedValue.COMPLEX_UNIT_PX,getTextSize()-1);
       		float real_size=(float)(textSize*baseSize);
        	setScaleY(real_size/getTextSize());
			if(withStoke){
				stokeText=(TextView) ((AbsoluteLayout)getParent()).findViewById(stokeTextId);
				stokeText.setTextSize(TypedValue.COMPLEX_UNIT_PX,getTextSize());
				stokeText.setScaleY(getScaleY());
			}
		}else{
			float u=1,p=0.992f;
			int tim=0;
			setLineSpacing(0,1);
			while(getLayoutParams().height<(fontheight(getTextSize()))*u*(stokeWidth+textSize)/textSize*getText().toString().replace("\n","").length()){
				setTextSize(TypedValue.COMPLEX_UNIT_PX,getTextSize()-1);
				u*=p;tim++;
				setLineSpacing(0,u);
			}
       		//setMaxEms(1);
			float real_size=(float)(textSize*baseSize);
        	//setScaleX(real_size/getTextSize());
			if(withStoke){
				stokeText=(TextView) ((AbsoluteLayout)getParent()).findViewById(stokeTextId);
				stokeText.setTextSize(TypedValue.COMPLEX_UNIT_PX,getTextSize());
				stokeText.setMaxEms(1);
				stokeText.setLineSpacing(0,(float)Math.pow(p+0*1/textSize*(stokeWidth+textSize),tim));//getLineSpacingExtra(),getLineSpacingMultiplier());
				//stokeText.setScaleX(getScaleY());
			}
		}
	}
	private int fontheight(float size){
		Paint p= new Paint();
		p.setTextSize(size);
		Paint.FontMetrics fm=p.getFontMetrics();
		return(int)Math.ceil(fm.descent-fm.top)+2;
	}
	protected void moveStoke(){
		if(withStoke){
			stokeText=((AbsoluteLayout)getParent()).findViewById(stokeTextId);
			AbsoluteLayout.LayoutParams pms =(AbsoluteLayout.LayoutParams)stokeText.getLayoutParams();
			AbsoluteLayout.LayoutParams pms1=(AbsoluteLayout.LayoutParams)getLayoutParams();
			pms.x=pms1.x;
			pms.y=pms1.y;
			
		}
	}
	protected void removeStroke(){
		if(withStoke){
			stokeText=((AbsoluteLayout)getParent()).findViewById(stokeTextId);
			stokeText.setText("");
		}
	}
	/*
	private TextPaint m_TextPaint;
	@Override
	protected void onDraw(Canvas canvas) {
		if (m_bDrawSideLine) {
			m_TextPaint=getPaint();
			int color=m_TextPaint.getColor();
			m_TextPaint.setTextAlign(Paint.Align.LEFT);
			// 描外层
			//super.setTextColor(Color.BLUE); // 不能直接这么设，如此会导致递归
			setTextColorUseReflection(Color.BLUE);
			//m_TextPaint.setColor(Color.BLUE);
			m_TextPaint.setStrokeWidth(3);  // 描边宽度
			m_TextPaint.setStyle(Paint.Style.STROKE); //描边种类
			m_TextPaint.setFakeBoldText(true); // 外层text采用粗体
			m_TextPaint.setShadowLayer(0, 0, 0, 0); //字体的阴影效果，可以忽略
			float viewLenth=this.getWidth();
			canvas.drawText(getText().toString(),
			getGravity()==(Gravity.LEFT|Gravity.CENTER_VERTICAL)?0:getGravity()==(Gravity.RIGHT|Gravity.CENTER_VERTICAL)?viewLenth:0.5f*viewLenth,
			getBaseline(),m_TextPaint);
			//drawText(canvas);
			//setWillNotDraw(false);
			//怕是只能super1次吧？

			// 描内层，恢复原先的画笔

			//super.setTextColor(Color.BLUE); // 不能直接这么设，如此会导致递归  
			setTextColorUseReflection(Color.RED);
			//m_TextPaint.setColor(Color.RED);
			//m_TextPaint.setStrokeWidth(0);                         
			m_TextPaint.setStyle(Paint.Style.FILL);                         
			m_TextPaint.setFakeBoldText(false);                        
			m_TextPaint.setShadowLayer(0, 0, 0, 0);                         
		}
		drawText(canvas);
	}
	private void drawText(Canvas canvas){
		super.onDraw(canvas);
		
	}
	private void setTextColorUseReflection(int color) {
		Field textColorField;
	    try {
			textColorField = TextView.class.getDeclaredField("mCurTextColor");
			textColorField.setAccessible(true);
			textColorField.set(this,color);
			textColorField.setAccessible(false);
	    } catch (NoSuchFieldException e) {
			e.printStackTrace();
	    } catch (IllegalArgumentException e) {
			e.printStackTrace();
	    } catch (IllegalAccessException e) {
			e.printStackTrace();
	    }
		m_TextPaint.setColor(color);
	}
	*//*
	private TextPaint paint;
    private int mInnerColor=Color.BLUE;
    private int mOuterColor=Color.GRAY;
    private int mStrokeWidth = 3;
    private int bottomY=0;
    private TextGravity alignStyle = TextGravity.Left;
    private enum TextGravity { Left, Center, Right }

    @Override
    protected void onDraw(Canvas canvas) {
        paint = this.getPaint();
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        bottomY= (int) -fontMetrics.top;
		// 描外层
		if(m_bDrawSideLine){
        setTextColorUseReflection(mOuterColor);
        paint.setStrokeWidth(mStrokeWidth); // 描边宽度
        paint.setStyle(Paint.Style.FILL_AND_STROKE); // 描边种类
        paint.setFakeBoldText(true); // 外层text采用粗体
        drawText(canvas);
		}
        // 描内层，恢复原先的画笔
        setTextColorUseReflection(mInnerColor);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        paint.setFakeBoldText(false);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        int dx = mStrokeWidth;
        paint.setTextAlign(Paint.Align.LEFT);
        switch (alignStyle) {
            case Center:
                dx = getWidth() / 2;
                paint.setTextAlign(Paint.Align.CENTER);
                break;
            case Right:
                dx = getWidth() - mStrokeWidth;
                paint.setTextAlign(Paint.Align.RIGHT);
                break;
        }
        canvas.drawText(getText().toString(), dx, bottomY, paint);
    }


    /**
     * 使用反射的方法进行字体颜色的设置
     * @param color
    
    private void setTextColorUseReflection(int color) {
        Field textColorField;
        try {
            textColorField = TextView.class.getDeclaredField("mCurTextColor");
            textColorField.setAccessible(true);
            textColorField.set(this, color);
            textColorField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        paint.setColor(color);
    }
	*/
}
