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