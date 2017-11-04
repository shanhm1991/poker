package com.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CardType {
	/**
	 * 不出
	 */
	public static final int T0 = 0;

	/**
	 * 单张
	 */
	public static final int T1 = 1;

	/**
	 * 对子
	 */
	public static final int T2 = 2;

	/**
	 * 3不带
	 */
	public static final int T3 = 3;

	/**
	 * 炸弹
	 */
	public static final int T4 = 4;

	/**
	 * 3带1
	 */
	public static final int T31 = 5;

	/**
	 * 3带2
	 */
	public static final int T32 = 6;

	/**
	 * 飞机
	 */
	public static final int T33 = 11;

	/**
	 * 顺子
	 */
	public static final int T123 = 9;

	/**
	 * 连对
	 */
	public static final int T22 = 10;

	/**
	 * 飞机带单牌
	 */
	public static final int T3312 = 12;

	/**
	 * 飞机带对子
	 */
	public static final int T3322 = 13;

	public List<CardLabel> listT1 = new ArrayList<CardLabel>();

	public List<CardLabel> listT2 = new ArrayList<CardLabel>();

	public List<CardLabel> listT3 = new ArrayList<CardLabel>();



	public static void getT123(List<CardLabel> list,Think model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		if(list.size()>0&&(list.get(0).getValue()<7 ||list.get(list.size()-1).getValue()>10))
			return;
		if(list.size()<5)
			return;
		for(int i=0,len=list.size();i<len;i++){
			int k=i;
			for(int j=i;j<len;j++){
				if(list.get(i).getValue() - list.get(j).getValue()==j-i)
				{
					k=j;
				}
			}
			if(k-i>=4){
				String s="";
				for(int j=i;j<k;j++){
					s+=list.get(j).getName()+",";
					del.add(list.get(j));
				}
				s+=list.get(k).getName();
				del.add(list.get(k));
				model.a123.add(s);
				i=k;
			}
		}
		list.removeAll(del);
	}

	//拆双顺
	public static void getT1122(List<CardLabel> list,Think model){
		List<String> del=new ArrayList<String>();//要删除的Cards
		//从model里面的对子找
		List<String> l=model.a2;
		if(l.size()<3)
			return ;
		Integer s[]=new Integer[l.size()];
		for(int i=0,len=l.size();i<len;i++){
			String []name=l.get(i).split(",");
			s[i]=Integer.parseInt(name[0].substring(2,name[0].length()));
		}
		//s0,1,2,3,4  13,9,8,7,6
		for(int i=0,len=l.size();i<len;i++){
			int k=i;
			for(int j=i;j<len;j++){
				if(s[i]-s[j]==j-i)
					k=j;
			}
			if(k-i>=2){//说明从i到k是连队 k=4 i=1
				String ss="";
				for(int j=i;j<k;j++){
					ss+=l.get(j)+",";
					del.add(l.get(j));
				}
				ss+=l.get(k);
				model.a112233.add(ss);
				del.add(l.get(k));
				i=k;
			}
		}
		l.removeAll(del);
	}

	public static void getT111222(List<CardLabel> list,Think model){
		List<String> del=new ArrayList<String>();//要删除的Cards
		//从model里面的3带找
		List<String> l=model.a3;
		if(l.size()<2)
			return ;
		Integer s[]=new Integer[l.size()];
		for(int i=0,len=l.size();i<len;i++){
			String []name=l.get(i).split(",");
			s[i]=Integer.parseInt(name[0].substring(2,name[0].length()));
		}
		for(int i=0,len=l.size();i<len;i++){
			int k=i;
			for(int j=i;j<len;j++){
				if(s[i]-s[j]==j-i)
					k=j;
			}
			if(k!=i){//说明从i到k是飞机
				String ss="";
				for(int j=i;j<k;j++){
					ss+=l.get(j)+",";
					del.add(l.get(j));
				}
				ss+=l.get(k);
				model.a111222.add(ss);
				del.add(l.get(k));
				i=k;
			}
		}
		l.removeAll(del);
	}

	public static void getT4(List<CardLabel> list,Think model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		//王炸
		if(list.size()>=2 &&list.get(0).getColor()==5 && list.get(1).getColor()==5)
		{
			model.a4.add(list.get(0).getName()+","+list.get(1).getName()); //按名字加入
			del.add(list.get(0));
			del.add(list.get(1));
		}
		//如果王不构成炸弹咋先拆单
		if(list.get(0).getColor()==5 && list.get(1).getColor()!=5)
		{
			del.add(list.get(0));
			model.a1.add(list.get(0).getName());
		}
		list.removeAll(del);
		//一般的炸弹
		for(int i=0,len=list.size();i<len;i++){
			if(i+3<len && list.get(i).getValue() == list.get(i+3).getValue())
			{
				String s=list.get(i).getName()+",";
				s+=list.get(i+1).getName()+",";
				s+=list.get(i+2).getName()+",";
				s+=list.get(i+3).getName();
				model.a4.add(s);
				for(int j=i;j<=i+3;j++)
					del.add(list.get(j));
				i=i+3;
			}
		}
		list.removeAll(del);
	}

	//拆3带
	public static void getT3(List<CardLabel> list,Think model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		//连续3张相同
		for(int i=0,len=list.size();i<len;i++){
			if(i+2<len&&list.get(i).getValue() == list.get(i+2).getValue())
			{
				String s=list.get(i).getName()+",";
				s+=list.get(i+1).getName()+",";
				s+=list.get(i+2).getName();
				model.a3.add(s);
				for(int j=i;j<=i+2;j++)
					del.add(list.get(j));
				i=i+2;
			}
		}
		list.removeAll(del);
	}

	//拆对子
	public static void getT2(List<CardLabel> list,Think model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		//连续2张相同
		for(int i=0,len=list.size();i<len;i++){
			if(i+1<len&& list.get(i).getValue() == list.get(i+1).getValue())
			{
				String s=list.get(i).getName()+",";
				s+=list.get(i+1).getName();
				model.a2.add(s);
				for(int j=i;j<=i+1;j++)
					del.add(list.get(j));
				i=i+1;
			}
		}
		list.removeAll(del);
	}

	//拆单牌
	public static void getT1(List<CardLabel> list,Think model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		//1
		for(int i=0,len=list.size();i<len;i++){
			model.a1.add(list.get(i).getName());
			del.add(list.get(i));
		}
		list.removeAll(del);
	}





	public void listType(List<CardLabel> list){
		Map<CardLabel,Integer> map = new HashMap<CardLabel,Integer>();
		for(CardLabel card : list){
			Integer count = map.get(card);
			if(count == null){
				map.put(card,1);
			}else{
				map.put(card,++count);
			}
		}
		Iterator<Entry<CardLabel, Integer>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<CardLabel, Integer> entry = it.next();
			if(entry.getValue() == 1){
				listT1.add(entry.getKey());
			}else if(entry.getValue() == 2){
				listT2.add(entry.getKey());
			}else if(entry.getValue() == 3){
				listT3.add(entry.getKey());
			}
		}
	}

	public int cValue(List<CardLabel> list){
		Collections.sort(list,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel c1, CardLabel c2) {
				return c2.getValue() - c1.getValue();
			}
		});
		if(list.get(0).getValue() - list.get(list.size() - 1).getValue() == list.size() - 1){
			return list.get(0).getValue();
		}
		//A字顺
		if(list.contains(new CardLabel(null,"1-1",false))){
			Collections.sort(list,new Comparator<CardLabel>() {
				@Override
				public int compare(CardLabel c1, CardLabel c2) {
					return c2.getContinueValue() - c1.getContinueValue();
				}
			});
			return list.get(0).getContinueValue();
		}
		return 0;
	}


}
