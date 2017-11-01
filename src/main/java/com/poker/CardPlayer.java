package com.poker;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTextField;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"position"},callSuper = false)
public class CardPlayer {

	/**
	 * 左手电脑
	 */
	public static final int POSITION_LEFT = 0;

	/**
	 * 玩家
	 */
	public static final int POSITION_PLAYER = 1;

	/**
	 * 右手电脑
	 */
	public static final int POSITION_RIGHT = 2;

	private List<CardLabel> cardList = new ArrayList<CardLabel>();

	private List<CardLabel> currentList = new ArrayList<CardLabel>();

	private Point point;

	private Integer position;

	private MainFrame frame;

	private boolean dizhu;

	private JTextField timeFiled;

	public CardPlayer(MainFrame frame, int role){
		this.position = role;
		this.frame = frame;
		timeFiled = new JTextField("倒计时:");
		timeFiled.setVisible(false);
		switch(role){
		case POSITION_LEFT:
			point = new Point(80,20);
			timeFiled.setBounds(140, 230, 60, 20);
			break;
		case POSITION_PLAYER:
			point = new Point(80,430);
			timeFiled.setBounds(374, 360, 60, 20);
			break;
		case POSITION_RIGHT:
			point = new Point(700,20);
			timeFiled.setBounds(620, 230, 60, 20);
			break;
		default:;
		}
		frame.container.add(timeFiled);
	}

	public void order(){
		Collections.sort(cardList,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel o1, CardLabel o2) {
				int a1=Integer.parseInt(o1.getName().substring(0, 1));//花色
				int a2=Integer.parseInt(o2.getName().substring(0,1));
				int b1=Integer.parseInt(o1.getName().substring(2,o1.getName().length()));//数值
				int b2=Integer.parseInt(o2.getName().substring(2,o2.getName().length()));
				int flag=0;
				//如果是王的话
				if(a1==5) b1+=100;
				if(a1==5&&b1==1) b1+=50;
				if(a2==5) b2+=100;
				if(a2==5&&b2==1) b2+=50;
				//如果是A或者2
				if(b1==1) b1+=20;
				if(b2==1) b2+=20;
				if(b1==2) b1+=30;
				if(b2==2) b2+=30;
				flag=b2-b1;
				if(flag==0)
					return a2-a1;
				else {
					return flag;
				}
			}
		});
	}

	public void resetPosition(){
		Point point=new Point();
		switch(position){
		case POSITION_LEFT:
			point.x=50;
			point.y=(450/2)-(cardList.size()+1)*15/2;
			break;
		case POSITION_PLAYER:
			point.x=(800/2)-(cardList.size()+1)*21/2;
			point.y=450;
			break;
		case POSITION_RIGHT:
			point.x=700;
			point.y=(450/2)-(cardList.size()+1)*15/2;
			break;
		default:;
		}
		int len=cardList.size();
		for(int i=0;i<len;i++){
			CardLabel card=cardList.get(i);
			card.move(point);
			frame.container.setComponentZOrder(card, 0);
			if(position==1)point.x+=21;
			else point.y+=15;
		}
	}

	public int getScore(){
		int score=0;
		for(int i=0,len=cardList.size();i<len;i++){
			CardLabel card=cardList.get(i);
			if(card.getName().substring(0, 1).equals("5")){
				//System.out.println(card.name.substring(0, 1));
				score+=5;
			}
			if(card.getName().substring(2, card.getName().length()).equals("2")){
				//System.out.println(2);
				score+=2;
			}
		}
		return score;
	}

	public void publishCard() {//int position
		//		CardPlayer player = frame.getPlayer(position);
		Model model = Common.getModel(cardList);
		// 待走的牌
		List<String> list = new ArrayList();

		// 主动出牌
		if (frame.getPlayer((position + 1) % 3).getTimeFiled().getText().equals("不要")
				&& frame.getPlayer((position + 2) % 3).getTimeFiled().getText().equals("不要")) {
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
			CardPlayer prePlayer = frame.getPlayer((position + 2) % 3);
			CardPlayer nextPlayer = frame.getPlayer((position + 1) % 3);
			List<CardLabel> cardList = prePlayer.getCurrentList();
			if(cardList.isEmpty()){
				cardList = nextPlayer.getCurrentList();
			}

			switch(CardType.getType(cardList)){
			case CardType.T1: 

			}

			int cType=CardType.getType(cardList);
			//如果是单牌
			if(cType==CardType.T1)
			{
				AI_1(model.a1, cardList, list, position);
			}//如果是对子
			else if(cType==CardType.T2)
			{
				AI_1(model.a2, cardList, list, position);
			}//3带
			else if(cType==CardType.T3)
			{
				AI_1(model.a3, cardList, list, position);
			}//炸弹
			else if(cType==CardType.T4)
			{
				AI_1(model.a4, cardList, list, position);
			}//如果是3带1
			else if(cType==CardType.T31){
				//偏家 涉及到拆牌
				//if((role+1)%3==main.dizhuFlag)
				AI_2(model.a3, model.a1, cardList, list);
			}//如果是3带2
			else if(cType==CardType.T32){
				//偏家
				//if((role+1)%3==main.dizhuFlag)
				AI_2(model.a3, model.a2, cardList, list);
			}//如果是4带11
			else if(cType==CardType.T411){
				AI_5(model.a4, model.a1, cardList, list, position);
			}
			//如果是4带22
			else if(cType==CardType.T422){
				AI_5(model.a4, model.a2, cardList, list, position);
			}
			//顺子
			else if(cType==CardType.T123){
				AI_3(model.a123, cardList, list, position);
			}
			//双顺
			else if(cType==CardType.T1122){
				AI_3(model.a112233, cardList, list, position);
			}
			//飞机带单
			else if(cType==CardType.T11122234){
				AI_4(model.a111222,model.a1, cardList, list, position);
			}
			//飞机带对
			else if(cType==CardType.T1112223344){
				AI_4(model.a111222,model.a2, cardList, list, position);
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
		currentList.clear();
		if (list.size() > 0) {
			Point point = new Point();
			if (position == 0)
				point.x = 200;
			if (position == 2)
				point.x = 550;
			point.y = (400 / 2) - (list.size() + 1) * 15 / 2;// 屏幕中部
			// 将name转换成Card
			for (int i = 0, len = list.size(); i < len; i++) {
				List<CardLabel> cards = getCardByName(cardList,list.get(i));
				for (CardLabel card : cards) {
					card.move(point);
					point.y += 15;
					currentList.add(card);
					cardList.remove(card);
				}
			}
			resetPosition();
		} else {
			frame.getPlayer(position).getTimeFiled().setVisible(true);
			frame.getPlayer(position).getTimeFiled().setText("不要");
		}
		for(CardLabel card : currentList){
			card.turnUp();
		}
	}

	private List getCardByName(List<CardLabel> list, String n) {
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

	//单牌，对子，3个，4个,通用
	private void AI_1(List<String> model,List<CardLabel> player,List<String> list,int position){
		//顶家
		if((position+1)%3 == frame.getDizhuPosition()){
			for(int i=0,len=model.size();i<len;i++){
				if(getValueInt(model.get(i)) > player.get(0).getValue()){
					list.add(model.get(i));
					break;
				}
			}
		}else {//偏家
			for(int len=model.size(),i=len-1;i>=0;i--){
				if(getValueInt(model.get(i)) > player.get(0).getValue()){
					list.add(model.get(i));
					break;
				}
			}
		}
	}

	//3带1,2,4带1,2
	private void AI_2(List<String> model1,List<String> model2,List<CardLabel> player,List<String> list){
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

	//通过name估值
	private  int getValueInt(String n){
		String name[]=n.split(",");
		String s=name[0];
		int i=Integer.parseInt(s.substring(2, s.length()));
		if(s.substring(0, 1).equals("5"))
			i+=3;
		if(s.substring(2, s.length()).equals("1")||s.substring(2, s.length()).equals("2"))
			i+=13;
		return i;
	}

	//4带1，2
	private void AI_5(List<String> model1,List<String> model2,List<CardLabel> player,List<String> list,int role){
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

	//顺子
	private void AI_3(List<String> model,List<CardLabel> player,List<String> list,int role){

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
	private void AI_4(List<String> model1,List<String> model2,List<CardLabel> player,List<String> list,int role){
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
}
