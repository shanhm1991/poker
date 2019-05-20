package com.poker.frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author shanhm1991
 *
 */
public class Util {
	
	public static void getT4(List<Card> list){
		
		Map<Integer,List<Card>> cardMap = new HashMap<Integer,List<Card>>();
		for(Card card : list){
			List<Card> set = cardMap.get(card.getValue());
			if(set == null){
				set = new ArrayList<Card>();
				cardMap.put(card.getValue(), set);
			}
			set.add(card);
		}
		
		
		
		for(Entry<Integer, List<Card>> entry : cardMap.entrySet()){
			System.out.println(entry.getKey() + ":  " + entry.getValue().size() ); 
		}
		
		
	}

}
