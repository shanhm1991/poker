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
import javax.swing.SwingUtilities;

import com.poker.player.CardPlayer;
import com.poker.player.PlayerConputer;
import com.poker.player.PlayerUser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MainFrame extends JFrame {

	private static final long serialVersionUID = -2875228669172807387L;

	private volatile int competeSeconds = 10;

	private volatile boolean completeEnd = false;

	public Container container = null;

	private JMenuItem start;

	private JMenuItem exit;

	private JMenuItem about;

	private JLabel lordLabel; 

	private int publishTurn;

	private int lordPosition;

	private CardPlayer userPlayer;

	private CardPlayer leftConputer;

	private CardPlayer rightConputer;

	private List<CardLabel> lordCardList;

	boolean nextPlayer = false;

	public MainFrame() throws InterruptedException{ 
		setSize(830, 620);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(getOwner());
		setTitle("斗地主 - shanhm1991@163.com");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initFrame();
		userPlayer = new PlayerUser(this,CardPlayer.POSITION_USER);
		leftConputer = new PlayerConputer(this,CardPlayer.POSITION_LEFT);
		rightConputer = new PlayerConputer(this,CardPlayer.POSITION_RIGHT);

		initCard();
		compete();
		publish();
	}

	private void initFrame() {
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
		List<CardLabel> cardList = new LinkedList<CardLabel>();
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 13; j++) {
				if ((i == 5) && (j > 2)){
					break;
				}
				CardLabel card = new CardLabel(this, i + "-" + j);
				card.setLocation(350, 50);
				container.add(card);
				cardList.add(card);
			}
		}
		for(int i=0;i<100;i++){
			SecureRandom random = new SecureRandom();
			int index = random.nextInt(54);
			CardLabel card = cardList.get(index);
			cardList.set(index, cardList.get(0));
			cardList.set(0, card);
		}

		lordCardList = new ArrayList<CardLabel>();
		for(int i=0;i<cardList.size();i++){
			CardLabel card = cardList.get(i);
			if(i >= 51){
				card.move(new Point(300 + (i - 51) * 80, 10));
				lordCardList.add(card);
				continue;
			}
			switch ((i)%3) {
			case 0:
				card.move(new Point(50,60+i*5));
				leftConputer.getCardHoldList().add(card);
				break;
			case 1:
				card.move(new Point(180+i*7,450));
				userPlayer.getCardHoldList().add(card);
				card.turnUp(); 
				break;
			case 2:
				card.move(new Point(700,60+i*5));
				rightConputer.getCardHoldList().add(card);
				break;
			}
			container.setComponentZOrder(card, 0);
		}
		userPlayer.order();
		userPlayer.resetPosition();
		leftConputer.order();
		leftConputer.resetPosition();
		rightConputer.order();
		rightConputer.resetPosition();
	}

	private void compete(){
		int startPosition = 1; 
		for(int i = startPosition; ;i++){
			CardPlayer player = getPlayer(i % 3);
			player.compete(30);
			if(player.isLord()){
				lordPosition = player.getPosition();
				break;
			}
		}
		for(CardLabel card : lordCardList){
			card.turnUp();
		}
		CardPlayer lord = getPlayer(lordPosition);
		lord.getCardHoldList().addAll(lordCardList);
		lord.order();
		lord.resetPosition();
		lordLabel.setLocation(lord.getLordPoint()); 
		lordLabel.setVisible(true); 
		lord.getClockFiled().setVisible(true);
	}

	private void publish(){
		publishTurn = lordPosition - 1;
		while (true) {
			CardPlayer player = getPlayer((++publishTurn) % 3);
			player.publish(15);
			if(player.getCardHoldList().isEmpty()){
				if(player.getPosition() == CardPlayer.POSITION_USER){
					JOptionPane.showMessageDialog(this, "you win!");
				}else{
					JOptionPane.showMessageDialog(this, "you loss!");
				}
			}
		}
	}

	public CardPlayer getPlayer(int position){
		switch(position){
		case CardPlayer.POSITION_LEFT:
			return leftConputer;
		case CardPlayer.POSITION_USER:
			return userPlayer;
		case CardPlayer.POSITION_RIGHT:
			return rightConputer;
		default:
			return null;
		}
	}

	private void showAbout(){
		JOptionPane.showMessageDialog(this, "version 1.0 shanhm1991@163.com");
	}

	public static void main(String[] args) throws InterruptedException {
		new MainFrame();
	}
}
