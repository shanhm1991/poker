package com.poker;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Common {
	//对list排序
	public static void order(List<CardLabel> list){
		Collections.sort(list,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel o1, CardLabel o2) {
				// TODO Auto-generated method stub
				int a1=Integer.parseInt(o1.getName().substring(0, 1));//花色
				int a2=Integer.parseInt(o2.getName().substring(0,1));
				int b1=Integer.parseInt(o1.getName().substring(2,o1.getName().length()));//数值
				int b2=Integer.parseInt(o2.getName().substring(2,o2.getName().length()));
				int flag=0;
				//如果是王的话
				if(a1==5) b1+=100;
				if(a1==5&&b1==1) b1+=50;
				if(a2==5) b2+=100;
				if(a2==5&&b2==1) b2+=50;
				//如果是A或者2
				if(b1==1) b1+=20;
				if(b2==1) b2+=20;
				if(b1==2) b1+=30;
				if(b2==2) b2+=30;
				flag=b2-b1;
				if(flag==0)
					return a2-a1;
				else {
					return flag;
				}
			}
		});
	}
	//重新定位 flag代表电脑1 ,2 或者是我
	public static void rePosition(MainFrame m,List<CardLabel> list,int flag){
		Point p=new Point();
		if(flag==0)
		{
			p.x=50;
			p.y=(450/2)-(list.size()+1)*15/2;
		}
		if(flag==1)
		{//我的排序 _y=450 width=830
			p.x=(800/2)-(list.size()+1)*21/2;
			p.y=450;
		}
		if(flag==2)
		{
			p.x=700;
			p.y=(450/2)-(list.size()+1)*15/2;
		}
		int len=list.size();
		for(int i=0;i<len;i++){
			CardLabel card=list.get(i);
			card.move(p);
			m.container.setComponentZOrder(card, 0);
			if(flag==1)p.x+=21;
			else p.y+=15;

		}
	}
	//地主牌权值，看是否抢地主
	public static int getScore(List<CardLabel> list){
		int count=0;
		for(int i=0,len=list.size();i<len;i++){
			CardLabel card=list.get(i);
			if(card.getName().substring(0, 1).equals("5"))
			{
				//System.out.println(card.name.substring(0, 1));
				count+=5;
			}
			if(card.getName().substring(2, card.getName().length()).equals("2"))
			{
				//System.out.println(2);
				count+=2;
			}
		}
		return count;

	}
	
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