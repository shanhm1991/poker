package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.poker.card.Card;
import com.poker.card.Type;
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
			setClockEnd(true); 
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
	private class CompeteTask implements Callable<Boolean>{
		
		@Override
		public Boolean call() throws Exception {
			Type type = new Type(cardHoldList);
			//判断散乱程度
			List<Card> distinctList = type.distinctList;
			Collections.sort(distinctList,new Comparator<Card>() {
				@Override
				public int compare(Card c1, Card c2) {
					return c2.getValue() - c1.getValue();
				}
			});
			
			//找出缺数进行分段, 两个缺数之差如果大于5则可能组成顺子
			List<Card> lackList = new ArrayList<>();
			for(int i = 1; i <= 13; i++){
				Card card = new Card("1-" + i);
				if(type.count(card) == 0){
					lackList.add(card);
				}
			}
			
			List<Card> singleList = new ArrayList<>();//只能单出的牌
			ListIterator<Card> it = lackList.listIterator();
		    int prev = 0;
			while(it.hasNext()){
				Card card = it.next();
				int next = card.getValue();
				int interval = next - prev;
				if(interval == 0){
					continue;
				}
				
				//连续小于5，且仅有一张的牌只能单出或被三带
				if(interval <= 5){
					for(int i = prev + 1;i < next;i++){
						Card single = new Card("1-" + i);
						if(type.count(single) == 1){
							singleList.add(single);
						}
					}
				}else{
					//获取连续牌的最小手数， 确定是否单出
					int total  = 0;
					for(int i = prev + 1;i < next;i++){
						Card c = new Card("1-" + i);
						total += type.count(c);
					}
				}
				prev = next;
			}
			
			System.out.println(name + "  " + distinctList); 
			System.out.println(name + "  " + lackList); 
			System.out.println(name + "  " + singleList); 
			
//			int x = type.count(new Card("5-1")); //小王 3分
//			int y = type.count(new Card("5-2")); //大王 2分
//			int xx = type.count(new Card("1-2")); //2字 1分
//			if((3 * x + 2 * y + xx) < 4){
//				return false;
//			}
			
			Thread.sleep(3000); //让秒表走一会儿
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
