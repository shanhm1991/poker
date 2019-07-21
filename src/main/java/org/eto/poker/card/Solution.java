package org.eto.poker.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author shanhm1991
 *
 */
public class Solution {
	
	//手数<牌型<牌型数<牌数
	Map<Integer, Map<Integer, List<List<Card>>>> solutionMap = new HashMap<>();
	
	private List<Card> list = new ArrayList<>();

	public Solution(List<Card> cardList){
		list.addAll(cardList);
	}
	
	
}
