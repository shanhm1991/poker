package com.poker;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class TurnThread extends Thread {
	MainFrame main;
	boolean isRun = true;
	int i = 10;

	public TurnThread(MainFrame m, int i) {
		this.main = m;
		this.i = i;
	}

	@Override
	public void run() {
		while (i > -1 && isRun) {
			main.getPlayer().getClockFiled().setText("倒计时:" + i--);
			second(1);
		}
		if (i == -1){
			main.getPlayer().getClockFiled().setText("不抢");
		}

		main.getCompeteButton().setVisible(false);
		main.getNotCompeteButton().setVisible(false);

		CardPlayer player = main.getPlayer();
		CardPlayer leftConputer = main.getLeftConputer();
		CardPlayer rightConputer = main.getRightConputer();
		for (CardLabel card : player.getCardHoldList()){
			card.setClickable(true);
		}

		Point point = null;
		if (main.getPlayer().getClockFiled().getText().equals("抢地主")) {
			// 得到地主牌
			player.getCardHoldList().addAll(main.getLordCardList());
			openlord(true);
			second(2);// 等待五秒
			player.order();
			player.resetPosition();
			point = player.getPoint();
			player.setDizhu(true);
			main.setDizhuPosition(CardPlayer.POSITION_PLAYER);
		} else {
			if (leftConputer.getScore() < rightConputer.getScore()) {
				main.getRightConputer().getClockFiled().setText("抢地主");
				main.getRightConputer().getClockFiled().setVisible(true);
				openlord(true);
				second(3);
				point = rightConputer.getPoint();
				rightConputer.setDizhu(true); 
				rightConputer.getCardHoldList().addAll(main.getLordCardList());
				rightConputer.order();
				rightConputer.resetPosition();
				openlord(false);
				main.setDizhuPosition(CardPlayer.POSITION_RIGHT);
			} else {
				main.getLeftConputer().getClockFiled().setText("抢地主");
				main.getLeftConputer().getClockFiled().setVisible(true);
				openlord(true);
				second(3);
				leftConputer.setDizhu(true); 
				point = leftConputer.getPoint();
				leftConputer.getCardHoldList().addAll(main.getLordCardList());
				leftConputer.order();
				leftConputer.resetPosition();
				main.setDizhuPosition(CardPlayer.POSITION_LEFT);
				//openlord(false);
			}
		}

		main.getDizhuLabel().setLocation(point);
		main.getDizhuLabel().setVisible(true);

		main.getCompeteButton().setVisible(false);
		main.getNotCompeteButton().setVisible(false);

		turnOn(false);

		main.getPlayer().getClockFiled().setText("不要");
		main.getPlayer().getClockFiled().setVisible(false);
		main.getLeftConputer().getClockFiled().setText("不要");
		main.getLeftConputer().getClockFiled().setVisible(false);
		main.getRightConputer().getClockFiled().setText("不要");
		main.getRightConputer().getClockFiled().setVisible(false);

		// 开始游戏 根据地主不同顺序不同
		main.setTurn(main.getDizhuPosition());  

		while (true) {
			switch(main.getTurn()){
			case CardPlayer.POSITION_PLAYER:
				turnOn(true);// 出牌按钮 --我出牌
				timeWait(30, 1);// 我自己的定时器
				turnOn(false);//选完关闭
				main.setTurn((main.getTurn()+1)%3);
				if(win()){
					return;
				}
				break;
			case CardPlayer.POSITION_LEFT:
				timeWait(1, 0); 
				main.getPlayer(CardPlayer.POSITION_LEFT).publishCard();
				main.setTurn((main.getTurn()+1)%3);
				if(win()){
					return;
				}
				break;
			case CardPlayer.POSITION_RIGHT:
				timeWait(1, 2); 
				main.getPlayer(CardPlayer.POSITION_RIGHT).publishCard();
				main.setTurn((main.getTurn()+1)%3);
				if(win()){
					return;
				}
				break;
			}
		}
	}

	// 等待i秒
	public void second(int i) {
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 地主牌翻看
	public void openlord(boolean is) {
		for (int i = 0; i < 3; i++) {
			if (is)
				main.getLordCardList().get(i).turnUp(); // 地主牌翻看
			else {
				main.getLordCardList().get(i).turnBack(); // 地主牌闭合
			}
			main.getLordCardList().get(i).setClickable(true); 
		}
	}

	// 打开出牌按钮
	public void turnOn(boolean flag) {
		main.getPublishButton().setVisible(flag);
		main.getNotPublishButton().setVisible(flag);
	}

	// 按name获得Card，方便从Model取出
	public List<CardLabel> getCardByName(List<CardLabel> list, String n) {
		String[] name = n.split(",");
		List<CardLabel> cardsList = new ArrayList<CardLabel>();
		int j = 0;
		for (int i = 0, len = list.size(); i < len; i++) {
			if (j < name.length && list.get(i).getName().equals(name[j])) {
				cardsList.add(list.get(i));
				i = 0;
				j++;
			}
		}
		return cardsList;
	}
	// 延时，模拟时钟
	public void timeWait(int n, int position) {
		List<CardLabel> cardList = main.getPlayer(position).getCardPublishList();
		
		

		if (cardList.size() > 0)
			hideCards(cardList);
		
		
		if (position == 1)// 如果是我，10秒到后直接下一家出牌
		{
			int i = n;

			while (main.nextPlayer == false && i >= 0) {
				// main.container.setComponentZOrder(main.time[player], 0);

				main.getPlayer(position).getClockFiled().setText("倒计时:" + i);
				main.getPlayer(position).getClockFiled().setVisible(true);
				second(1);
				i--;
			}
			if (i == -1) {
				main.getPlayer(position).getClockFiled().setText("超时");
			}
			main.nextPlayer = false;
		} else {
			for (int i = n; i >= 0; i--) {
				second(1);
				main.getPlayer(position).getClockFiled().setText("倒计时:" + i);
				main.getPlayer(position).getClockFiled().setVisible(true);
			}
		}
		main.getPlayer(position).getClockFiled().setVisible(false);
	}
	
	private void hideCards(List<CardLabel> list){
		for(int i=0,len=list.size();i<len;i++){
			list.get(i).setVisible(false);
		}
	}

	//判断输赢
	public boolean win(){
		for(int i=0;i<3;i++){
			if(main.getPlayer().getCardHoldList().isEmpty()){
				String s;
				if(i==1){
					s="恭喜你，胜利了!";
				}else {
					s="恭喜电脑"+i+",赢了! 你的智商有待提高哦";
				}
				JOptionPane.showMessageDialog(main, s);
				return true;
			}
		}
		return false;
	}
}
