package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.poker.CardLabel;
import com.poker.MainFrame;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerConputer extends CardPlayer {

	public PlayerConputer(MainFrame frame, int position) {
		super(frame, position);
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
		ThinkingTask competeThinking = new ThinkingTask();
		FutureTask<List<CardLabel>> future = new FutureTask<List<CardLabel>>(competeThinking);
		new Thread(future).start();
		clock(seconds);
	}

	@Override
	public void publish(final int seconds) {
		cardPublishList.clear();

		ThinkingTask publishThinking = new ThinkingTask();
		FutureTask<List<CardLabel>> future = new FutureTask<List<CardLabel>>(publishThinking);
		new Thread(future).start();
		clock(seconds);

		List<CardLabel> publishCardList;
		try {
			publishCardList = future.get(1, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			System.out.println("occured a exception:" + e.getMessage()); 
			publishCardList = new ArrayList<CardLabel>();
		}

		if(publishCardList.isEmpty()){
			frame.getPlayer(position).getClockFiled().setVisible(true);
			frame.getPlayer(position).getClockFiled().setText("不要");
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
		for(CardLabel card : publishCardList){
			card.move(point);
			point.y += 15;
			card.turnUp();
		}
		resetPosition();
	}

	/**
	 * 电脑思考线程，非swing线程，不能更新swing组件
	 */
	private class ThinkingTask implements Callable<List<CardLabel>>{

		@Override
		public List<CardLabel> call() throws Exception {
			

			Thread.sleep(2000);
			
			clockEnd = true;

			return new ArrayList<CardLabel>();
		}
	}
}
