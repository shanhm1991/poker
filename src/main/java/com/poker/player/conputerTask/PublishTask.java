package com.poker.player.conputerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.poker.CardLabel;
import com.poker.player.CardPlayer;

public class PublishTask implements Callable<List<CardLabel>>{

	private CardPlayer conputer;

	public PublishTask(CardPlayer conputer) {
		this.conputer = conputer;
	}

	@Override
	public List<CardLabel> call() throws Exception {
		Thread.sleep(2000);

		conputer.setClockEnd(true);

		return new ArrayList<CardLabel>();
	}

}
