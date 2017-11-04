package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void complete() throws InterruptedException{

	}
	
	public void publish() {

	}

	public void resetPosition(){
		Point point=new Point();
		switch(position){
		case POSITION_LEFT:
			point.x=50;
			point.y=(450/2)-(cardHoldList.size()+1)*15/2;
			break;
		case POSITION_USER:
			point.x=(800/2)-(cardHoldList.size()+1)*21/2;
			point.y=450;
			break;
		case POSITION_RIGHT:
			point.x=700;
			point.y=(450/2)-(cardHoldList.size()+1)*15/2;
			break;
		default:;
		}
		int len=cardHoldList.size();
		for(int i=0;i<len;i++){
			CardLabel card=cardHoldList.get(i);
			card.move(point);
			frame.container.setComponentZOrder(card, 0);
			if(position == POSITION_USER)
				point.x+=21;
			else 
				point.y+=15;
		}
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

	public void clock(final int seconds){
		clockEnd = false;
		clockThread = new Thread(){
			public void run(){
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
						clockFiled.setVisible(false);
						return;
					}
				}

				clockEnd = true;
				clockFiled.setVisible(false);
			}
		};
		clockThread.run();
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

	public static int getType(List<CardLabel> cardList){
		Map<CardLabel,Integer> cardMap = new HashMap<CardLabel,Integer>();
		for(CardLabel card : cardList){
			Integer count = cardMap.get(card);
			if(count == null){
				cardMap.put(card, 1);
			}else{
				cardMap.put(card, ++count);
			}
		} 
		int mapSize = cardMap.size();
		int listSize = cardList.size();
		if(mapSize == 1) {
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
		if(cardMap.containsValue(4)){
			return CardType.T0;
		}
		//mapSize > 1
		if(mapSize == 2){
			if(cardList.get(0).getValue() > 50 && cardList.get(1).getValue() > 50){
				return CardType.T4;
			}
			if(cardMap.containsValue(3)){
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
		if(listSize <= 5){
			return CardType.T0;
		}
		//listSize > 4
		CardType type = new CardType();
		type.listType(cardList); 
		//飞机
		if(type.listT1.isEmpty() && type.listT2.isEmpty() && type.cValue(type.listT3) != 0){ 
			return CardType.T33;
		}
		//连对
		if(type.listT1.isEmpty() && type.listT3.isEmpty() && type.cValue(type.listT2) != 0){
			return CardType.T22;
		}
		//顺子
		if(type.listT2.isEmpty() && type.listT3.isEmpty() && type.cValue(type.listT1) != 0){
			return CardType.T123;
		}
		//飞机带单
		if(type.listT3.size() == type.listT1.size() && type.listT2.isEmpty() && type.cValue(type.listT3) != 0){
			return CardType.T3312;

		}
		//飞机带双
		if(type.listT3.size() == type.listT2.size() && type.listT1.isEmpty() && type.cValue(type.listT3) != 0){
			return CardType.T3322;
		}
		return CardType.T0;
	}

	public static boolean compare(List<CardLabel> ownList,List<CardLabel> otherList){
		int ownType = getType(ownList);
		int otherType = getType(ownList);
		if(ownType == CardType.T4){
			if(ownList.size() == 2 || otherType != CardType.T4){
				return true;
			}
			if(otherList.size() == 2){//size=2是王炸
				return false;
			}
			return ownList.get(0).getSingleValue() > otherList.get(0).getSingleValue();
		}
		if(ownList.size() != otherList.size() || ownType != otherType){
			return false;
		}


		if(ownType == CardType.T1 || ownType==CardType.T2 || ownType==CardType.T3
				|| ownType == CardType.T123 || ownType == CardType.T22 || ownType == CardType.T33){
			return ownList.get(0).getSingleValue() > otherList.get(0).getSingleValue();
		}
		if(ownType == CardType.T31 || ownType == CardType.T32
				|| ownType == CardType.T3312 || ownType == CardType.T3322){


			return false;
		}
		return false;
	}

}
