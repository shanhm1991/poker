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
//		for(CardLabel card : cardHoldList){
//			card.setClickable(true); 
//		}
	}

	@Override
	public void complete() throws InterruptedException{
		competeButton.setVisible(true);
		notCompeteButton.setVisible(true);
		clock(10);
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
		clock(15);//TODO
	}

	private void playerPublish() {
		List<CardLabel> publishCards = new ArrayList<CardLabel>();
		for(CardLabel card : cardHoldList) {
			if(card.isClicked()){
				publishCards.add(card);
			}
		}
		//主动出牌或者跟牌
		if ((frame.getPlayer((position + 1) % 3).isClockEnd()
				&& frame.getPlayer((position + 2) % 3).isClockEnd()
				&& getType(publishCards) != CardType.T0)
				|| checkCards(publishCards)){

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

	private  boolean checkCards(List<CardLabel> ownList){
		List<CardLabel> otherList = frame.getPlayer((position + 2) % 3).getCardPublishList();
		if(otherList.isEmpty()){
			otherList = frame.getPlayer((position + 1) % 3).getCardPublishList();
		}

		compare(ownList, otherList);

		int ownType=getType(ownList);

		//牌不一样且不是炸弹，或者牌型不一样
		if((ownList.size() != otherList.size() && ownType!=CardType.T4)
				|| getType(ownList) != getType(otherList)){
			return false;
		}

		//王炸弹
		if(ownType == CardType.T4){
			if(ownList.size()==2)
				return true;
			if(otherList.size()==2)
				return false;
		}
		//单牌,对子,3带,4炸弹
		if(ownType==CardType.T1||ownType==CardType.T2||ownType==CardType.T3||ownType==CardType.T4){
			if(ownList.get(0).getSingleValue() <= otherList.get(0).getSingleValue())
			{
				return false;
			}else {
				return true;
			}
		}
		//顺子,连队，飞机裸
		if(ownType==CardType.T123||ownType==CardType.T22||ownType==CardType.T33)
		{
			if(ownList.get(0).getValue() <= otherList.get(0).getValue())
				return false;
			else 
				return true;
		}
		//按重复多少排序
		//3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的 
		if(ownType==CardType.T31||ownType==CardType.T32
				||ownType==CardType.T3312||ownType==CardType.T3322){
			List<CardLabel> a1=getOrder2(ownList); //我出的牌
			List<CardLabel> a2=getOrder2(otherList);//当前最大牌
			if(a1.get(0).getValue() < a2.get(0).getValue())
				return false;
		}
		return true;
	}

}
