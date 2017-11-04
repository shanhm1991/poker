package com.poker.player;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import com.poker.CardLabel;
import com.poker.CardType;
import com.poker.MainFrame;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerUser extends CardPlayer {

	private JButton competeButton;

	private JButton notCompeteButton;

	private JButton publishButton;

	private JButton notPublishButton;

	public PlayerUser(MainFrame frame, int position) {
		super(frame, position);
		lordPoint = new Point(80,430);
		clockFiled.setBounds(374, 360, 60, 20);
		competeButton = new JButton("抢地主");
		competeButton.setVisible(false);
		competeButton.setBounds(320, 400,75,20);
		competeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				lord = true;
				clockEnd = true;
				clockFiled.setText("抢地主");
				competeButton.setVisible(false);
				notCompeteButton.setVisible(false);
			}
		});
		notCompeteButton = new JButton("不  抢");
		notCompeteButton.setVisible(false);
		notCompeteButton.setBounds(420, 400,75,20);
		notCompeteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				lord = false;
				clockEnd = true;
				clockFiled.setText("不  抢");
			}
		});
		publishButton= new JButton("出牌");
		publishButton.setVisible(false);
		publishButton.setBounds(320, 400, 60, 20);
		publishButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				playerPublish();
				clockEnd = true;
			}
		});
		notPublishButton= new JButton("不要");
		notPublishButton.setVisible(false);
		notPublishButton.setBounds(420, 400, 60, 20);
		notPublishButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clockEnd = true;
				cardPublishList.clear();
				clockFiled.setText("不要");

			}
		});
		frame.container.add(competeButton);
		frame.container.add(notCompeteButton);
		frame.container.add(publishButton);
		frame.container.add(notPublishButton);
	}

	@Override
	public void order(){
		super.order();
		for(CardLabel card : cardHoldList){
			card.setClickable(true); 
		}
	}

	@Override
	public void compete() throws InterruptedException{
		competeButton.setVisible(true);
		notCompeteButton.setVisible(true);
		clock(30);
		while(!clockEnd){
			//同步阻塞等待 TODO
		}
		if(!lord){
			clockFiled.setText("不  抢");
			clockFiled.setVisible(true); 
		}
		Thread.sleep(500);
		clockFiled.setVisible(false);
		competeButton.setVisible(false);
		notCompeteButton.setVisible(false);
	}

	@Override
	public void publish(){
		publishButton.setVisible(true);
		notPublishButton.setVisible(true);
		clock(15);
	}

	private void playerPublish() {
		List<CardLabel> publishCards = new ArrayList<CardLabel>();
		for(CardLabel card : cardHoldList) {
			if(card.isClicked()){
				publishCards.add(card);
			}
		}
		System.out.println(publishCards.size());
		CardType ownType = new CardType(publishCards);
		int ownType_T = getType(publishCards,ownType);
		System.out.println(ownType_T);
		if(ownType_T == CardType.T0){
			return;
		}
		//主动出牌或者跟牌
		if ((frame.getPlayer((position + 1) % 3).isClockEnd() && frame.getPlayer((position + 2) % 3).isClockEnd())
				|| compareConputer(publishCards,ownType,ownType_T)){
			Point point=new Point();
			point.x=(770/2)-(publishCards.size()+1)*15/2;;
			point.y=300;
			for(int i=0,len=publishCards.size();i<len;i++){
				publishCards.get(i).move(point);
				point.x+=15;
			}
			cardPublishList = publishCards;
			cardHoldList.removeAll(publishCards);
			resetPosition();
			clockFiled.setVisible(false);
		}
	}
	
	private boolean compareConputer(List<CardLabel> ownList,CardType ownType,int ownType_T){
		List<CardLabel> otherList = frame.getPlayer((position + 2) % 3).getCardPublishList();
		if(otherList.isEmpty()){
			otherList = frame.getPlayer((position + 1) % 3).getCardPublishList();
		}
		CardType otherType = new CardType(ownList);
		int otherType_T = getType(ownList,otherType);
		//有炸弹
		if(ownType_T == CardType.T4){
			if(ownList.size() == 2 || otherType_T != CardType.T4){
				return true;
			}
			if(otherList.size() == 2){//size=2是王炸
				return false;
			}
			return ownList.get(0).getSingleValue() > otherList.get(0).getSingleValue();
		}
		//牌数或牌种不同
		if(ownList.size() != otherList.size() || ownType_T != otherType_T){
			return false;
		}
		//ownList.size=otherList.size、ownType=otherType
		if(ownType_T == CardType.T1 || ownType_T==CardType.T2 || ownType_T==CardType.T3){
			return ownList.get(0).getSingleValue() > otherList.get(0).getSingleValue();
		}
		if(ownType_T == CardType.T123){
			return typeValue(ownType.listT1) > typeValue(otherType.listT1);
		}
		if(ownType_T == CardType.T22){
			return typeValue(ownType.listT2) > typeValue(otherType.listT2);
		}
		if(ownType_T == CardType.T31 || ownType_T == CardType.T32){
			return ownType.listT3.get(0).getSingleValue() > otherType.listT3.get(0).getSingleValue();
		}
		if(ownType_T == CardType.T33 || ownType_T == CardType.T3312 || ownType_T == CardType.T3322){
			return typeValue(ownType.listT3) > typeValue(otherType.listT3);
		}
		return false;
	}
}
