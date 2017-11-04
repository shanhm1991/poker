package com.poker;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

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

//		compete();
		
//		publish();
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

	/**
	 * 发牌
	 */
	private void initCard() {
		CardLabel card[] = new CardLabel[56];
		int count = 1;
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 13; j++) {
				if ((i == 5) && (j > 2))
					break;
				else {
					card[count] = new CardLabel(this, i + "-" + j,true);
					card[count].setLocation(350, 50);
					container.add(card[count]);
					count++;
				}
			}
		}
		for(int i=0;i<100;i++){
			Random random=new Random();
			int a=random.nextInt(54)+1;
			int b=random.nextInt(54)+1;
			CardLabel k = card[a];
			card[a] = card[b];
			card[b] = k;
		}
		lordCardList=new ArrayList<CardLabel>();
		int t=0;
		for(int i=1;i<=54;i++){
			if(i>=52){
				card[i].move(new Point(300+(i-52)*80,10));
				lordCardList.add(card[i]);
				continue;
			}
			switch ((t++)%3) {
			case 0:
				card[i].move(new Point(50,60+i*5));
				leftConputer.getCardHoldList().add(card[i]);
				break;
			case 1:
				card[i].move(new Point(180+i*7,450));
				userPlayer.getCardHoldList().add(card[i]);
				card[i].show(); 
				break;
			case 2:
				card[i].move(new Point(700,60+i*5));
				rightConputer.getCardHoldList().add(card[i]);
				break;
			}
			container.setComponentZOrder(card[i], 0);
		}
		
		
		
		
		
//		userPlayer.order();
//		userPlayer.resetPosition();
//		leftConputer.order();
//		leftConputer.resetPosition();
//		rightConputer.order();
//		rightConputer.resetPosition();
		
//		userPlayer.enableClickCard();
	}

	/**
	 * 抢地主
	 */
	private void compete() throws InterruptedException{
		int startPosition = 1; //new SecureRandom().nextInt(3);
		for(int i = startPosition; ;i++){
			CardPlayer player = getPlayer(i % 3);
			player.complete();
			if(player.isLord()){
				lordPosition = player.getPosition();
				System.out.println(lordPosition);
				break;
			}
		}
		showCards(lordCardList);
		Thread.sleep(2000);
		CardPlayer lord = getPlayer(lordPosition);
		lord.getCardHoldList().addAll(lordCardList);
		lord.order();
		lord.resetPosition();
		lordLabel.setLocation(lord.getLordPoint()); 
		lordLabel.setVisible(true); 
		lord.getClockFiled().setVisible(true);
	}
	
	/**
	 * 出牌
	 */
	private void publish(){ //TODO
		publishTurn = lordPosition - 1;
		while (true) {
			CardPlayer player = getPlayer((++publishTurn) % 3);
			player.publish();
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
	
	private void showCards(List<CardLabel> cardList){
		for(CardLabel card : cardList){
			card.show();
		}
	}

	private void showAbout(){
		JOptionPane.showMessageDialog(this, "version 1.0  by牧风-shanhm1991@163.com");
	}

	public static void main(String[] args) throws InterruptedException {
		new MainFrame();
	}
}
