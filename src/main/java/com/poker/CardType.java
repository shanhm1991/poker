package com.poker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CardType {
	/**
	 * 不出
	 */
	public static final int T0 = 0;

	/**
	 * 单排
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
	 * 4带2
	 */
	public static final int T411 = 7;

	/**
	 * 4带2对
	 */
	public static final int T422 = 8;

	/**
	 * 顺子
	 */
	public static final int T123 = 9;

	/**
	 * 连对
	 */
	public static final int T1122 = 10;

	/**
	 * 飞机
	 */
	public static final int T111222 = 11;

	/**
	 * 飞机带单牌
	 */
	public static final int T11122234 = 12;

	/**
	 * 飞机带对子
	 */
	public static final int T1112223344 = 13;

	/**
	 * 获取牌型
	 */
	public static int getType(List<CardLabel> cardList){
//		int cardListSize = cardList.size();
//		Map<String,Integer> cardMap = new HashMap<String,Integer>();
//		for(CardLabel card : cardList){
//			String value = String.valueOf(card.value());
//			if(cardMap.get(value) == null) {
//				cardMap.put(value, 1);
//			}else {
//				cardMap.put(value,(cardMap.get(value)).intValue() + 1);
//			}
//		} 
//		if(cardMap.size() == 1) {
//			switch(cardListSize) {
//			case 1:
//				return T1;
//			case 2:
//				return T2;
//			case 3:
//				return T3;
//			case 4:
//				return T4;
//			}
//		}
//		if(cardMap.size() == 2 && cardMap.containsValue(3)) {
//			switch(cardListSize) {
//			case 4:
//				return T31; 
//			case 5:
//				return T32;
//			}
//		}
//		if(cardList.size() == cardMap.size() 
//				&& (cardList.get(0).value() - cardList.get(cardListSize-1).value() == cardListSize-1)){
//			
//			
//			
//		}
//		
		
		
		
		
		
		
		
		int size = cardList.size();
		//4张以下：单张、对子、3不带、3带1、炸弹、王炸
		if(size <= 4){	
			//已经排过序，第一张与最后一张相同代表所有都相同
			if(cardList.size()>0 && cardList.get(0).value() == cardList.get(size-1).value()){
				switch (size) {
				case 1:
					return T1;
				case 2:
					return T2;
				case 3:
					return T3;
				case 4:
					return T4;
				}
			}
			//王炸 TODO
			if(size==2 && cardList.get(1).color() == 5)
				return T2;

			//当第一个和最后个不同时,3带1
			if(size==4 &&((cardList.get(0).value() == cardList.get(size-2).value())||
					cardList.get(1).value()==cardList.get(size-1).value())){
				return T31;
			}

			return T0;
		}
		
		

		//当5张以上时，顺子，3带2，飞机，2顺，4带2等等
		if(size >= 5){

			//现在按相同数字最大出现次数
			Card_index card_index=new Card_index();
			for(int i=0;i<4;i++){
				card_index.a[i]=new ArrayList<Integer>();
			}
			//求出各种数字出现频率
			getMax(card_index,cardList); //a[0,1,2,3]分别表示重复1,2,3,4次的牌
			
			
			//3带2 -----必含重复3次的牌
			if(card_index.a[2].size()==1 &&card_index.a[1].size()==1 && size==5)
				return T32;
			
			
			
			//4带2(单,双)
			if(card_index.a[3].size()==1 && size==6)
				return T411;
			if(card_index.a[3].size()==1 && card_index.a[1].size()==2 &&size==8)
				return T422;
			
			
			//单连,保证不存在王
			if((cardList.get(0).color()!=5)&&(card_index.a[0].size()==size) &&
					(cardList.get(0).value() - cardList.get(size-1).value()==size-1))
				return T123;
			
			//连队
			if(card_index.a[1].size()==size/2 && size%2==0 && size/2>=3
					&&(cardList.get(0).value() - cardList.get(size-1).value()==(size/2-1)))
				return T1122;
			//飞机
			if(card_index.a[2].size()==size/3 && (size%3==0) &&
					(cardList.get(0).value() - cardList.get(size-1).value()==(size/3-1)))
				return T111222;
			//飞机带n单,n/2对
			if(card_index.a[2].size()==size/4 &&
					((Integer)(card_index.a[2].get(size/4-1))-(Integer)(card_index.a[2].get(0))==size/4-1))
				return T11122234;

			//飞机带n双
			if(card_index.a[2].size()==size/5 && card_index.a[2].size()==size/5 &&
					((Integer)(card_index.a[2].get(size/5-1))-(Integer)(card_index.a[2].get(0))==size/5-1))
				return T1112223344;

		}
		
		
		
		
		return T0;
	}
	
	/**
	 * 1-13各算一种,王算第14种
	 */
	private static void getMax(Card_index card_index,List<CardLabel> list){
		int count[]=new int[14];
		for(int i=0;i<14;i++)
			count[i]=0;
		
		for(int i=0,len=list.size();i<len;i++){
			
			
			if(list.get(i).color()==5)
				count[13]++;
			else
				count[list.get(i).value()-1]++; 
		}
		
		for(int i=0;i<14;i++){
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
	
	
	

	public static void getT123(List<CardLabel> list,Model model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		if(list.size()>0&&(list.get(0).value()<7 ||list.get(list.size()-1).value()>10))
			return;
		if(list.size()<5)
			return;
		for(int i=0,len=list.size();i<len;i++){
			int k=i;
			for(int j=i;j<len;j++){
				if(list.get(i).value() - list.get(j).value()==j-i)
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
	public static void getT1122(List<CardLabel> list,Model model){
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

	public static void getT111222(List<CardLabel> list,Model model){
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

	public static void getT4(List<CardLabel> list,Model model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		//王炸
		if(list.size()>=2 &&list.get(0).color()==5 && list.get(1).color()==5)
		{
			model.a4.add(list.get(0).getName()+","+list.get(1).getName()); //按名字加入
			del.add(list.get(0));
			del.add(list.get(1));
		}
		//如果王不构成炸弹咋先拆单
		if(list.get(0).color()==5 && list.get(1).color()!=5)
		{
			del.add(list.get(0));
			model.a1.add(list.get(0).getName());
		}
		list.removeAll(del);
		//一般的炸弹
		for(int i=0,len=list.size();i<len;i++){
			if(i+3<len && list.get(i).value() == list.get(i+3).value())
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
	public static void getT3(List<CardLabel> list,Model model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		//连续3张相同
		for(int i=0,len=list.size();i<len;i++){
			if(i+2<len&&list.get(i).value() == list.get(i+2).value())
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
	public static void getT2(List<CardLabel> list,Model model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		//连续2张相同
		for(int i=0,len=list.size();i<len;i++){
			if(i+1<len&& list.get(i).value() == list.get(i+1).value())
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
	public static void getT1(List<CardLabel> list,Model model){
		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
		//1
		for(int i=0,len=list.size();i<len;i++){
			model.a1.add(list.get(i).getName());
			del.add(list.get(i));
		}
		list.removeAll(del);
	}

}

class Card_index{
	List a[]=new ArrayList[4];//单张
}