package com.poker.player.idea;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.poker.Card;
import com.poker.player.Player;

public class IdeaPublish implements Callable<List<Card>>{

	private Player conputer;

	public IdeaPublish(Player conputer) {
		this.conputer = conputer;
	}

	@Override
	public List<Card> call() throws Exception {
		Thread.sleep(2000);

		conputer.setClockEnd(true);

		return new ArrayList<Card>();
	}

}
