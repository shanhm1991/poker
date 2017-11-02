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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MainFrame extends JFrame {

	private static final long serialVersionUID = -2875228669172807387L;

	public Container container = null;

	private JMenuItem start;

	private JMenuItem exit;

	private JMenuItem about;

	private JButton competeButton;

	private JButton notCompeteButton;

	private JButton publishButton;

	private JButton notPublishButton;
	
	private JLabel dizhuLabel; 

	private int turn;
	
	private int dizhuPosition;

	private CardPlayer player;

	private CardPlayer leftConputer;

	private CardPlayer rightConputer;

	private List<CardLabel> lordCardList;

	TurnThread turnThread; 

	boolean nextPlayer=false; //转换角色

	public MainFrame(){
		setSize(830, 620);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(getOwner());
		setTitle("斗地主 - shanhm1991@163.com");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		init();

		initCard();

		player.getClockFiled().setVisible(true);
		
		//线程安全性,把非主线程的UI控制放到里面
		SwingUtilities.invokeLater(new NewTimer(this,10));
	}

	public void init() {
		container = this.getContentPane();
		container.setLayout(null);
		container.setBackground(new Color(0, 112, 26)); 

		start = new JMenuItem("新游戏");
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// this.restart();
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

		competeButton = new JButton("抢地主");
		competeButton.setVisible(false);
		competeButton.setBounds(320, 400,75,20);
		competeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				player.getClockFiled().setText("抢地主");
				turnThread.isRun=false; 
			}
		});
		container.add(competeButton);

		notCompeteButton = new JButton("不  抢");
		notCompeteButton.setVisible(false);
		notCompeteButton.setBounds(420, 400,75,20);
		notCompeteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				player.getClockFiled().setText("不抢");
				turnThread.isRun=false; 
			}
		});
		container.add(notCompeteButton);

		publishButton= new JButton("出牌");
		publishButton.setVisible(false);
		publishButton.setBounds(320, 400, 60, 20);
		publishButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				player.publishCard();
//				playPublish();
			}
		});
		container.add(publishButton);

		notPublishButton= new JButton("不要");
		notPublishButton.setVisible(false);
		notPublishButton.setBounds(420, 400, 60, 20);
		notPublishButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				notPublish();
			}
		});
		container.add(notPublishButton);

		dizhuLabel=new JLabel(new ImageIcon("images/dizhu.gif"));
		dizhuLabel.setVisible(false);
		dizhuLabel.setSize(40, 40);
		container.add(dizhuLabel);

		player = new CardPlayer(this,CardPlayer.POSITION_PLAYER);
		leftConputer = new CardPlayer(this,CardPlayer.POSITION_LEFT);
		rightConputer = new CardPlayer(this,CardPlayer.POSITION_RIGHT);
	}

	/**
	 * 发牌
	 */
	public void initCard() {
		CardLabel card[] = new CardLabel[56];
		int count = 1;
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 13; j++) {
				if ((i == 5) && (j > 2))
					break;
				else {
					card[count] = new CardLabel(this, i + "-" + j, false);
					card[count].setLocation(350, 50);
					container.add(card[count]);
					count++;
				}
			}
		}
		//打乱顺序
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
				player.getCardHoldList().add(card[i]);
				card[i].turnUp(); 
				break;
			case 2:
				card[i].move(new Point(700,60+i*5));
				rightConputer.getCardHoldList().add(card[i]);
				break;
			}
			container.setComponentZOrder(card[i], 0);
		}

		player.order();
		leftConputer.order();
		rightConputer.order();

		leftConputer.resetPosition();
		rightConputer.resetPosition();
		player.resetPosition();
		
		competeButton.setVisible(true);
		notCompeteButton.setVisible(true);
	}

	private void notPublish(){
		nextPlayer=true;
		player.getCardPublishList().clear();
		player.getClockFiled().setText("不要");
	}

	private void showAbout(){
		JOptionPane.showMessageDialog(this, "version 1.0  by牧风-shanhm1991@163.com");
	}

	public CardPlayer getPlayer(int role){
		switch(role){
		case CardPlayer.POSITION_LEFT:
			return leftConputer;
		case CardPlayer.POSITION_PLAYER:
			return player;
		case CardPlayer.POSITION_RIGHT:
			return rightConputer;
		default:
			return null;
		}
	}


	public static void main(String args[]) {

		new MainFrame();

	}



}

class NewTimer implements Runnable{

	MainFrame main;
	int i;
	public NewTimer(MainFrame m,int i){
		this.main=m;
		this.i=i;
	}
	@Override
	public void run() {
		main.turnThread=new TurnThread(main,10);
		main.turnThread.start();
	}

}
