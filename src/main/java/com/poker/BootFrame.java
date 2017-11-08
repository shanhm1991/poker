package com.poker;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.poker.player.Player;
import com.poker.player.PlayerConputer;
import com.poker.player.PlayerUser;

public class BootFrame extends JFrame {

	private static final long serialVersionUID = -28832292935664928L;

	public Container container = null;

	private JMenuItem start;

	private JMenuItem exit;

	private JMenuItem about;

	private JLabel lordLabel; 

	private int lordPosition;

	private Player userPlayer;

	private Player leftConputer;

	private Player rightConputer;

	private List<Card> lordCardList;

	public BootFrame() {
		init();
		userPlayer = new PlayerUser(this,Player.POSITION_USER);
		leftConputer = new PlayerConputer(this,Player.POSITION_LEFT);
		rightConputer = new PlayerConputer(this,Player.POSITION_RIGHT);
		initCard();
		compete();
		publish();
	}

	private void init() {
		setSize(830, 620);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(getOwner());
		setTitle("斗地主 - shanhm1991@163.com");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container = this.getContentPane();
		container.setLayout(null);
		container.setBackground(new Color(0, 112, 26)); 
		start = new JMenuItem("新游戏");
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(1);
			}
		});
		exit = new JMenuItem("退出");
		exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		about = new JMenuItem("关于");
		about.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				showAbout();
			}
		});
		JMenu gameMenu = new JMenu("游戏");
		gameMenu.add(start);
		gameMenu.add(exit);
		JMenu helpMenu = new JMenu("帮助");
		helpMenu.add(about);
		JMenuBar menu = new JMenuBar();
		menu.add(gameMenu);
		menu.add(helpMenu);
		this.setJMenuBar(menu);
		lordLabel=new JLabel(new ImageIcon("images/dizhu.gif"));
		lordLabel.setVisible(false);
		lordLabel.setSize(40, 40);
		container.add(lordLabel);
	}

	private void initCard(){
		List<Card> cardList = new LinkedList<Card>();
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 13; j++) {
				if ((i == 5) && (j > 2)){
					break;
				}
				Card card = new Card(i + "-" + j).init();
				container.add(card.getLabel());
				cardList.add(card);
			}
		}
		for(int i=0;i<100;i++){
			SecureRandom random = new SecureRandom();
			int index = random.nextInt(54);
			Card card = cardList.get(index);
			cardList.set(index, cardList.get(0));
			cardList.set(0, card);
		}

		lordCardList = new ArrayList<Card>();
		for(int i=0;i<cardList.size();i++){
			Card card = cardList.get(i);
			if(i >= 51){
				card.asynmove(new Point(300 + (i - 51) * 80, 10));
				lordCardList.add(card);
				continue;
			}
			switch ((i)%3) {
			case 0:
				card.asynmove(new Point(50,60+i*5));
				leftConputer.getCardHoldList().add(card);
				break;
			case 1:
				card.asynmove(new Point(180+i*7,450));
				userPlayer.getCardHoldList().add(card);
				card.show(); 
				break;
			case 2:
				card.asynmove(new Point(700,60+i*5));
				rightConputer.getCardHoldList().add(card);
				break;
			}
			container.setComponentZOrder(card.getLabel(), 0);
		}
		userPlayer.order();
		userPlayer.resetPosition(false);
		leftConputer.order();
		leftConputer.resetPosition(false);
		rightConputer.order();
		rightConputer.resetPosition(false); 
	}

	private void compete(){
		int startPosition = 1; 
		for(int i = startPosition; ;i++){
			Player player = getPlayer(i % 3);
			player.compete(30);
			if(player.isLord()){
				lordPosition = player.getPosition();
				break;
			}
		}

		for(Card card : lordCardList){
			card.show();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Player lord = getPlayer(lordPosition);
		lord.getCardHoldList().addAll(lordCardList);
		lord.order();
		lord.resetPosition(false);
		lordLabel.setLocation(lord.getLordPoint()); 
		lordLabel.setVisible(true); 

		for(Card card : userPlayer.getCardHoldList()){
			card.setClickable(true); 
		}
	}

	private void publish(){
		int publishTurn = lordPosition - 1;
		while (true) {
			Player player = getPlayer((++publishTurn) % 3);
			player.publish(15);
			if(player.getCardHoldList().isEmpty()){
				if(player.getPosition() == Player.POSITION_USER){
					JOptionPane.showMessageDialog(this, "you win!");
				}else{
					JOptionPane.showMessageDialog(this, "you loss!");
				}
				return;
			}
		}
	}

	public Player getPlayer(int position){
		switch(position){
		case Player.POSITION_LEFT:
			return leftConputer;
		case Player.POSITION_USER:
			return userPlayer;
		case Player.POSITION_RIGHT:
			return rightConputer;
		default:
			return null;
		}
	}

	private void showAbout(){
		JOptionPane.showMessageDialog(this, "version 1.0 shanhm1991@163.com");
	}

	public static void main(String[] args) {
		new BootFrame();
	}

}
