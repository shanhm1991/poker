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

		player.getTimeFiled().setVisible(true);
		
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
				player.getTimeFiled().setText("抢地主");
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
				player.getTimeFiled().setText("不抢");
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
				playPublish();
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
				leftConputer.getCardList().add(card[i]);
				break;
			case 1:
				card[i].move(new Point(180+i*7,450));
				player.getCardList().add(card[i]);
				card[i].turnUp(); 
				break;
			case 2:
				card[i].move(new Point(700,60+i*5));
				rightConputer.getCardList().add(card[i]);
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

	private void playPublish(){
		List<CardLabel> publishCards = new ArrayList<CardLabel>();
		List<CardLabel> myCards = player.getCardList();
		for(int i = 0; i < myCards.size(); i++){
			CardLabel card = myCards.get(i);
			if(card.isClicked()){
				publishCards.add(card);
			}
		}
		int flag=0;

		//主动出牌or跟牌
		if(leftConputer.getTimeFiled().getText().equals("不要") && rightConputer.getTimeFiled().getText().equals("不要")){
			if(CardType.getType(publishCards)!=CardType.T0)
				flag=1;//表示可以出牌
		}else{
			flag=checkCards(publishCards);
		}

		//判断是否符合出牌
		if(flag==1){
			player.setCurrentList(publishCards);
			player.getCardList().removeAll(publishCards);//移除走的牌

			Point point=new Point();
			point.x=(770/2)-(publishCards.size()+1)*15/2;;
			point.y=300;
			for(int i=0,len=publishCards.size();i<len;i++){
				publishCards.get(i).move(point);
				point.x+=15;
			}

			player.resetPosition();
			player.getTimeFiled().setVisible(false);
			this.nextPlayer=true;
		}
	}

	private void notPublish(){
		nextPlayer=true;
		player.getCurrentList().clear();
		player.getTimeFiled().setText("不要");
	}

	//检查牌的是否能出
	private  int checkCards(List<CardLabel> c){
		//找出当前最大的牌是哪个电脑出的,c是点选的牌
		List<CardLabel> currentlist = leftConputer.getCurrentList();
		if(currentlist.isEmpty()){
			currentlist = rightConputer.getCurrentList();
		}
				
		int cType=CardType.getType(c);
		//如果张数不同直接过滤
		if(cType!=CardType.T4&&c.size()!=currentlist.size())
			return 0;
		//比较我的出牌类型
		if(CardType.getType(c)!= CardType.getType(currentlist))
		{

			return 0;
		}
		//比较出的牌是否要大
		//王炸弹
		if(cType==CardType.T4)
		{
			if(c.size()==2)
				return 1;
			if(currentlist.size()==2)
				return 0;
		}
		//单牌,对子,3带,4炸弹
		if(cType==CardType.T1||cType==CardType.T2||cType==CardType.T3||cType==CardType.T4){
			if(c.get(0).singleValue() <= currentlist.get(0).singleValue())
			{
				return 0;
			}else {
				return 1;
			}
		}
		//顺子,连队，飞机裸
		if(cType==CardType.T123||cType==CardType.T1122||cType==CardType.T111222)
		{
			if(c.get(0).value() <= currentlist.get(0).value())
				return 0;
			else 
				return 1;
		}
		//按重复多少排序
		//3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的 
		if(cType==CardType.T31||cType==CardType.T32||cType==CardType.T411||cType==CardType.T422
				||cType==CardType.T11122234||cType==CardType.T1112223344){
			List<CardLabel> a1=Common.getOrder2(c); //我出的牌
			List<CardLabel> a2=Common.getOrder2(currentlist);//当前最大牌
			if(a1.get(0).value() < a2.get(0).value())
				return 0;
		}
		return 1;
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
