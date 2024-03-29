package com.megasteelx.exse.items;

import android.content.*;
import android.view.*;
import android.widget.*;
import com.megasteelx.exse.activities.*;
import com.megasteelx.exse.utils.*;
import java.util.*;
import com.megasteelx.exse.*;
import java.io.*;

public class ItemsGroup extends ImageView implements ItemInterface {
	public int groupId = -1;//if is ItemGroup child then ItemGroup Id, else NULL.
	//IDK what will happen when an IG is a child of another IG.
	
    AbsoluteLayout mParent;
	
    Context mContext;
    double mBasesize;

	ItemCore mCore;
	ArrayList<ItemCore[]>childCores= new ArrayList<ItemCore[]>();
	ArrayList<int[]>childIds= new ArrayList<int[]>();
	ArrayList<Integer>childTrans= new ArrayList<Integer>();
	int partNum,childPerPart;
	String[]childNames,childTypes;
	String childStyle="";
	int ctrlBarId;
	int currentPosi=0;
	boolean showButtonFlag=false,isHorizon=false;
	LinearLayout ctrlBar;

	public ItemsGroup(Context context, ItemCore core, int id)
	{
		super(context);
		mCore = core;
		//LogUtils.d(id+"");
		setFocusable(true);
		setFocusableInTouchMode(true);

		setId(id);

		dealExtStyle();
	}
	private void dealExtDef()
	{
		if (mCore.getExtDefine() == null || mCore.getExtDefine().isEmpty())return;
		String tempExtDef=new String(mCore.getExtDefine());
		String[]extDefs=tempExtDef.split(";");
		for (int i=0;i < extDefs.length;i++)
		{
			tempExtDef = extDefs[i];
			String[] extDefKVP=tempExtDef.split(":");
			if (extDefKVP.length != 2)
			{
				LogUtils.e(tempExtDef + "_not expected K-V pair");
			}
			else
			{
				//此处处理extdef键值对（伪）
                //one example of define:
                //*ItemGroup/mgroup/childNum:3;childType:SinglelineText!mText_PictureLoader!mpicture_Touchswitch!mSwitch*
				if (extDefKVP[0].trim().equals("childNum"))
				{
					childPerPart = Integer.parseInt(extDefKVP[1].trim());
				}
				if (extDefKVP[0].trim().equals("childType"))
				{
					String[]childs=extDefKVP[1].trim().split("_");
					childTypes = new String[childs.length];
					childNames = new String[childs.length];
					if (childNames.length != childPerPart)
					{
						LogUtils.e(mCore.getName() + "_unequal defined childs:" + childNames.length + "and childnumber:" + childPerPart);
					}
					else
					{
					    String[] temp;
					    for (int ii=0;ii < childs.length;ii++)
						{
					        temp = childs[ii].split("!");
					        childTypes[ii] = temp[0];
					        childNames[ii] = temp[1];
                            /*ItemCore[] tmp=childCores.get(0);
							 tmp[ii]=((CardEditActivity)mContext).getCard().getItemFromName(temp[1]);
							 if(!childTypes[ii].equals(tmp[ii].getType())){
							 LogUtils.e("child Type_required:"+childTypes[ii]+",found:"+tmp[ii].getType());
							 }
							 ((CardEditActivity)mContext).getCard().removeItemFromName(temp[1]);
							 */
                        }
                    }
				}
				//done.
			}
		}
	}
	private String encodeData()
	{
        partNum = childIds.size();
        childPerPart = childNames.length;
        String data="";
        ItemCore tempCore;
        for (int i=0;i < partNum;i++)
		{
            for (int j=0;j < childPerPart;j++)
			{
                tempCore = childCores.get(i)[j];
                data += //tempCore.getName()+"!"+
					tempCore.getData() + (j != childPerPart - 1 ?"-": "");
            }
            //use #0 core to locate whole part;
            data += ";" + (childTrans.get(i))
				+ (i != partNum - 1 ?"_": "");
        }
        return data;
	}
	private void decodeData(String data, double baseSize)
	{
	    String[]dataSentence=data.split("_"),dataPart,childDatas;
        int extTop=0,extLeft=0;
		
		for (int i=0;i < dataSentence.length;i++)
		{
			setCurrentPosi(i);
			if (i > 0&&(childIds==null||dataSentence.length>childIds.size()))addItem();
            dataPart = dataSentence[i].split(";");
            if (dataPart.length != 2)
			{
                LogUtils.e("ItemGroup decodedata_unaviliable data_" + dataSentence[i]);
                continue;
            }
            childDatas = dataPart[0].split("-");
			childTrans.set(i, Integer.parseInt(dataPart[1].trim()));
			if (!isHorizon)
			{
            	extTop = Integer.parseInt(dataPart[1].trim());
            }
			else
			{
				extLeft = Integer.parseInt(dataPart[1].trim());
            }
			for (int j=0;j < childDatas.length;j++)
			{
                childCores.get(i)[j].data = (childDatas[j]);
                childCores.get(i)[j].top += (mCore.getTop() * 0 + extTop + 0 * ((i > 0 && !isHorizon) ?childCores.get(i - 1)[0].getHeight(): 0));
                childCores.get(i)[j].left += (mCore.getLeft() * 0 + extLeft + 0 * ((i > 0 && isHorizon) ?childCores.get(i - 1)[0].getWidth(): 0));
            }
        }
	}
	private void giveId(int part)
	{
	    for (int i=0;i < childPerPart;i++)
		{
            childIds.get(part)[i] = View.generateViewId();
        }
    }
	private void refreshView(final AbsoluteLayout parent, Context context, double baseSize,String data)
	{
	    int itemId,id;boolean isRedraw=false,isRedrawed=false,isFocusing=false;
		int redawi=0;
		String[]datas=data.split("×");
		partNum=childIds.size();
        for (int i=0;i < partNum;i++)
		{
            for (int j=0;j < childPerPart;j++)
			{
				for (int k=1;k < datas.length - 1;k++)
				{
					isRedraw |= (childCores.get(i)[j].getData().equals(datas[k]));
				}
				isFocusing=childCores.get(i)[j].getData().equals(datas[0]);
				if(isRedraw)redawi=i;
				if (data.equals("all") ||
					data.equals("new") ||
					isRedraw)
				{
					itemId = childIds.get(i)[j];
					id = View.generateViewId();
					if (parent.findViewById(itemId) != null)
					{
						if (data.equals("new"))continue;
						parent.findViewById(itemId).setId(id);
						parent.findViewById(id).setLayoutParams(new AbsoluteLayout.LayoutParams(0, 0, 0, 0));
						//parent.removeView(parent.findViewById(id));
						parent.findViewById(id).setVisibility(View.GONE);
					}
					childCores.get(i)[j].drawView(parent, context, baseSize, childIds.get(i)[j], false);
					//if(isRedraw&&isFocusing){
					//	parent.findViewById(childIds.get(i)[j]).requestFocus();
					//	isFocusing=false;
					//	LogUtils.d(childCores.get(i)[j].getData());
					//}else{
						parent.clearFocus();
					//}
					isRedraw = false;
					isRedrawed=true;
				}
				if(isRedrawed&&i==redawi){
					parent.bringChildToFront(findViewById(childIds.get(i)[j]));
				}
			}
        }
    }
	private void dealExtStyle()
	{
		//读取extstyle
		if (!(mCore.getExtStyle() == null || mCore.getExtStyle().isEmpty()))
		{
			String tempExtStl=new String(mCore.getExtStyle());
			String[]extStls=tempExtStl.split(";");
			for (int i=0;i < extStls.length;i++)
			{
				//注意此时tES代表的变量不同了
				tempExtStl = extStls[i];
				String[] extStlKVP=tempExtStl.split(":");
				if (extStlKVP.length != 2)
				{
					LogUtils.e(tempExtStl + "_not expected K-V pair");
				}
				else
				{
					//此处处理extstl键值对（伪）
					if (extStlKVP[0].trim().equals("childs"))
					{
						childStyle = extStlKVP[1].trim();
					}
					else if (extStlKVP[0].trim().equals("gravity"))
					{
						if (extStlKVP[1].trim().equals("horizontal"))
						{
							isHorizon = true;
						}
						else if (extStlKVP[1].trim().equals("vertical"))
						{
							isHorizon = false;
						}
						else
						{
							LogUtils.e("gravity '" + extStlKVP[1] + "' is not allowed. Legal values are horizontal and vertical");
						}
					}
					else if (extStlKVP[0].trim().equals("groupId"))
					{
						groupId = Integer.parseInt(extStlKVP[1].trim());
					}
					//done.
				}
			}
		}//extstl处理完毕。
	}

	private void setCurrentPosi(int i)
	{
		currentPosi = i;
		try
		{
			TextView pos=((View)getParent()).findViewById(ctrlBarId).findViewById(R.id.igbarpos);
			pos.setText(i + 1 + "");
		}
		catch (Exception e)
		{

		}
	}
	private void removeCurrentChild(Context context, AbsoluteLayout parent, double baseSize, int trans)
	{
		int pos=currentPosi;

		View tempView;
		AbsoluteLayout.LayoutParams tmpLp;
		/*
		 int top=0,bottom=0;
		 if(pos>0){
		 tmpLp=(AbsoluteLayout.LayoutParams)(parent.findViewById(childIds.get(pos-1)[0]).getLayoutParams());
		 top=isHorizon?tmpLp.x+tmpLp.width:tmpLp.y+tmpLp.height;
		 }
		 if(pos<childCores.size()-1){
		 tmpLp=(AbsoluteLayout.LayoutParams)(parent.findViewById(childIds.get(pos+1)[0]).getLayoutParams());
		 bottom=isHorizon?tmpLp.x:tmpLp.y;
		 }
		 tmpLp=(AbsoluteLayout.LayoutParams)(parent.findViewById(childIds.get(pos)[0]).getLayoutParams());
		 if(bottom==0||top+trans*baseSize+(isHorizon?tmpLp.width:tmpLp.height)<=bottom){

		 }else{
		 trans=0;//(int)((bottom-top-(isHorizon?tmpLp.width:tmpLp.height))/baseSize);
		 }
		 */
		int tr=childTrans.get(pos);
		childTrans.set(pos, tr + trans);
		
		ItemCore tmpCore;
		for (int i=0;i < childPerPart;i++)
		{
			tempView = parent.findViewById(childIds.get(pos)[i]);
			tmpCore = childCores.get(pos)[i];
			tmpCore.setLeft(tmpCore.getLeft() + (isHorizon ?trans: 0));
			tmpCore.setTop(tmpCore.getTop() + (isHorizon ?0: trans));
			tmpLp = (AbsoluteLayout.LayoutParams)tempView.getLayoutParams();
			tmpLp.x += isHorizon ?trans * baseSize: 0;
			tmpLp.y += isHorizon ?0: trans * baseSize;
			tempView.setLayoutParams(tmpLp);
			try
			{
				((SinglelineText)tempView).moveStoke();
			}
			catch (Exception e)
			{
				continue;
			}
		}
		returnData(context, encodeData());
	}
	private void addItem()
	{
		//clone last part of childCores BUT generate NEW IDs and NEW location
		try
		{
			partNum++;
			ItemCore[] tempCores=childCores.get(childCores.size() - 1);
			int len=tempCores.length;
			ItemCore[] newCores=new ItemCore[len];
			childIds.add(new int[len]);
			giveId(childIds.size() - 1);
			for (int i=0;i < len;i++)
			{
				newCores[i] = (ItemCore)tempCores[i].clone();
				newCores[i].setTop(tempCores[i].getTop() + (isHorizon ?0: tempCores[0].getHeight()));
				newCores[i].setLeft(tempCores[i].getLeft() + (isHorizon ?tempCores[0].getWidth(): 0));
			}
			childCores.add(newCores);
			childTrans.add(0);
			setCurrentPosi(partNum - 1);

		}
		catch (CloneNotSupportedException e)
		{
			LogUtils.e("clone part failed_" + e.toString());
		}
		catch (Exception e)
		{
			LogUtils.e(e.toString());
		}
	}
	private ArrayList<ArrayList<String>> readGroupMark(Context context)
	{
		//format: {reason,reason_value,result1,result1_extStl,result2,result2_extStl,"0",anti_result1,anti1_extStl,…}
		ArrayList<ArrayList<String>> ret= new ArrayList<ArrayList<String>>();
		ArrayList<String> result= new ArrayList<String>();
		try
		{
			String path=SettingUtils.PATH_SOURCE + "/" + SettingUtils.getCurrentStyle(context) + "/marks/" + mCore.name + ".mrk";
			if (!new File(path).exists())
			{
				LogUtils.w("no mrk-file for group" + mCore.name);
				return null;
			}
			else
			{
				/*String f=FileUtils.FileToString(path);
				 if(f.trim().equals("")){
				 throw new NullPointerException("empty mrk-file");
				 }*/
				String[] str=FilesUtils.FileToLines(path);//f.trim().split("\n");
				String[] seprated,reason,results;
				for (int i=0;i < str.length;i++)
				{
					seprated = str[i].trim().split("->|!>");
					if (seprated.length != 3)continue;
					reason = seprated[0].split("=");
					result.add(reason[0]);
					result.add(reason[1]);
					results = seprated[1].split(",");

					for (int j=0;j < results.length;j++)
					{
						//just not want to use another var
						reason = results[j].replace("}", "").replace("{", "POI").split("POI");
						result.add(reason[0]);
						result.add(reason[1]);
					}
					result.add("0");
					//anti-results
					results = seprated[2].split(";");
					for (int j=0;j < results.length;j++)
					{
						//just not want to use another var
						reason = results[j].replace("}", "").replace("{", "POI").split("POI");
						result.add(reason[0]);
						result.add(reason[1]);
					}
					ret.add(result);
					result = new ArrayList<String>();
				}
				return ret;
			}
		}
		catch (Exception e)
		{
			LogUtils.e(e.toString());
			return null;
		}
	}
	@Override
	public void addToParent(final AbsoluteLayout parent, final Context context, final double baseSize)
	{
		try
		{
			mParent = parent;
			mContext = context;mBasesize = baseSize;
			LogUtils.i("drawing IG@" + baseSize + mCore.width + mCore.height + mCore.left + mCore.top + "@" + parent);
			setImageResource(R.drawable.nu);
			parent.addView(this, new AbsoluteLayout.LayoutParams(
							   (int)(baseSize * mCore.width),
							   (int)(baseSize * mCore.height),
							   (int)(baseSize * mCore.left),
							   (int)(baseSize * mCore.top)
						   ));
			dealExtDef();
			dealExtStyle();
			String[]styleDescs=childStyle.split("%");
			if (styleDescs.length == childPerPart)
			{
				//deal with cores;
				String[] tempString;
				ItemCore[] tempCores=new ItemCore[childPerPart];
				ItemCore tempItem;
				for (int j=0;j < childPerPart;j++)
				{
					tempItem = new ItemCore();
					try
					{
						tempString = styleDescs[j].split("!");
						tempItem.setType(childTypes[j]);
						tempItem.setName(tempString[0].trim());
						tempItem.setHeight(Integer.parseInt(tempString[2].trim()));
						tempItem.setWidth(Integer.parseInt(tempString[1].trim()));
						tempItem.setTop(mCore.top + Integer.parseInt(tempString[4].trim()));
						tempItem.setLeft(mCore.left + Integer.parseInt(tempString[3].trim()));
						tempItem.setExtStyle(tempString.length == 5 ?"groupId:" + getId(): "groupId:" + getId() + ";" + tempString[5].replace('-', ';').replace('_', ':'));
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						LogUtils.w("reading style_" + e.toString());
					}
					catch (NumberFormatException e)
					{
						LogUtils.e("not a number_" + e.getMessage());
					}
					catch (NullPointerException e)
					{

					}
					finally
					{
						tempCores[j] = tempItem;
						//childCores.set(j,tempCores);
						//LogUtils.i(tempItem.getName());
					}
				}
				childCores.add(tempCores);//new ItemCore[childPerPart]);
				childIds.add(new int[childPerPart]);
				childTrans.add(0);
				giveId(0);
				decodeData(mCore.getData(), baseSize);
			}
			else
			{
				LogUtils.e("not equal childs_in stl:" + styleDescs.length + "and dfn:" + childPerPart);
			}
			returnData(context, encodeData());
			dealDataUpdate(parent,context,"all");
			//refreshView(parent,context,baseSize);
		}
		catch (Exception e)
		{
			LogUtils.d(e.toString());
		}
		LayoutInflater barLayoutinf= LayoutInflater.from(context);
		ctrlBar = (LinearLayout)barLayoutinf.inflate(R.layout.itemgroup_bar, null);
		ctrlBarId = View.generateViewId();
		ctrlBar.setId(ctrlBarId);
		parent.addView(ctrlBar, new AbsoluteLayout.LayoutParams(
						   1000,
						   100,
						   0 + (int)(baseSize * mCore.left),
						   -120 + (int)(baseSize * mCore.top)
					   ));
		Button partPlusButton=parent.findViewById(ctrlBarId).findViewById(R.id.igbarplus);
		partPlusButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					addItem();
					returnData(context, encodeData());
					refreshView(parent, context, baseSize,"new");
				}
			});

		Button lastPartMinusButton=parent.findViewById(ctrlBarId).findViewById(R.id.igbarminus);
		lastPartMinusButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					if (childIds.size() > 1)
					{
						partNum--;
						int[] ids=childIds.get(partNum);
						for (int i=0;i < childPerPart;i++)
						{
							//parent.removeView(
							((View)(parent.findViewById(ids[i]))).setVisibility(View.GONE);
						}
						childIds.remove(partNum);
						childCores.remove(partNum);
						childTrans.remove(partNum);
						//refreshView(parent,context,baseSize);
						if(currentPosi==partNum){
							setCurrentPosi(partNum-1);
						}
					}
					else
					{
						Toast.makeText(context, "only one", Toast.LENGTH_SHORT);
					}
				}
			});

		Button transPlus1Button=parent.findViewById(ctrlBarId).findViewById(R.id.igbardown);
		transPlus1Button.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					removeCurrentChild(context, parent, baseSize, 1);
				}
			});
		Button transPlus10Button=parent.findViewById(ctrlBarId).findViewById(R.id.igbardowndown);
		transPlus10Button.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					removeCurrentChild(context, parent, baseSize, 10);
				}
			});
		Button transMinus1Button=parent.findViewById(ctrlBarId).findViewById(R.id.igbarup);
		transMinus1Button.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					removeCurrentChild(context, parent, baseSize, -1);
				}
			});
		Button transMinus10Button=parent.findViewById(ctrlBarId).findViewById(R.id.igbarupup);
		transMinus10Button.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					removeCurrentChild(context, parent, baseSize, -10);
				}
			});
		Button posiChangeButton=parent.findViewById(ctrlBarId).findViewById(R.id.igbarnext);
		posiChangeButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{

					setCurrentPosi(currentPosi < childCores.size() - 1 ?currentPosi + 1: 0);
				}
			});
		setCurrentPosi(childIds.size() - 1);



		((LinearLayout)parent.findViewById(ctrlBarId)).setVisibility(GONE);

		setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View p1, MotionEvent p2)
				{
					if (showButtonFlag)
					{
						parent.findViewById(ctrlBarId)
							.setVisibility(GONE);
						returnData(context, encodeData());
						showButtonFlag = false;
					}
					else
					{
						parent.findViewById(ctrlBarId)
							.setVisibility(VISIBLE);
						parent.bringChildToFront(parent.findViewById(ctrlBarId));
						showButtonFlag = true;
					}

					return true;
				}
			});
		setOnFocusChangeListener(new OnFocusChangeListener(){

				@Override
				public void onFocusChange(View p1, boolean p2)
				{
					if (p2)
					{

					}
					else
					{
						if (showButtonFlag)
						{
							parent.findViewById(ctrlBarId)
								.setVisibility(GONE);
							showButtonFlag = false;
							returnData(context, encodeData());
						}
					}
				}
			});
	}

	public void dealDataUpdate(final AbsoluteLayout parent,Context context, String datua)
	{
		//LogUtils.d(groupId+"get");
		ArrayList<ArrayList<String>> marks=readGroupMark(context);
		ArrayList<String> tmark;
		String tstr;
		ItemCore[] tcores;
		ItemCore tcore;
		int startIndex=0;
		boolean flag=false,actflag=false;
		boolean ischeckedtrue=false,issthchanged=false;
		String changeddata="";
		try
		{
			if (marks != null)
			{
				for (int i=0;i < childCores.size();i++)
				{
					tcores = childCores.get(i);
					for (int j=0;j < marks.size();j++)
					{
						tmark = marks.get(j);
						for (int l=0;l < tcores.length;l++)
						{
							tcore = tcores[l];
							if (tcore.name.equals(tmark.get(0)))
							{
								flag = true;
								//LogUtils.d(tcore.data);LogUtils.d(tmark.get(1));
								startIndex = 2;
								if (tcore.data.equals(tmark.get(1)))
								{
									actflag = true;
								}
								else
								{
									actflag = false;//startIndex=tmark.indexOf("0")+1;
								}
								//LogUtils.d(startIndex + "");
							}
						}
						if (!flag)continue;
						for (int k=startIndex;k < tmark.size();k += 2)
						{
							tstr = tmark.get(k);
							if (tstr.equals("0"))break;
							for (int m=0;m < tcores.length;m++)
							{
								tcore = tcores[m];
								//LogUtils.d(i+";"+m+"|"+ismodified[i][m]);
								if (tcore.name.equals(tstr))
								{//}&&!ismodified[i][m]){
									if (actflag)
									{
										tcore.extStyle = (tcore.extStyle.isEmpty() ?tmark.get(k + 1): tcore.extStyle + ";" + tmark.get(k + 1));
										ischeckedtrue = true;
										issthchanged=true;
										changeddata+=tcore.getData()+"×";
									}
									else
									{
										if (!ischeckedtrue){
											tcore.extStyle = tcore.extStyle.replace(tmark.get(k + 1), "");//StringUtils.replaceLast(tcore.extStyle,tmark.get(k+1),"");
											issthchanged=true;
											changeddata+=tcore.getData()+"×";
										}
									}
									//ismodified[i][m]=true;

								}
							}
						}
						flag = false;
					}
				}
			}
			if(datua.equals("all")){
				refreshView(parent, context, mBasesize,"all");
				
			} else
			if(issthchanged){
				refreshView(parent, context, mBasesize,datua+"×"+changeddata);
			}
			returnData(context,encodeData());
		}
		catch (Exception e)
		{
			//LogUtils.d(e.toString());
		}
		//((CardEditActivity)context).refreshCardView();
	}
	public String getData()
	{
		return encodeData();
	}
	@Override
	public void returnData(Context context, String data)
	{
		if(!mCore.data.equals(data)){
		mCore.data = data;
		((CardEditActivity)context).onReturnData(mCore.getName(), data, groupId);
			
		}
	}

	@Override
	public void reDraw(Context context, ItemCore more, double baseSize)
	{
		mCore = more;
		decodeData(more.getData(), baseSize);
		refreshView(mParent, context, baseSize,"all");
	}

}
