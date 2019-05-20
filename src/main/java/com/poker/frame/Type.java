package com.poker.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author shanhm1991
 *
 */
public class Type {
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
	 * 顺子
	 */
	public static final int T123 = 123;

	/**
	 * 连对
	 */
	public static final int T222 = 222;

	/**
	 * 3带1
	 */
	public static final int T31 = 31;

	/**
	 * 3带2
	 */
	public static final int T32 = 32;

	/**
	 * 飞机
	 */
	public static final int T33 = 33;

	/**
	 * 飞机带单牌
	 */
	public static final int T3312 = 3312;

	/**
	 * 飞机带对子
	 */
	public static final int T3322 = 3322;

	private List<Card> cardList = new ArrayList<Card>();

	public List<Card> listT1 = new ArrayList<Card>();

	public List<Card> listT2 = new ArrayList<Card>();

	public List<Card> listT3 = new ArrayList<Card>();

	public List<Card> listT4 = new ArrayList<Card>();

	public List<Card> distinctList = new ArrayList<Card>();

	public Type(List<Card> cardList){
		this.cardList = cardList;
		//		Collections.sort(cardList,new Comparator<Card>() {
		//			@Override
		//			public int compare(Card c1, Card c2) {
		//				return c2.getValue() - c1.getValue();
		//			}
		//		});
		//		for(int m=0;m<cardList.size();m++){
		//			Card mCard = cardList.get(m);
		//			int cradCount = 0;
		//			for(int n=m;n<cardList.size();n++,cradCount++ ){
		//				Card nCard = cardList.get(n);
		//				if(nCard.getValue() == mCard.getValue()){
		//					continue;
		//				}else{
		//					break;
		//				}
		//			}
		//			m = m + cradCount - 1;//抵销掉自增的1 
		//			switch(cradCount){
		//			case 1:
		//				listT1.add(mCard); break;
		//			case 2:
		//				listT2.add(mCard); break;
		//			case 3:
		//				listT3.add(mCard); break;
		//			case 4:
		//				listT4.add(mCard); break;
		//			}
		//		}
		//		distinctList.addAll(listT1);
		//		distinctList.addAll(listT2);
		//		distinctList.addAll(listT3);
		//		distinctList.addAll(listT4);
		Map<Card,Integer> map = new HashMap<Card,Integer>();
		for(Card card : cardList) {
			Integer count = map.get(card);
			if(count == null) {
				map.put(card, 1);
			}else {
				map.put(card, ++count);
			}
		}
		distinctList.addAll(map.keySet());
		for(Card card : distinctList) {
			switch(map.get(card)) {
			case 1:
				listT1.add(card); break;
			case 2:
				listT2.add(card); break;
			case 3:
				listT3.add(card); break;
			case 4:
				listT4.add(card); break;
			}
		}
	}

	public int type(){
		int listSize = cardList.size();
		int distinctSize = distinctList.size();
		//只有一种牌的牌型
		if(distinctSize == 1) {
			switch(listSize) {
			case 1:
				return Type.T1;
			case 2:
				return Type.T2;
			case 3:
				return Type.T3;
			case 4:
				return Type.T4;
			}
			return Type.T0;
		}
		//炸弹只能单出
		if(listT4.size() > 0){
			return Type.T0;
		}
		//只有两种牌的牌型
		if(distinctSize == 2){
			if(cardList.get(0).getValue() > 50 && cardList.get(1).getValue() > 50){
				return Type.T4;
			}
			if(listT3.size() > 0){
				switch(listSize) {
				case 4:
					return Type.T31;
				case 5:
					return Type.T32;
				case 6:
					return Type.T33;
				}
			}
			return Type.T0;
		}
		//4张以下至多只能有两种牌型
		if(listSize <= 4){
			return Type.T0;
		}
		//listSize > 4
		//飞机
		if(listT1.isEmpty() && listT2.isEmpty() && value123(listT3) != 0){ 
			return Type.T33;
		}
		//连对
		if(listT1.isEmpty() && listT3.isEmpty() && value123(listT2) != 0){
			return Type.T222;
		}
		//顺子
		if(listT2.isEmpty() && listT3.isEmpty() && value123(listT1) != 0){
			return Type.T123;
		}
		//飞机带单
		if(listT3.size() == listT1.size() && listT2.isEmpty() && value123(listT3) != 0){
			return Type.T3312;

		}
		//飞机带双
		if(listT3.size() == listT2.size() && listT1.isEmpty() && value123(listT3) != 0){
			return Type.T3322;
		}
		return Type.T0;
	}

	public int valueT123(){
		return value123(listT1);
	}

	public int valueT222(){
		return value123(listT2);
	}

	public int valueT33(){
		return value123(listT3);
	}

	public int valueT3(){
		return listT3.get(0).getSingleValue();
	}

	public int valueT4(){
		return listT4.get(0).getSingleValue();
	}

	private int value123(List<Card> list){
		Collections.sort(list,new Comparator<Card>() {
			@Override
			public int compare(Card c1, Card c2) {
				return c2.getValue() - c1.getValue();
			}
		});
		if(list.get(0).getValue() - list.get(list.size() - 1).getValue() == list.size() - 1){
			return list.get(0).getValue();
		}
		//A字顺 
		if(list.contains(new Card("1-1"))){ 
			Collections.sort(list,new Comparator<Card>() {
				@Override
				public int compare(Card c1, Card c2) {
					return c2.getContinueValue() - c1.getContinueValue();
				}
			});
			if(list.get(0).getContinueValue() - list.get(list.size() - 1).getContinueValue() == list.size() - 1){
				return list.get(0).getContinueValue();
			}
		}
		return 0;
	}
}
