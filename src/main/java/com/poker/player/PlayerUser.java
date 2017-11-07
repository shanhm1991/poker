package com.poker.player;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;

import com.poker.Card;
import com.poker.BootFrame;
import com.poker.Type;


public class PlayerUser extends Player {

	private JButton competeButton;

	private JButton notCompeteButton;

	private JButton publishButton;

	private JButton notPublishButton;
	
	public PlayerUser(BootFrame frame,int position) {
		super(frame,position);
		lordPoint = new Point(80,430);
		clockFiled.setBounds(374, 360, 60, 20);
		competeButton = new JButton("抢地主");
		competeButton.setVisible(false);
		competeButton.setBounds(320, 400,75,20);
		competeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clockFiled.setText("抢地主");
				lord = true;
				clockEnd = true;
			}
		});
		notCompeteButton = new JButton("不  抢");
		notCompeteButton.setVisible(false);
		notCompeteButton.setBounds(420, 400,75,20);
		notCompeteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clockFiled.setText("不  抢");
				lord = false;
				clockEnd = true;
			}
		});
		publishButton= new JButton("出牌");
		publishButton.setVisible(false);
		publishButton.setBounds(320, 400, 60, 20);
		publishButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmPublishCard();
			}
		});
		notPublishButton= new JButton("不要");
		notPublishButton.setVisible(false);
		notPublishButton.setBounds(420, 400, 60, 20);
		notPublishButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clockFiled.setText("不要");
				clockEnd = true;
				published = false;
			}
		});
		frame.container.add(competeButton);
		frame.container.add(notCompeteButton);
		frame.container.add(publishButton);
		frame.container.add(notPublishButton);
	}

	@Override
	public void compete(final int seconds) {
		competeButton.setVisible(true);
		notCompeteButton.setVisible(true);
		clock(seconds);
		competeButton.setVisible(false);
		notCompeteButton.setVisible(false);
	}
	
	@Override
	public void publish(final int seconds){
		for(Card card : cardPublishList){
			card.hide();
		}
		cardPublishList.clear();
		publishButton.setVisible(true);
		notPublishButton.setVisible(true);
		clock(15);
		publishButton.setVisible(false);
		notPublishButton.setVisible(false);
	}

	private void confirmPublishCard() {
		for(Card card : cardHoldList) {
			if(card.isClicked()){
				cardPublishList.add(card);
			}
		}
		if(cardPublishList.isEmpty() || !isPublishAble()){
			return;
		}
		Point point=new Point();
		point.x = (770 / 2) - (cardPublishList.size() + 1) * 15 / 2;;
		point.y = 300;
		for(int i=0,len=cardPublishList.size();i<len;i++){
			cardPublishList.get(i).synmove(point);
			point.x += 15;
		}
		cardHoldList.removeAll(cardPublishList);
		resetPosition(true);
		clockFiled.setVisible(false);
		clockEnd = true;
		published = true;
	}
	
	private boolean isPublishAble(){
		Type ownType = new Type(cardPublishList);
		int ownTypeValue = ownType.typeValue();
		if(ownTypeValue == Type.T0){
			return false;
		}
		Player prePlayer = frame.getPlayer((position + 2) % 3);
		Player nextPlayer = frame.getPlayer((position + 1) % 3);
		//主动出牌直接出，接牌要出比别人大的牌
		if(prePlayer.isClockEnd() && nextPlayer.isClockEnd()){
			return true;
		}

		List<Card> otherPublishCards = prePlayer.getCardPublishList();
		if(otherPublishCards.isEmpty()){
			otherPublishCards = nextPlayer.getCardPublishList();
		}
		Type otherType = new Type(otherPublishCards);
		int otherTypeValue = otherType.typeValue();

		//有炸弹
		if(ownTypeValue == Type.T4){
			if(cardPublishList.size() == 2 || otherTypeValue != Type.T4){
				return true;
			}
			if(otherPublishCards.size() == 2){//size=2是王炸
				return false;
			}
			return ownType.valueT4() > otherType.valueT4();
		}
		//牌数或牌种不同
		if(cardPublishList.size() != otherPublishCards.size() || ownTypeValue != otherTypeValue){
			return false;
		}
		//ownList.size=otherList.size、ownType=otherType
		if(ownTypeValue == Type.T1 || ownTypeValue==Type.T2 || ownTypeValue==Type.T3){
			return cardPublishList.get(0).getSingleValue() > otherPublishCards.get(0).getSingleValue();
		}
		if(ownTypeValue == Type.T123){
			return ownType.valueT123() > otherType.valueT123();
		}
		if(ownTypeValue == Type.T222){
			return ownType.valueT222() > otherType.valueT222();
		}
		if(ownTypeValue == Type.T31 || ownTypeValue == Type.T32){
			return ownType.valueT3() > otherType.valueT3();
		}
		if(ownTypeValue == Type.T33 || ownTypeValue == Type.T3312 || ownTypeValue == Type.T3322){
			return ownType.valueT33() > otherType.valueT33();
		}
		return false;
	}
}
