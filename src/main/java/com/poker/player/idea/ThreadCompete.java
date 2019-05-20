package com.poker.player.idea;

import java.util.concurrent.Callable;

import com.poker.player.Player;

/**
 * 
 * @author shanhm1991
 *
 */
public class ThreadCompete implements Callable<Boolean>{
	
	private Player conputer;
	
	public ThreadCompete(Player conputer) {
		this.conputer = conputer;
	}

	@Override
	public Boolean call() throws Exception {
		
		Thread.sleep(3000);

		conputer.setClockEnd(true);
		
		return false;
	}

}
