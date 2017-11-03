package com.poker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

	public static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			System.out.println(formatTime() +": ignore a interrupt request");
		}
	}
	
	public static String formatTime() {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS ").format(new Date());
	}

}
