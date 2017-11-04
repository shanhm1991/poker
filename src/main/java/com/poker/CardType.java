package com.poker;

import java.util.ArrayList;
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
	
	public List<CardLabel> listT4 = new ArrayList<CardLabel>();

	public List<CardLabel> distinctList = new ArrayList<CardLabel>();
	
	public CardType(List<CardLabel> list){
		Map<CardLabel.CardKey,Integer> map = new HashMap<CardLabel.CardKey,Integer>();
		for(CardLabel card : list){
			Integer count = map.get(card.getCardKey());
			if(count == null){
				map.put(card.getCardKey(),1);
			}else{
				map.put(card.getCardKey(),++count);
			}
		}
		Iterator<Entry<CardLabel.CardKey, Integer>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<CardLabel.CardKey, Integer> entry = it.next();
			if(entry.getValue() == 1){
				listT1.add(entry.getKey().getCard());
			}else if(entry.getValue() == 2){
				listT2.add(entry.getKey().getCard());
			}else if(entry.getValue() == 3){
				listT3.add(entry.getKey().getCard());
			}else if(entry.getValue() == 4){
				listT4.add(entry.getKey().getCard());
			}
		}
		distinctList.addAll(listT1);
		distinctList.addAll(listT2);
		distinctList.addAll(listT3);
		distinctList.addAll(listT4);
	}
}
