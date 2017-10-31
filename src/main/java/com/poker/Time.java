package com.poker;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Time extends Thread {
	MainFrame main;
	boolean isRun = true;
	int i = 10;

	public Time(MainFrame m, int i) {
		this.main = m;
		this.i = i;
	}

	@Override
	public void run() {

		while (i > -1 && isRun) {
			main.getPlayer().getTimeFiled().setText("倒计时:" + i--);
			second(1);
		}
		if (i == -1)// 正常终结，说明超时
			main.getPlayer().getTimeFiled().setText("不抢");

		main.getCompeteButton().setVisible(false);
		main.getNotCompeteButton().setVisible(false);

		CardPlayer player = main.getPlayer();

		//		List<CardLabel> myList = main.getMePlayer().getCardList();
		for (CardLabel card : player.getCardList())
			card.setClickable(true);
		if (main.getPlayer().getTimeFiled().getText().equals("抢地主")) {
			// 得到地主牌
			player.getCardList().addAll(main.lordList);
			openlord(true);
			second(2);// 等待五秒
			player.order();
			player.resetPosition();
			setlord(1);
		} else {
			CardPlayer leftConputer = main.getLeftConputer();
			CardPlayer rightConputer = main.getRightConputer();
			if (leftConputer.getScore() < rightConputer.getScore()) {
				main.getRightConputer().getTimeFiled().setText("抢地主");
				main.getRightConputer().getTimeFiled().setVisible(true);
				setlord(2);// 设定地主
				openlord(true);
				second(3);
				rightConputer.getCardList().addAll(main.lordList);
				rightConputer.order();
				rightConputer.resetPosition();
				openlord(false);
			} else {
				main.getLeftConputer().getTimeFiled().setText("抢地主");
				main.getLeftConputer().getTimeFiled().setVisible(true);
				setlord(0);// 设定地主
				openlord(true);
				second(3);
				leftConputer.getCardList().addAll(main.lordList);
				leftConputer.order();
				leftConputer.resetPosition();
				//openlord(false);
			}
		}
		// 选完地主后 关闭地主按钮
		//		main.getLandlord()[0].setVisible(false);
		//		main.getLandlord()[1].setVisible(false);

		main.getCompeteButton().setVisible(false);
		main.getNotCompeteButton().setVisible(false);

		turnOn(false);
		main.getPlayer().getTimeFiled().setText("不要");
		main.getPlayer().getTimeFiled().setVisible(false);
		main.getLeftConputer().getTimeFiled().setText("不要");
		main.getLeftConputer().getTimeFiled().setVisible(false);
		main.getRightConputer().getTimeFiled().setText("不要");
		main.getRightConputer().getTimeFiled().setVisible(false);
		
		// 开始游戏 根据地主不同顺序不同
		main.setTurn(main.getDizhuFlag());  

		while (true) {
			if(main.getTurn()==1) {
				turnOn(true);// 出牌按钮 --我出牌
				timeWait(30, 1);// 我自己的定时器
				turnOn(false);//选完关闭
				main.setTurn((main.getTurn()+1)%3);
				if(win())
					break;
			}

			if (main.getTurn()==0) {
				computer0();
				main.setTurn((main.getTurn()+1)%3);
				if(win())
					break;
			}

			if(main.getTurn()==2){
				computer2();
				main.setTurn((main.getTurn()+1)%3);
				if(win())
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
				main.lordList.get(i).turnUp(); // 地主牌翻看
			else {
				main.lordList.get(i).turnBack(); // 地主牌闭合
			}
			main.lordList.get(i).setClickable(true); 
		}
	}

	// 设定地主
	public void setlord(int i) {
		Point point = new Point();
		if (i == 1)// 我是地主
		{
			point.x = 80;
			point.y = 430;
			main.setDizhuFlag(1);// 设定地主
		}
		if (i == 0) {
			point.x = 80;
			point.y = 20;
			main.setDizhuFlag(0);
		}
		if (i == 2) {
			point.x = 700;
			point.y = 20;
			main.setDizhuFlag(2);
		}
		main.getDizhu().setLocation(point);
		main.getDizhu().setVisible(true);
	}

	// 打开出牌按钮
	public void turnOn(boolean flag) {
		main.getPublishButton().setVisible(flag);
		main.getNotPublishButton().setVisible(flag);
	}

	// 电脑0走牌(我代表1)
	public void computer0() {
		timeWait(1, 0); // 定时
		ShowCard(0); // 出牌

	}

	// 电脑2走牌(我代表1)
	public void computer2() {
		timeWait(1, 2); // 定时
		ShowCard(2); // 出牌

	}

	// 走牌
	public void ShowCard(int role) {
		CardPlayer player1 = main.getPlayer(role);

		Model model = Common.getModel(player1.getCardList());
		
		// 待走的牌
		List<String> list = new ArrayList();
		
		// 主动出牌
		if (main.getPlayer((role + 1) % 3).getTimeFiled().getText().equals("不要")
				&& main.getPlayer((role + 2) % 3).getTimeFiled().getText().equals("不要")) {
			// 有单出单 (除开3带，飞机能带的单牌)
			if (model.a1.size() > (model.a111222.size() * 2 + model.a3.size())) {
				list.add(model.a1.get(model.a1.size() - 1));
			}// 有对子出对子 (除开3带，飞机)
			else if (model.a2.size() > (model.a111222.size() * 2 + model.a3
					.size())) {
				list.add(model.a2.get(model.a2.size() - 1));
			}// 有顺子出顺子
			else if (model.a123.size() > 0) {
				list.add(model.a123.get(model.a123.size() - 1));
			}// 有3带就出3带，没有就出光3
			else if (model.a3.size() > 0) {
				// 3带单,且非关键时刻不能带王，2
				if (model.a1.size() > 0) {
					list.add(model.a1.get(model.a1.size() - 1));
				}// 3带对
				else if (model.a2.size() > 0) {
					list.add(model.a2.get(model.a2.size() - 1));
				}
				list.add(model.a3.get(model.a3.size() - 1));
			}// 有双顺出双顺
			else if (model.a112233.size() > 0) {
				list.add(model.a112233.get(model.a112233.size() - 1));
			}// 有飞机出飞机
			else if (model.a111222.size() > 0) {
				String name[] = model.a111222.get(0).split(",");
				// 带单
				if (name.length / 3 <= model.a1.size()) {
					list.add(model.a111222.get(model.a111222.size() - 1));
					for (int i = 0; i < name.length / 3; i++)
						list.add(model.a1.get(i));
				} else if (name.length / 3 <= model.a2.size())// 带双
				{
					list.add(model.a111222.get(model.a111222.size() - 1));
					for (int i = 0; i < name.length / 3; i++)
						list.add(model.a2.get(i));
				}
				// 有炸弹出炸弹
			} else if (model.a4.size() > 0) {
				// 4带2,1
				int sizea1 = model.a1.size();
				int sizea2 = model.a2.size();
				if (sizea1 >= 2) {
					list.add(model.a1.get(sizea1 - 1));
					list.add(model.a1.get(sizea1 - 2));
					list.add(model.a4.get(0));

				} else if (sizea2 >= 2) {
					list.add(model.a2.get(sizea1 - 1));
					list.add(model.a2.get(sizea1 - 2));
					list.add(model.a4.get(0));

				} else {// 直接炸
					list.add(model.a4.get(0));

				}

			}
		}// 如果是跟牌
		else {
			List<CardLabel> player = main.getCurrentList()[(role + 2) % 3].size() > 0 
					? main.getCurrentList()[(role + 2) % 3]
							: main.getCurrentList()[(role + 1) % 3];

					int cType=CardType.getType(player);
					//如果是单牌
					if(cType==CardType.T1)
					{
						AI_1(model.a1, player, list, role);
					}//如果是对子
					else if(cType==CardType.T2)
					{
						AI_1(model.a2, player, list, role);
					}//3带
					else if(cType==CardType.T3)
					{
						AI_1(model.a3, player, list, role);
					}//炸弹
					else if(cType==CardType.T4)
					{
						AI_1(model.a4, player, list, role);
					}//如果是3带1
					else if(cType==CardType.T31){
						//偏家 涉及到拆牌
						//if((role+1)%3==main.dizhuFlag)
						AI_2(model.a3, model.a1, player, list, role);
					}//如果是3带2
					else if(cType==CardType.T32){
						//偏家
						//if((role+1)%3==main.dizhuFlag)
						AI_2(model.a3, model.a2, player, list, role);
					}//如果是4带11
					else if(cType==CardType.T411){
						AI_5(model.a4, model.a1, player, list, role);
					}
					//如果是4带22
					else if(cType==CardType.T422){
						AI_5(model.a4, model.a2, player, list, role);
					}
					//顺子
					else if(cType==CardType.T123){
						AI_3(model.a123, player, list, role);
					}
					//双顺
					else if(cType==CardType.T1122){
						AI_3(model.a112233, player, list, role);
					}
					//飞机带单
					else if(cType==CardType.T11122234){
						AI_4(model.a111222,model.a1, player, list, role);
					}
					//飞机带对
					else if(cType==CardType.T1112223344){
						AI_4(model.a111222,model.a2, player, list, role);
					}
					//炸弹
					if(list.size()==0)
					{
						int len4=model.a4.size();
						if(len4>0)
							list.add(model.a4.get(len4-1));
					}
		}

		// 定位出牌
		main.getCurrentList()[role].clear();
		if (list.size() > 0) {
			Point point = new Point();
			if (role == 0)
				point.x = 200;
			if (role == 2)
				point.x = 550;
			point.y = (400 / 2) - (list.size() + 1) * 15 / 2;// 屏幕中部
			// 将name转换成Card
			for (int i = 0, len = list.size(); i < len; i++) {
				List<CardLabel> cards = getCardByName(player1.getCardList(),list.get(i));
				for (CardLabel card : cards) {
					card.move(point);
					point.y += 15;
					main.getCurrentList()[role].add(card);
					player1.getCardList().remove(card);
				}
			}
			player1.resetPosition();
		} else {
			main.getPlayer(role).getTimeFiled().setVisible(true);
			main.getPlayer(role).getTimeFiled().setText("不要");
		}
		for(CardLabel card:main.getCurrentList()[role])
			card.turnUp();
	}

	// 按name获得Card，方便从Model取出
	public List getCardByName(List<CardLabel> list, String n) {
		String[] name = n.split(",");
		List cardsList = new ArrayList<CardLabel>();
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
	//顺子
	public void AI_3(List<String> model,List<CardLabel> player,List<String> list,int role){

		for(int i=0,len=model.size();i<len;i++)
		{
			String []s=model.get(i).split(",");
			if(s.length==player.size()&&getValueInt(model.get(i)) > player.get(0).getValue())
			{
				list.add(model.get(i));
				return;
			}
		}
	}
	//飞机带单，双
	public void AI_4(List<String> model1,List<String> model2,List<CardLabel> player,List<String> list,int role){
		//排序按重复数
		player=Common.getOrder2(player);
		int len1=model1.size();
		int len2=model2.size();

		if(len1<1 || len2<1)
			return;
		for(int i=0;i<len1;i++){
			String []s=model1.get(i).split(",");
			String []s2=model2.get(0).split(",");
			if((s.length/3<=len2)&&(s.length*(3+s2.length)==player.size())&&getValueInt(model1.get(i)) > player.get(0).getValue())
			{
				list.add(model1.get(i));
				for(int j=1;j<=s.length/3;j++)
					list.add(model2.get(len2-j));
			}
		}
	}
	//4带1，2
	public void AI_5(List<String> model1,List<String> model2,List<CardLabel> player,List<String> list,int role){
		//排序按重复数
		player=Common.getOrder2(player);
		int len1=model1.size();
		int len2=model2.size();

		if(len1<1 || len2<2)
			return;
		for(int i=0;i<len1;i++){
			if(getValueInt(model1.get(i)) > player.get(0).getValue())
			{
				list.add(model1.get(i));
				for(int j=1;j<=2;j++)
					list.add(model2.get(len2-j));
			}
		}
	}
	//单牌，对子，3个，4个,通用
	public void AI_1(List<String> model,List<CardLabel> player,List<String> list,int role){
		//顶家
		if((role+1)%3==main.getDizhuFlag())
		{

			for(int i=0,len=model.size();i<len;i++)
			{
				if(getValueInt(model.get(i)) > player.get(0).getValue())
				{
					list.add(model.get(i));
					break;
				}
			}
		}else {//偏家

			for(int len=model.size(),i=len-1;i>=0;i--)
			{
				if(getValueInt(model.get(i)) > player.get(0).getValue())
				{
					list.add(model.get(i));
					break;
				}
			}
		}
	}
	//3带1,2,4带1,2
	public void AI_2(List<String> model1,List<String> model2,List<CardLabel> player,List<String> list,int role){
		//model1是主牌,model2是带牌,player是玩家出的牌,,list是准备回的牌
		//排序按重复数
		player=Common.getOrder2(player);
		int len1=model1.size();
		int len2=model2.size();
		//如果有王直接炸了
		if(len1>0&&model1.get(0).length()<10)
		{
			list.add(model1.get(0));
			System.out.println("王炸");
			return;
		}
		if(len1<1 || len2<1)
			return;
		for(int len=len1,i=len-1;i>=0;i--)
		{	
			if(getValueInt(model1.get(i)) > player.get(0).getValue())
			{
				list.add(model1.get(i));
				break;
			}
		} 
		list.add(model2.get(len2-1));
		if(list.size()<2)
			list.clear();
	}
	// 延时，模拟时钟
	public void timeWait(int n, int player) {
		
		System.out.println(main == null);

		if (main.getCurrentList()[player].size() > 0)
			Common.hideCards(main.getCurrentList()[player]);
		if (player == 1)// 如果是我，10秒到后直接下一家出牌
		{
			int i = n;

			while (main.nextPlayer == false && i >= 0) {
				// main.container.setComponentZOrder(main.time[player], 0);
				
				main.getPlayer(player).getTimeFiled().setText("倒计时:" + i);
				main.getPlayer(player).getTimeFiled().setVisible(true);
				second(1);
				i--;
			}
			if (i == -1) {
				main.getPlayer(player).getTimeFiled().setText("超时");
			}
			main.nextPlayer = false;
		} else {
			for (int i = n; i >= 0; i--) {
				second(1);
				// main.container.setComponentZOrder(main.time[player], 0);
				main.getPlayer(player).getTimeFiled().setText("倒计时:" + i);
				main.getPlayer(player).getTimeFiled().setVisible(true);
			}
		}
		main.getPlayer(player).getTimeFiled().setVisible(false);
	}
	//通过name估值
	public  int getValueInt(String n){
		String name[]=n.split(",");
		String s=name[0];
		int i=Integer.parseInt(s.substring(2, s.length()));
		if(s.substring(0, 1).equals("5"))
			i+=3;
		if(s.substring(2, s.length()).equals("1")||s.substring(2, s.length()).equals("2"))
			i+=13;
		return i;
	}

	//判断输赢
	public boolean win(){
//		for(int i=0;i<3;i++){
//			if(main.playerList[i].size()==0)
//			{
//				String s;
//				if(i==1)
//				{
//					s="恭喜你，胜利了!";
//				}else {
//					s="恭喜电脑"+i+",赢了! 你的智商有待提高哦";
//				}
//				JOptionPane.showMessageDialog(main, s);
//				return true;
//			}
//		}
		return false;
	}
}
