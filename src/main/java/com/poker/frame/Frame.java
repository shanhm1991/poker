package com.poker.frame;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.poker.player.Player;
import com.poker.card.Card;
import com.poker.player.Conputer;
import com.poker.player.Person;

/**
 * 
 * @author shanhm1991
 *
 */
public class Frame extends JFrame {

	private static final long serialVersionUID = -28832292935664928L;

	public Container container = null;

	private Player person;

	private Player leftConputer;

	private Player rightConputer;

	public Frame() {
		setSize(830, 620);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(getOwner());
		setTitle("斗地主 - shanhm1991@163.com");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container = this.getContentPane();
		container.setLayout(null);
		container.setBackground(new Color(0, 112, 26)); 
		
		JMenu gameMenu = new JMenu("游戏");
		JMenuItem exit = new JMenuItem("退出");
		exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		gameMenu.add(exit);
		
		JMenuItem start = new JMenuItem("新游戏");
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(1);
			}
		});
		gameMenu.add(start);
		
		JMenu helpMenu = new JMenu("帮助");
		JMenuItem about = new JMenuItem("关于");
		about.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				showAbout();
			}
		});
		helpMenu.add(about);
		
		JMenuBar menu = new JMenuBar();
		menu.add(gameMenu);
		menu.add(helpMenu);
		this.setJMenuBar(menu);
	}

	public void play(){
		person = new Person(this,Player.PERSON);
		leftConputer = new Conputer(this,Player.CONPUTER_LEFT);
		rightConputer = new Conputer(this,Player.CONPUTER_RIGHT);
		
		//初始化
		List<Card> cardList = new LinkedList<Card>();
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 13; j++) {
				Card card = new Card(i + "-" + j).init();
				cardList.add(card);
				container.add(card.getLabel());
			}
		}
		Card card1 = new Card("5-1").init();
		cardList.add(card1);
		container.add(card1.getLabel());
		Card card2 = new Card("5-2").init();
		cardList.add(card2);
		container.add(card2.getLabel());
		
		//洗牌
		SecureRandom random = new SecureRandom();
		for(int i = 0;i < 100;i++){
			int index = random.nextInt(54);
			Card card = cardList.get(index);
			cardList.set(index, cardList.get(0));
			cardList.set(0, card);
		}

		//发牌
		List<Card> lordCardList = new ArrayList<Card>();
		for(int i=0; i<cardList.size(); i++){
			Card card = cardList.get(i);
			if(i >= 51){
				card.asynmove(new Point(300 + (i - 51) * 80, 10), container);
				lordCardList.add(card);
				continue;
			}
			
			switch ((i)%3) {
			case 0:
				card.asynmove(new Point(50, 60 + i * 5), container);
				leftConputer.getCardHoldList().add(card);
				card.show(); 
				break;
			case 1:
				card.asynmove(new Point(180 + i * 7, 450), container);
				person.getCardHoldList().add(card);
				card.show(); 
				break;
			case 2:
				card.asynmove(new Point(700, 60 + i * 5), container);
				rightConputer.getCardHoldList().add(card);
				card.show(); 
				break;
			}
		}
		
		//理牌
		person.order(false);
		person.resetPosition(false);
		leftConputer.order(false);
		leftConputer.resetPosition(false);
		rightConputer.order(false);
		rightConputer.resetPosition(false); 
		
		//抢地主
		Player lorder = null;
		for(int i = 1; ;i++){ //玩家开始抢
			Player player = getPlayer(i % 3);
			player.compete(15);
			if(player.isLord()){
				lorder = player;
				lorder.lordinit(lordCardList);
				break;
			}
		}
		
		//从地主开始出牌
		for(int i = lorder.getPosition(); ;i++){
			Player player = getPlayer((i % 3));
			player.setPublished(false);
			player.publish(15);
			
			if(player.getCardHoldList().isEmpty()){
				if(player.getPosition() == Player.PERSON){ 
					JOptionPane.showMessageDialog(this, "winner!");
				}else{
					JOptionPane.showMessageDialog(this, "losser!");
				}
				return;
			}
		}
	}

	public Player getPlayer(int position){
		switch(position){
		case Player.CONPUTER_LEFT:
			return leftConputer;
		case Player.PERSON:
			return person;
		case Player.CONPUTER_RIGHT:
			return rightConputer;
		default:
			return null;
		}
	}

	private void showAbout(){
		JOptionPane.showMessageDialog(this, "version 1.0 shanhm1991@163.com");
	}

}
