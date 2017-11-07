package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTextField;

import com.poker.Card;
import com.poker.BootFrame;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"position"},callSuper = false)
public abstract class Player {
	/**
	 * 左手电脑
	 */
	public static final int POSITION_LEFT = 0;

	/**
	 * 玩家
	 */
	public static final int POSITION_USER = 1;

	/**
	 * 右手电脑
	 */
	public static final int POSITION_RIGHT = 2;

	protected List<Card> cardHoldList = new ArrayList<Card>();

	protected List<Card> cardPublishList = new ArrayList<Card>();

	protected Point lordPoint;

	protected int position;

	protected JTextField clockFiled;

	protected volatile boolean lord;

	protected volatile boolean published;

	protected volatile boolean clockEnd = true;
	
	protected BootFrame frame;

	private Thread clockThread;

	public Player(BootFrame frame,int position){
		this.frame = frame;
		this.position = position;
		clockFiled = new JTextField("倒计时:");
		clockFiled.setVisible(false);
		frame.container.add(clockFiled);
	}

	public void order(){
		Collections.sort(cardHoldList,new Comparator<Card>() {
			@Override
			public int compare(Card c1, Card c2) {
				return c2.getSingleValue() - c1.getSingleValue();
			}
		});
	}

	public abstract void compete(final int seconds);

	public abstract void publish(final int seconds);
	
	public void contain() {
		
	}

	public void resetPosition(boolean syn){
		int point_x = 0;
		int point_y = 0;
		switch(position){
		case POSITION_LEFT:
			point_x = 50;
			point_y = (450 / 2) - (cardHoldList.size() + 1) * 15 / 2;
			break;
		case POSITION_USER:
			point_x = (800 / 2) - (cardHoldList.size() + 1) * 21 / 2;
			point_y = 450;
			break;
		case POSITION_RIGHT:
			point_x = 700;
			point_y = (450 / 2) - (cardHoldList.size() + 1) * 15 / 2;
			break;
		default:;
		}
		for(int i = 0;i < cardHoldList.size();i++){
			Card card=cardHoldList.get(i);
			if(syn) {
				card.synmove(new Point(point_x,point_y));
			}else {
				card.asynmove(new Point(point_x,point_y));
			}
			frame.container.setComponentZOrder(card.getLabel(), 0);
			if(position == POSITION_USER)
				point_x += 21;
			else 
				point_y += 15;
		}

	}

	/**
	 * 时钟放在主线程中走
	 */
	protected void clock(final int seconds){
		clockEnd = false;
		int clockTime = seconds;
		clockFiled.setVisible(true);
		while(clockTime >= 0){
			if(clockEnd){
				clockFiled.setVisible(false);
				return;
			}
			clockFiled.setText("倒计时:" + clockTime--);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				if(clockEnd){
					clockFiled.setVisible(false);
					return;
				}
			}
		}
		clockEnd = true;
		clockFiled.setVisible(false);
	}
}
