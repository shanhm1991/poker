package com.poker;

import java.util.ArrayList;
import java.util.List;

public class Common {
	//拆牌
	public static Model getModel(List<CardLabel> list){
		List<CardLabel> list2=new ArrayList<CardLabel>(list);
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
	public static List<CardLabel> getOrder2(List<CardLabel> list){
		List<CardLabel> list2=new ArrayList<CardLabel>(list);
		List<CardLabel> list3=new ArrayList<CardLabel>();
		int len=list2.size();
		int a[]=new int[20];
		for(int i=0;i<20;i++)
			a[i]=0;
		for(int i=0;i<len;i++)
		{
			a[list2.get(i).value()]++;
		}
		int max=0;
		for(int i=0;i<20;i++){
			max=0;
			for(int j=19;j>=0;j--){
				if(a[j]>a[max])
					max=j;
			}

			for(int k=0;k<len;k++){
				if(list2.get(k).value() == max){
					list3.add(list2.get(k));
				}
			}
			list2.remove(list3);
			a[max]=0;
		}
		return list3;
	}
}
