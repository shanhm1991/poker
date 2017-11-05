package com.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	
	private List<CardLabel> cardList = new ArrayList<CardLabel>();

	public List<CardLabel> listT1 = new ArrayList<CardLabel>();

	public List<CardLabel> listT2 = new ArrayList<CardLabel>();

	public List<CardLabel> listT3 = new ArrayList<CardLabel>();

	public List<CardLabel> listT4 = new ArrayList<CardLabel>();

	public List<CardLabel> distinctList = new ArrayList<CardLabel>();

	public CardType(List<CardLabel> cardList){
		this.cardList = cardList;
		Collections.sort(cardList,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel c1, CardLabel c2) {
				return c2.getValue() - c1.getValue();
			}
		});
		for(int m=0;m<cardList.size();m++){
			CardLabel mCard = cardList.get(m);
			int cradCount = 0;
			for(int n=m;n<cardList.size();n++,cradCount++ ){
				CardLabel nCard = cardList.get(n);
				if(nCard.getValue() == mCard.getValue()){
					continue;
				}else{
					break;
				}
			}
			m = m + cradCount - 1;//抵销掉自增的1 
			switch(cradCount){
			case 1:
				listT1.add(mCard); break;
			case 2:
				listT2.add(mCard); break;
			case 3:
				listT3.add(mCard); break;
			case 4:
				listT4.add(mCard); break;
			}
		}
		distinctList.addAll(listT1);
		distinctList.addAll(listT2);
		distinctList.addAll(listT3);
		distinctList.addAll(listT4);
	}
	
	public int typeValue(){
		int listSize = cardList.size();
		int distinctSize = distinctList.size();
		if(distinctSize == 1) {
			switch(listSize) {
			case 1:
				return CardType.T1;
			case 2:
				return CardType.T2;
			case 3:
				return CardType.T3;
			case 4:
				return CardType.T4;
			}
			return CardType.T0;
		}
		if(listT4.size() > 0){
			return CardType.T0;
		}
		if(distinctSize == 2){
			if(cardList.get(0).getValue() > 50 && cardList.get(1).getValue() > 50){
				return CardType.T4;
			}
			if(listT3.size() > 0){
				switch(listSize) {
				case 4:
					return CardType.T31;
				case 5:
					return CardType.T32;
				case 6:
					return CardType.T33;
				}
			}
			return CardType.T0;
		}
		if(listSize <= 4){
			return CardType.T0;
		}
		//listSize > 4
		//飞机
		if(listT1.isEmpty() && listT2.isEmpty() && continueValue(listT3) != 0){ 
			return CardType.T33;
		}
		//连对
		if(listT1.isEmpty() && listT3.isEmpty() && continueValue(listT2) != 0){
			return CardType.T222;
		}
		//顺子
		if(listT2.isEmpty() && listT3.isEmpty() && continueValue(listT1) != 0){
			return CardType.T123;
		}
		//飞机带单
		if(listT3.size() == listT1.size() && listT2.isEmpty() && continueValue(listT3) != 0){
			return CardType.T3312;

		}
		//飞机带双
		if(listT3.size() == listT2.size() && listT1.isEmpty() && continueValue(listT3) != 0){
			return CardType.T3322;
		}
		return CardType.T0;
	}
	
	public int valueT123(){
		return continueValue(listT1);
	}
	
	public int valueT222(){
		return continueValue(listT2);
	}
	
	public int valueT33(){
		return continueValue(listT3);
	}
	
	public int valueT3(){
		return listT3.get(0).getSingleValue();
	}
	
	public int valueT4(){
		return listT4.get(0).getSingleValue();
	}
	
	private int continueValue(List<CardLabel> list){
		Collections.sort(list,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel c1, CardLabel c2) {
				return c2.getValue() - c1.getValue();
			}
		});
		if(list.get(0).getValue() - list.get(list.size() - 1).getValue() == list.size() - 1){
			return list.get(0).getValue();
		}
		
		boolean containsA = false;
		for(CardLabel card : list){
			if(card.getValue() == 1){
				containsA = true;
				break;
			}
		}
		//A字顺 TODO
		if(containsA){
			Collections.sort(list,new Comparator<CardLabel>() {
				@Override
				public int compare(CardLabel c1, CardLabel c2) {
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
