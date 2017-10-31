package com.poker;

import java.util.ArrayList;
import java.util.List;

public class Common {
	//得到最大相同数
	public static void getMax(Card_index card_index,List<CardLabel> list){
		int count[]=new int[14];//1-13各算一种,王算第14种
		for(int i=0;i<14;i++)
			count[i]=0;
		for(int i=0,len=list.size();i<len;i++){
			if(list.get(i).getColor()==5)
				count[13]++;
			else
				count[list.get(i).getValue()-1]++;
		}
		for(int i=0;i<14;i++)
		{
			switch (count[i]) {
			case 1:
				card_index.a[0].add(i+1);
				break;
			case 2:
				card_index.a[1].add(i+1);
				break;
			case 3:
				card_index.a[2].add(i+1);
				break;
			case 4:
				card_index.a[3].add(i+1);
				break;
			}
		}
	}
	//拆牌
	public static Model getModel(List<CardLabel> list){
		List list2=new ArrayList<CardLabel>(list);
		Model model=new Model();
		CardType.getT4(list2, model); 
		CardType.getT3(list2, model);
		CardType.getT111222(list2, model);
		CardType.getT2(list2, model);
		CardType.getT1122(list2, model);
		CardType.getT123(list2, model);
		CardType.getT1(list2, model);
		return model;
	}
	
	//隐藏之前出过的牌
	public static void hideCards(List<CardLabel> list){
		for(int i=0,len=list.size();i<len;i++){
			list.get(i).setVisible(false);
		}
	}

	//检查牌的是否能出
	public static int checkCards(List<CardLabel> c,List<CardLabel>[] current){
		//找出当前最大的牌是哪个电脑出的,c是点选的牌
		List<CardLabel> currentlist=(current[0].size()>0)?current[0]:current[2];
		
		int cType=CardType.getType(c);
		//如果张数不同直接过滤
		if(cType!=CardType.T4&&c.size()!=currentlist.size())
			return 0;
		//比较我的出牌类型
		if(CardType.getType(c)!= CardType.getType(currentlist))
		{

			return 0;
		}
		//比较出的牌是否要大
		//王炸弹
		if(cType==CardType.T4)
		{
			if(c.size()==2)
				return 1;
			if(currentlist.size()==2)
				return 0;
		}
		//单牌,对子,3带,4炸弹
		if(cType==CardType.T1||cType==CardType.T2||cType==CardType.T3||cType==CardType.T4){
			if(c.get(0).getValue() <= currentlist.get(0).getValue())
			{
				return 0;
			}else {
				return 1;
			}
		}
		//顺子,连队，飞机裸
		if(cType==CardType.T123||cType==CardType.T1122||cType==CardType.T111222)
		{
			if(c.get(0).getValue() <= currentlist.get(0).getValue())
				return 0;
			else 
				return 1;
		}
		//按重复多少排序
		//3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的 
		if(cType==CardType.T31||cType==CardType.T32||cType==CardType.T411||cType==CardType.T422
				||cType==CardType.T11122234||cType==CardType.T1112223344){
			List<CardLabel> a1=Common.getOrder2(c); //我出的牌
			List<CardLabel> a2=Common.getOrder2(currentlist);//当前最大牌
			if(a1.get(0).getValue() < a2.get(0).getValue())
				return 0;
		}
		return 1;
	}
	//按照重复次数排序
	public static List getOrder2(List<CardLabel> list){
		List<CardLabel> list2=new ArrayList<CardLabel>(list);
		List<CardLabel> list3=new ArrayList<CardLabel>();
		List<Integer> list4=new ArrayList<Integer>();
		int len=list2.size();
		int a[]=new int[20];
		for(int i=0;i<20;i++)
			a[i]=0;
		for(int i=0;i<len;i++)
		{
			a[list2.get(i).getValue()]++;
		}
		int max=0;
		for(int i=0;i<20;i++){
			max=0;
			for(int j=19;j>=0;j--){
				if(a[j]>a[max])
					max=j;
			}

			for(int k=0;k<len;k++){
				if(list2.get(k).getValue() == max){
					list3.add(list2.get(k));
				}
			}
			list2.remove(list3);
			a[max]=0;
		}
		return list3;
	}
}
class Card_index{
	List a[]=new ArrayList[4];//单张
}