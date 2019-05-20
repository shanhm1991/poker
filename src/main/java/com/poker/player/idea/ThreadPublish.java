package com.poker.player.idea;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.poker.frame.Card;
import com.poker.player.Player;

/**
 * 
 * @author shanhm1991
 *
 */
public class ThreadPublish implements Callable<List<Card>>{

	private Player conputer;

	public ThreadPublish(Player conputer) {
		this.conputer = conputer;
	}

	@Override
	public List<Card> call() throws Exception {
		Thread.sleep(2000);

		conputer.setClockEnd(true);

		return new ArrayList<Card>();
	}

}
