package com.poker;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTextField;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"role"},callSuper = false)
public class CardPlayer {
	
	/**
	 * 左手电脑
	 */
	public static final int ROLE_LEFT = 0;
	
	/**
	 * 玩家
	 */
	public static final int ROLE_PLAYER = 1;
	
	/**
	 * 右手电脑
	 */
	public static final int ROLE_RIGHT = 2;

	private Integer role;
	
	private MainFrame frame;
	
	private JTextField timeFiled;
	
	private List<CardLabel> cardList = new ArrayList<CardLabel>();

	private List<CardLabel> currentList = new ArrayList<CardLabel>();
	
	
	public CardPlayer(MainFrame frame, int role){
		this.role = role;
		this.frame = frame;
		timeFiled = new JTextField("倒计时:");
		timeFiled.setVisible(false);
		switch(role){
		case ROLE_LEFT:
			timeFiled.setBounds(140, 230, 60, 20);
			break;
		case ROLE_PLAYER:
			timeFiled.setBounds(374, 360, 60, 20);
			break;
		case ROLE_RIGHT:
			timeFiled.setBounds(620, 230, 60, 20);
			break;
		default:;
		}
		frame.container.add(timeFiled);
	}
	
	public  void order(){
		Collections.sort(cardList,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel o1, CardLabel o2) {
				int a1=Integer.parseInt(o1.getName().substring(0, 1));//花色
				int a2=Integer.parseInt(o2.getName().substring(0,1));
				int b1=Integer.parseInt(o1.getName().substring(2,o1.getName().length()));//数值
				int b2=Integer.parseInt(o2.getName().substring(2,o2.getName().length()));
				int flag=0;
				//如果是王的话
				if(a1==5) b1+=100;
				if(a1==5&&b1==1) b1+=50;
				if(a2==5) b2+=100;
				if(a2==5&&b2==1) b2+=50;
				//如果是A或者2
				if(b1==1) b1+=20;
				if(b2==1) b2+=20;
				if(b1==2) b1+=30;
				if(b2==2) b2+=30;
				flag=b2-b1;
				if(flag==0)
					return a2-a1;
				else {
					return flag;
				}
			}
		});
	}
	
	public void resetPosition(){
		Point point=new Point();
		switch(role){
		case ROLE_LEFT:
			point.x=50;
			point.y=(450/2)-(cardList.size()+1)*15/2;
			break;
		case ROLE_PLAYER:
			point.x=(800/2)-(cardList.size()+1)*21/2;
			point.y=450;
			break;
		case ROLE_RIGHT:
			point.x=700;
			point.y=(450/2)-(cardList.size()+1)*15/2;
			break;
		default:;
		}
		int len=cardList.size();
		for(int i=0;i<len;i++){
			CardLabel card=cardList.get(i);
			card.move(point);
			frame.container.setComponentZOrder(card, 0);
			if(role==1)point.x+=21;
			else point.y+=15;
		}
	}
	
	public int getScore(){
		int score=0;
		for(int i=0,len=cardList.size();i<len;i++){
			CardLabel card=cardList.get(i);
			if(card.getName().substring(0, 1).equals("5"))
			{
				//System.out.println(card.name.substring(0, 1));
				score+=5;
			}
			if(card.getName().substring(2, card.getName().length()).equals("2"))
			{
				//System.out.println(2);
				score+=2;
			}
		}
		return score;
	}
}
