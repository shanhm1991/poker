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
			//主线程忽略中断
		} catch (ExecutionException e) {
			//任务异常，强行结束线程
			competeThread.cancel(true);
			lord = false;
		} catch (TimeoutException e) {
			//任务超时，强行结束线程
			competeThread.cancel(true);
			lord = false;
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
}
