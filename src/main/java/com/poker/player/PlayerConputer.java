package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.poker.Card;
import com.poker.BootFrame;
import com.poker.player.idea.ThreadCompete;
import com.poker.player.idea.ThreadPublish;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerConputer extends Player {

	public PlayerConputer(BootFrame frame,int position) {
		super(frame,position);
		switch(position){
		case POSITION_LEFT:
			lordPoint = new Point(80,20);
			clockFiled.setBounds(140, 230, 60, 20);
			break;
		case POSITION_RIGHT:
			lordPoint = new Point(700,20);
			clockFiled.setBounds(620, 230, 60, 20);
			break;
		default:;
		}
	}

	@Override
	public void compete(final int seconds) {
		//计算抢地主线程
		FutureTask<Boolean> competeThread = new FutureTask<Boolean>(new ThreadCompete(this));
		new Thread(competeThread).start();
		//时钟线程
		Thread clockThread = new Thread(){
			@Override
			public void run() {
				clock(seconds);
			}
		};
		clockThread.start();
		//主线程阻塞等待
		try {
			lord = competeThread.get(seconds,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			competeThread.cancel(true);
		} catch (TimeoutException e) {
			competeThread.cancel(true);
		}finally{
			clockThread.interrupt();
		}

		if(lord){
			clockFiled.setText("抢地主");
			clockFiled.setVisible(true);
		}else{
			clockFiled.setText("不 抢");
			clockFiled.setVisible(true);
			try {
				Thread.sleep(1000);
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
		FutureTask<List<Card>> publishThread = new FutureTask<List<Card>>(new ThreadPublish(this));
		new Thread(publishThread).start();
		//时钟线程
		Thread clockThread = new Thread(){
			@Override
			public void run() {
				clock(seconds);
			}
		};
		clockThread.start();
		//主线程阻塞等待
		List<Card> publishCardList = null;
		try {
			publishCardList = publishThread.get(seconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			publishCardList = new ArrayList<Card>();
			publishThread.cancel(true);
		} catch (TimeoutException e) {
			publishCardList = new ArrayList<Card>();
			publishThread.cancel(true);
		}finally{
			clockThread.interrupt();
		}

		if(publishCardList.isEmpty()){
			clockFiled.setVisible(true);
			clockFiled.setText("不 要");
			return;
		}

		Point point = new Point();
		if (position == 0)
			point.x = 200;
		if (position == 2)
			point.x = 550;
		point.y = (400 / 2) - (publishCardList.size() + 1) * 15 / 2;
		cardPublishList.addAll(publishCardList);
		cardHoldList.removeAll(publishCardList);
		for(Card card : publishCardList){
			card.asynmove(point,frame.container);
			point.y += 15;
			card.show();
		}
		resetPosition(false);
	}
}
