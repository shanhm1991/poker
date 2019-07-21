package org.eto.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.eto.poker.card.Card;
import org.eto.poker.frame.Frame;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * 
 * @author shanhm1991
 *
 */
@Data
@EqualsAndHashCode(of = {"position"},callSuper = false)
public abstract class Player {
	
	protected static final Logger LOG = Logger.getLogger(Player.class);
	
	public static final int CONPUTER_LEFT = 0;
	
	public static final int CONPUTER_RIGHT = 2;

	public static final int PERSON = 1;
	
	protected String name;
	
	protected List<Card> cardHoldList = new ArrayList<Card>();

	protected List<Card> cardPublishList = new ArrayList<Card>();

	protected Point lordPoint;
	
	protected JLabel lordLabel; 

	protected int position;
	
	protected int lorderPosition;

	protected JTextField clockFiled;

	protected volatile boolean isLord;

	protected volatile boolean published;

	protected volatile boolean isClockEnd = true;
	
	protected Frame frame;

	private Thread clockThread;

	public Player(Frame frame,int position){
		this.frame = frame;
		this.position = position;
		clockFiled = new JTextField("倒计时:");
		clockFiled.setVisible(false);
		frame.container.add(clockFiled);
	}

	public void order(boolean isLord){
		Collections.sort(cardHoldList,new Comparator<Card>() {
			@Override
			public int compare(Card c1, Card c2) {
				return c2.getSingleValue() - c1.getSingleValue();
			}
		});
		if(isLord){
			LOG.info(name + ": 抢地主=" + cardHoldList);
		}else{
			LOG.info(name + ": 初始牌=" + cardHoldList);
		}
	}

	public abstract void compete(final int seconds);

	public abstract void publish(final int seconds);
	
	public void lordinit(List<Card> lordCardList){
		lorderPosition = position;
		for(Card card : lordCardList){
			card.show();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		lordLabel=new JLabel(new ImageIcon("images/dizhu.gif"));
		lordLabel.setSize(40, 40);
		lordLabel.setVisible(true);
		lordLabel.setLocation(lordPoint); 
		frame.container.add(lordLabel);
		
		cardHoldList.addAll(lordCardList);
		order(true);
		
		if(lorderPosition != PERSON){
			for(Card card : lordCardList){
				card.back();
			}
		}
		resetPosition(false);
	}

	public void resetPosition(boolean syn){
		int point_x = 0;
		int point_y = 0;
		switch(position){
		case CONPUTER_LEFT:
			point_x = 50;
			point_y = (450 / 2) - (cardHoldList.size() + 1) * 15 / 2;
			break;
		case PERSON:
			point_x = (800 / 2) - (cardHoldList.size() + 1) * 21 / 2;
			point_y = 450;
			break;
		case CONPUTER_RIGHT:
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
				card.asynmove(new Point(point_x,point_y),frame.container);
			}
			frame.container.setComponentZOrder(card.getLabel(), 0);
			if(position == PERSON)
				point_x += 21;
			else 
				point_y += 15;
		}
	}

	/**
	 * 时钟放在主线程中走
	 */
	protected void clock(final int seconds, boolean ispublish){
		isClockEnd = false;
		int clockTime = seconds;
		clockFiled.setVisible(true);
		while(clockTime >= 0){
			if(isClockEnd){
				clockFiled.setVisible(false);
				if(ispublish && !published){
					LOG.info(name + ": 不出");
				}
				return;
			}
			clockFiled.setText("倒计时:" + clockTime--);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				if(isClockEnd){
					clockFiled.setVisible(false);
					if(ispublish && !published){
						LOG.info(name + ": 不出");
					}
					return;
				}
			}
		}
		isClockEnd = true;
		clockFiled.setVisible(false);
		if(ispublish && !published){
			LOG.info(name + ": 不出");
		}
	}
}
