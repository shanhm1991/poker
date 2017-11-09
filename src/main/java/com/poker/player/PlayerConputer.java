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
import com.poker.player.idea.IdeaCompete;
import com.poker.player.idea.IdeaPublish;

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
		FutureTask<Boolean> future = new FutureTask<Boolean>(new IdeaCompete(this));
		new Thread(future).start();
		clock(seconds);
		
		
	}

	@Override
	public void publish(final int seconds) {
		cardPublishList.clear();
		FutureTask<List<Card>> future = new FutureTask<List<Card>>(new IdeaPublish(this));
		new Thread(future).start();
		clock(seconds);

		List<Card> publishCardList;
		try {
			publishCardList = future.get(1, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			System.out.println("occured a exception:" + e.getMessage()); 
			publishCardList = new ArrayList<Card>();
		}

		if(publishCardList.isEmpty()){
			clockFiled.setVisible(true);
			clockFiled.setText("不要");
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
