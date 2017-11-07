package com.poker.player.idea;

import java.util.concurrent.Callable;

import com.poker.Type;
import com.poker.player.Player;

/**
 * 每次抢地主或者出牌前先粗略算一下出牌最优手数
 * 对于炸弹和大王手数为-2；小王为-1.5
 * 对于单牌2为-1；A为-0.5；J/Q/K为1；7/8/9/10为1.5；3/4/5/6为2
 * 其他牌种，手数均算1
 */
public class IdeaCompete implements Callable<Boolean>{
	
	private Player conputer;
	
	public IdeaCompete(Player conputer) {
		this.conputer = conputer;
	}

	@Override
	public Boolean call() throws Exception {
		//CardType holdType = new CardType(conputer.getCardHoldList());
		
		Thread.sleep(2000);

		conputer.setClockEnd(true);
		
		return false;
	}

}
