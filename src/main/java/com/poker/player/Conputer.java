package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.poker.frame.Card;
import com.poker.frame.Frame;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * 电脑
 * 
 * @author shanhm1991
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Conputer extends Player {
	
	public Conputer(Frame frame,int position) {
		super(frame,position);
		switch(position){
		case CONPUTER_LEFT:
			lordPoint = new Point(80,20);
			clockFiled.setBounds(140, 230, 60, 20);
			name = "电脑[左]";
			break;
		case CONPUTER_RIGHT:
			lordPoint = new Point(700,20);
			clockFiled.setBounds(620, 230, 60, 20);
			name = "电脑[右]";
			break;
		default:;
		}
	}
	
	@Override
	public void compete(final int seconds) {
		FutureTask<Boolean> competeTask = new FutureTask<Boolean>(new CompeteTask());
		new Thread(competeTask).start();
		
		//计时读秒
		Thread clockThread = new Thread(){
			@Override
			public void run() {
				clock(seconds, false);
			}
		};
		clockThread.start();
		
		//同步等待
		try {
			isLord = competeTask.get(seconds,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		} catch (ExecutionException e) {
			competeTask.cancel(true);
			isLord = false;
		} catch (TimeoutException e) {
			competeTask.cancel(true);
			isLord = false;
		}finally{
			clockThread.interrupt();
		}

		if(isLord){
			clockFiled.setText("抢地主");
			clockFiled.setVisible(true);
		}else{
			clockFiled.setText("不 抢");
			clockFiled.setVisible(true);
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clockFiled.setVisible(false);
		}
	}

	@Override
	public void publish(final int seconds) {
		for(Card card : cardPublishList){
			card.hide();
		}
		cardPublishList.clear();
		//计算出牌线程
		FutureTask<List<Card>> publishThread = new FutureTask<List<Card>>(new PublishThread(this));
		new Thread(publishThread).start();
		//时钟线程
		Thread clockThread = new Thread(){
			@Override
			public void run() {
				clock(seconds, true);
			}
		};
		clockThread.start();
		//主线程阻塞等待
		List<Card> publishList = null;
		try {
			publishList = publishThread.get(seconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			publishList = new ArrayList<Card>();
			publishThread.cancel(true);
		} catch (TimeoutException e) {
			publishList = new ArrayList<Card>();
			publishThread.cancel(true);
		}finally{
			clockThread.interrupt();
		}

		if(publishList.isEmpty()){
			clockFiled.setVisible(true);
			clockFiled.setText("不 要");
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clockFiled.setVisible(false);
			return;
		}

		Point point = new Point();
		if (position == 0)
			point.x = 200;
		if (position == 2)
			point.x = 550;
		point.y = (400 / 2) - (publishList.size() + 1) * 15 / 2;
		for(Card card : publishList){
			card.asynmove(point,frame.container);
			point.y += 15;
			card.show();
		}

		List<Card> holdList = new ArrayList<Card>();
		for(Card hCard : cardHoldList){
			boolean publish = false;
			for(Card pCard : publishList){
				if(hCard.getName().equals(pCard.getName())){
					publish = true;
					break;
				}
			}
			if(!publish){
				holdList.add(hCard);
			}
		}

		cardPublishList = publishList;
		cardHoldList = holdList;
		resetPosition(false);
	}
	
	
	/**
	 * 
	 * @author shanhm1991
	 *
	 */
	private static class CompeteTask implements Callable<Boolean>{
		
		@Override
		public Boolean call() throws Exception {
			
			
			Thread.sleep(3000);

			
			return false;
		}

	}
	
	/**
	 * 
	 * @author shanhm1991
	 *
	 */
	private class PublishThread implements Callable<List<Card>>{

		private Player conputer;

		public PublishThread(Player conputer) {
			this.conputer = conputer;
		}

		@Override
		public List<Card> call() throws Exception {
			Thread.sleep(2000);

			conputer.setClockEnd(true);

			return new ArrayList<Card>();
		}

	}
}
