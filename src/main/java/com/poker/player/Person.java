package com.poker.player;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import com.poker.card.Card;
import com.poker.card.Type;
import com.poker.frame.Frame;

/**
 * 
 * 玩家
 * 
 * @author shanhm1991
 *
 */
public class Person extends Player {

	private JButton competeButton;

	private JButton notCompeteButton;

	private JButton publishButton;

	private JButton notPublishButton;

	public Person(Frame frame,int position) {
		super(frame,position);
		name = "玩家";

		lordPoint = new Point(80,430);
		clockFiled.setBounds(374, 360, 60, 20);

		competeButton = new JButton("抢地主");
		competeButton.setVisible(false);
		competeButton.setBounds(320, 400,75,20);
		competeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clockFiled.setText("抢地主");
				isLord = true;
				isClockEnd = true;
			}
		});

		notCompeteButton = new JButton("不  抢");
		notCompeteButton.setVisible(false);
		notCompeteButton.setBounds(420, 400,75,20);
		notCompeteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clockFiled.setText("不  抢");
				isLord = false;
				isClockEnd = true;
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
				isClockEnd = true;
				published = false;
			}
		});

		frame.container.add(competeButton);
		frame.container.add(notCompeteButton);
		frame.container.add(publishButton);
		frame.container.add(notPublishButton);
	}

	@Override
	public void lordinit(List<Card> lordCardList){
		super.lordinit (lordCardList);
		for(Card card : cardHoldList){
			card.setClickable(true); 
		}
	}

	@Override
	public void compete(final int seconds) {
		competeButton.setVisible(true);
		notCompeteButton.setVisible(true);
		clock(seconds, false);
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
		clock(seconds, true);
		publishButton.setVisible(false);
		notPublishButton.setVisible(false);
	}

	/*
	 * list.remove(card)是根据equals来删，会导致误删
	 * list.remove(index)每次删完都会重新移动数组，影响下一次删除
	 * 所以每次cardHoldList和cardPublishList整个重新替换，而不要调用remove
	 */
	private void confirmPublishCard() {
		List<Card> publishList = new ArrayList<Card>();
		List<Card> holdList = new ArrayList<Card>();
		for(Card card : cardHoldList) {
			if(card.isClicked()) {
				publishList.add(card);
			}else{
				holdList.add(card);
			}
		}

		Type type = new Type(publishList);
		if(publishList.isEmpty() || !isPublishAble(publishList, type)){
			return;
		}

		Point point=new Point();
		point.x = (770 / 2) - (publishList.size() + 1) * 15 / 2;;
		point.y = 300;
		for(Card card : publishList){
			card.synmove(point);
			point.x += 15; 
		}

		cardHoldList = holdList;
		cardPublishList = publishList;
		resetPosition(true);
		clockFiled.setVisible(false);
		isClockEnd = true;
		published = true;
		LOG.info(name + ": 出牌[" + type + "]=" + publishList);
	}

	//接牌
	private boolean isPublishAble(List<Card> publishList, Type ownType){
		int ownTypeValue = ownType.getType();
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
		int otherTypeValue = otherType.getType();

		//有炸弹
		if(ownTypeValue == Type.T4){
			if(publishList.size() == 2 || otherTypeValue != Type.T4){
				return true;
			}
			if(otherPublishCards.size() == 2){//size=2是王炸
				return false;
			}
			return ownType.valueT4() > otherType.valueT4();
		}
		//牌数或牌种不同
		if(publishList.size() != otherPublishCards.size() || ownTypeValue != otherTypeValue){
			return false;
		}
		//ownList.size=otherList.size、ownType=otherType
		if(ownTypeValue == Type.T1 || ownTypeValue==Type.T2 || ownTypeValue==Type.T3){
			return publishList.get(0).getSingleValue() > otherPublishCards.get(0).getSingleValue();
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
