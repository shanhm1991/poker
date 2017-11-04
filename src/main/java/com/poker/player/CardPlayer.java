package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTextField;

import com.poker.CardLabel;
import com.poker.CardType;
import com.poker.MainFrame;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"position"},callSuper = false)
public class CardPlayer {
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

	protected List<CardLabel> cardHoldList = new ArrayList<CardLabel>();

	protected List<CardLabel> cardPublishList = new ArrayList<CardLabel>();

	protected Point lordPoint;

	protected int position;

	protected MainFrame frame;

	protected boolean lord;

	protected JTextField clockFiled;

	private Thread clockThread;

	protected volatile boolean clockEnd = true;

	public CardPlayer(MainFrame frame, int position){
		this.position = position;
		this.frame = frame;
		clockFiled = new JTextField("倒计时:");
		clockFiled.setVisible(false);
		frame.container.add(clockFiled);
	}
	
	public void order(){
		Collections.sort(cardHoldList,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel c1, CardLabel c2) {
				return c2.getSingleValue() - c1.getSingleValue();
			}
		});
	}

	public void compete() throws InterruptedException{

	}
	
	public void publish() {

	}

	public void resetPosition(){
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
			CardLabel card=cardHoldList.get(i);
			card.move(new Point(point_x,point_y));
			frame.container.setComponentZOrder(card, 0);
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
				System.out.println(clockEnd);
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
	
	protected int getType(List<CardLabel> cardList){
		CardType type = new CardType(cardList);
		return getType(cardList,type);
	}
	
	protected int getType(List<CardLabel> cardList, CardType type){
		int listSize = cardList.size();
		int distinctSize = type.distinctList.size();
		if(distinctSize == 1) {
			switch(listSize) {
			case 1:
				return CardType.T1;
			case 2:
				return CardType.T2;
			case 3:
				return CardType.T3;
			case 4:
				return CardType.T4;
			}
			return CardType.T0;
		}
		if(type.listT4.size() > 0){
			return CardType.T0;
		}
		if(distinctSize == 2){
			if(cardList.get(0).getValue() > 50 && cardList.get(1).getValue() > 50){
				return CardType.T4;
			}
			if(type.listT3.size() > 0){
				switch(listSize) {
				case 4:
					return CardType.T31;
				case 5:
					return CardType.T32;
				case 6:
					return CardType.T33;
				}
			}
			return CardType.T0;
		}
		if(listSize <= 4){
			return CardType.T0;
		}
		//listSize > 4
		//飞机
		if(type.listT1.isEmpty() && type.listT2.isEmpty() && typeValue(type.listT3) != 0){ 
			return CardType.T33;
		}
		//连对
		if(type.listT1.isEmpty() && type.listT3.isEmpty() && typeValue(type.listT2) != 0){
			return CardType.T22;
		}
		//顺子
		if(type.listT2.isEmpty() && type.listT3.isEmpty() && typeValue(type.listT1) != 0){
			return CardType.T123;
		}
		//飞机带单
		if(type.listT3.size() == type.listT1.size() && type.listT2.isEmpty() && typeValue(type.listT3) != 0){
			return CardType.T3312;

		}
		//飞机带双
		if(type.listT3.size() == type.listT2.size() && type.listT1.isEmpty() && typeValue(type.listT3) != 0){
			return CardType.T3322;
		}
		return CardType.T0;
	}
	
	protected int typeValue(List<CardLabel> list){
		Collections.sort(list,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel c1, CardLabel c2) {
				return c2.getValue() - c1.getValue();
			}
		});
		if(list.get(0).getValue() - list.get(list.size() - 1).getValue() == list.size() - 1){
			return list.get(0).getValue();
		}
		//A字顺 TODO
		if(list.contains(new CardLabel(null,"1-1",false))){
			Collections.sort(list,new Comparator<CardLabel>() {
				@Override
				public int compare(CardLabel c1, CardLabel c2) {
					return c2.getContinueValue() - c1.getContinueValue();
				}
			});
			if(list.get(0).getValue() - list.get(list.size() - 1).getValue() == list.size() - 1){
				return list.get(0).getValue();
			}
		}
		return 0;
	}

	public int getScore(){
		int score=0;
		for(int i=0,len=cardHoldList.size();i<len;i++){
			CardLabel card=cardHoldList.get(i);
			if(card.getName().substring(0, 1).equals("5")){
				score+=5;
			}
			if(card.getName().substring(2, card.getName().length()).equals("2")){
				score+=2;
			}
		}
		return score;
	}

	//按照重复次数排序
	protected List<CardLabel> getOrder2(List<CardLabel> list){
		List<CardLabel> list2=new ArrayList<CardLabel>(list);
		List<CardLabel> list3=new ArrayList<CardLabel>();
		int len=list2.size();
		int a[]=new int[20];
		for(int i=0;i<20;i++)
			a[i]=0;
		for(int i=0;i<len;i++)
		{
			a[list2.get(i).getValue()]++;
		}
		int max=0;
		for(int i=0;i<20;i++){
			max=0;
			for(int j=19;j>=0;j--){
				if(a[j]>a[max])
					max=j;
			}

			for(int k=0;k<len;k++){
				if(list2.get(k).getValue() == max){
					list3.add(list2.get(k));
				}
			}
			list2.remove(list3);
			a[max]=0;
		}
		return list3;
	}
}
