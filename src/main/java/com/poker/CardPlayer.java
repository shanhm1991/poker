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

	private List<CardLabel> cardHoldList = new ArrayList<CardLabel>();

	private List<CardLabel> cardPublishList = new ArrayList<CardLabel>();

	private Point point;

	private Integer position;

	private MainFrame frame;

	private boolean dizhu;

	private JTextField clockFiled;

	public CardPlayer(MainFrame frame, int role){
		this.position = role;
		this.frame = frame;
		clockFiled = new JTextField("倒计时:");
		clockFiled.setVisible(false);
		switch(role){
		case POSITION_LEFT:
			point = new Point(80,20);
			clockFiled.setBounds(140, 230, 60, 20);
			break;
		case POSITION_PLAYER:
			point = new Point(80,430);
			clockFiled.setBounds(374, 360, 60, 20);
			break;
		case POSITION_RIGHT:
			point = new Point(700,20);
			clockFiled.setBounds(620, 230, 60, 20);
			break;
		default:;
		}
		frame.container.add(clockFiled);
	}

	public void order(){
		Collections.sort(cardHoldList,new Comparator<CardLabel>() {
			@Override
			public int compare(CardLabel c1, CardLabel c2) {
				return c2.singleValue() - c1.singleValue();
			}
		});
	}

	public void resetPosition(){
		Point point=new Point();
		switch(position){
		case POSITION_LEFT:
			point.x=50;
			point.y=(450/2)-(cardHoldList.size()+1)*15/2;
			break;
		case POSITION_PLAYER:
			point.x=(800/2)-(cardHoldList.size()+1)*21/2;
			point.y=450;
			break;
		case POSITION_RIGHT:
			point.x=700;
			point.y=(450/2)-(cardHoldList.size()+1)*15/2;
			break;
		default:;
		}
		int len=cardHoldList.size();
		for(int i=0;i<len;i++){
			CardLabel card=cardHoldList.get(i);
			card.move(point);
			frame.container.setComponentZOrder(card, 0);
			if(position==1)point.x+=21;
			else point.y+=15;
		}
	}

	public int getScore(){
		int score=0;
		for(int i=0,len=cardHoldList.size();i<len;i++){
			CardLabel card=cardHoldList.get(i);
			if(card.getName().substring(0, 1).equals("5")){
				score+=5;
			}
			if(card.getName().substring(2, card.getName().length()).equals("2")){
				score+=2;
			}
		}
		return score;
	}


	public void publishCard() {
		if(position == POSITION_PLAYER) {
			playerPublish();
		}else {
			conputerPublish();
		}
	}

	private void playerPublish() {
		List<CardLabel> publishCards = new ArrayList<CardLabel>();
		for(CardLabel card : cardHoldList) {
			if(card.isClicked()){
				publishCards.add(card);
			}
		}

		boolean flag=false;
		if(frame.getPlayer(CardPlayer.POSITION_LEFT).getClockFiled().getText().equals("不要") 
				&& frame.getPlayer(CardPlayer.POSITION_RIGHT).getClockFiled().getText().equals("不要")){
			if(CardType.getType(publishCards)!=CardType.T0)
				flag=true;//表示可以出牌
		}else{
			flag=checkCards(publishCards);
		}

		if(flag){
			cardPublishList = publishCards;
			cardHoldList.removeAll(publishCards);

			Point point=new Point();
			point.x=(770/2)-(publishCards.size()+1)*15/2;;
			point.y=300;
			for(int i=0,len=publishCards.size();i<len;i++){
				publishCards.get(i).move(point);
				point.x+=15;
			}

			resetPosition();
			clockFiled.setVisible(false);
			frame.setNextPlayer(true);
		}
	}

	//检查牌的是否能出
	private  boolean checkCards(List<CardLabel> c){
		//找出当前最大的牌是哪个电脑出的,c是点选的牌
		List<CardLabel> currentlist = frame.getPlayer(CardPlayer.POSITION_LEFT).getCardPublishList();
		if(currentlist.isEmpty()){
			currentlist = frame.getPlayer(CardPlayer.POSITION_RIGHT).getCardPublishList();
		}

		int cType=CardType.getType(c);
		//如果张数不同直接过滤
		if(cType!=CardType.T4&&c.size()!=currentlist.size())
			return false;
		//比较我的出牌类型
		if(CardType.getType(c)!= CardType.getType(currentlist))
		{

			return false;
		}
		//比较出的牌是否要大
		//王炸弹
		if(cType==CardType.T4)
		{
			if(c.size()==2)
				return true;
			if(currentlist.size()==2)
				return false;
		}
		//单牌,对子,3带,4炸弹
		if(cType==CardType.T1||cType==CardType.T2||cType==CardType.T3||cType==CardType.T4){
			if(c.get(0).singleValue() <= currentlist.get(0).singleValue())
			{
				return false;
			}else {
				return true;
			}
		}
		//顺子,连队，飞机裸
		if(cType==CardType.T123||cType==CardType.T1122||cType==CardType.T111222)
		{
			if(c.get(0).value() <= currentlist.get(0).value())
				return false;
			else 
				return true;
		}
		//按重复多少排序
		//3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的 
		if(cType==CardType.T31||cType==CardType.T32||cType==CardType.T411||cType==CardType.T422
				||cType==CardType.T11122234||cType==CardType.T1112223344){
			List<CardLabel> a1=getOrder2(c); //我出的牌
			List<CardLabel> a2=getOrder2(currentlist);//当前最大牌
			if(a1.get(0).value() < a2.get(0).value())
				return false;
		}
		return true;
	}


	private void conputerPublish() {
		Model model = getModel(cardHoldList);
		List<String> publishCardList = new ArrayList<String>();
		// 主动出牌
		if (frame.getPlayer((position + 1) % 3).getClockFiled().getText().equals("不要")
				&& frame.getPlayer((position + 2) % 3).getClockFiled().getText().equals("不要")) {
			// 有单出单 (除开3带，飞机能带的单牌)
			if (model.a1.size() > (model.a111222.size() * 2 + model.a3.size())) {
				publishCardList.add(model.a1.get(model.a1.size() - 1));
			}// 有对子出对子 (除开3带，飞机)
			else if (model.a2.size() > (model.a111222.size() * 2 + model.a3
					.size())) {
				publishCardList.add(model.a2.get(model.a2.size() - 1));
			}// 有顺子出顺子
			else if (model.a123.size() > 0) {
				publishCardList.add(model.a123.get(model.a123.size() - 1));
			}// 有3带就出3带，没有就出光3
			else if (model.a3.size() > 0) {
				// 3带单,且非关键时刻不能带王，2
				if (model.a1.size() > 0) {
					publishCardList.add(model.a1.get(model.a1.size() - 1));
				}// 3带对
				else if (model.a2.size() > 0) {
					publishCardList.add(model.a2.get(model.a2.size() - 1));
				}
				publishCardList.add(model.a3.get(model.a3.size() - 1));
			}// 有双顺出双顺
			else if (model.a112233.size() > 0) {
				publishCardList.add(model.a112233.get(model.a112233.size() - 1));
			}// 有飞机出飞机
			else if (model.a111222.size() > 0) {
				String name[] = model.a111222.get(0).split(",");
				// 带单
				if (name.length / 3 <= model.a1.size()) {
					publishCardList.add(model.a111222.get(model.a111222.size() - 1));
					for (int i = 0; i < name.length / 3; i++)
						publishCardList.add(model.a1.get(i));
				} else if (name.length / 3 <= model.a2.size())// 带双
				{
					publishCardList.add(model.a111222.get(model.a111222.size() - 1));
					for (int i = 0; i < name.length / 3; i++)
						publishCardList.add(model.a2.get(i));
				}
				// 有炸弹出炸弹
			} else if (model.a4.size() > 0) {
				// 4带2,1
				int sizea1 = model.a1.size();
				int sizea2 = model.a2.size();
				if (sizea1 >= 2) {
					publishCardList.add(model.a1.get(sizea1 - 1));
					publishCardList.add(model.a1.get(sizea1 - 2));
					publishCardList.add(model.a4.get(0));

				} else if (sizea2 >= 2) {
					publishCardList.add(model.a2.get(sizea1 - 1));
					publishCardList.add(model.a2.get(sizea1 - 2));
					publishCardList.add(model.a4.get(0));

				} else {// 直接炸
					publishCardList.add(model.a4.get(0));

				}

			}
		}// 如果是跟牌
		else {
			CardPlayer prePlayer = frame.getPlayer((position + 2) % 3);
			CardPlayer nextPlayer = frame.getPlayer((position + 1) % 3);
			List<CardLabel> cardList = prePlayer.getCardPublishList();
			if(cardList.isEmpty()){
				cardList = nextPlayer.getCardPublishList();
			}

			switch(CardType.getType(cardList)) {
			case CardType.T1:
				AI_1(model.a1, cardList, publishCardList, position); break;
			case CardType.T2:
				AI_1(model.a2, cardList, publishCardList, position); break;
			case CardType.T3:
				AI_1(model.a3, cardList, publishCardList, position); break;
			case CardType.T4:
				AI_1(model.a4, cardList, publishCardList, position); break;
			case CardType.T31:
				AI_2(model.a3, model.a1, cardList, publishCardList); break;
			case CardType.T32:
				AI_2(model.a3, model.a2, cardList, publishCardList); break;
			case CardType.T411:
				AI_5(model.a4, model.a1, cardList, publishCardList, position); break;
			case CardType.T422:
				AI_5(model.a4, model.a2, cardList, publishCardList, position); break;
			case CardType.T123:
				AI_3(model.a123, cardList, publishCardList, position); break;
			case CardType.T1122:
				AI_3(model.a112233, cardList, publishCardList, position); break;
			case CardType.T11122234:
				AI_4(model.a111222,model.a1, cardList, publishCardList, position); break;
			case CardType.T1112223344:
				AI_4(model.a111222,model.a2, cardList, publishCardList, position); break;
			}
			//炸弹
			if(publishCardList.size()==0){
				int len4=model.a4.size();
				if(len4>0)
					publishCardList.add(model.a4.get(len4-1));
			}
		}

		// 定位出牌
		cardPublishList.clear();
		if (publishCardList.size() > 0) {
			Point point = new Point();
			if (position == 0)
				point.x = 200;
			if (position == 2)
				point.x = 550;
			point.y = (400 / 2) - (publishCardList.size() + 1) * 15 / 2;
			// 将name转换成Card
			for (int i = 0, len = publishCardList.size(); i < len; i++) {
				List<CardLabel> cards = getCardByName(cardHoldList,publishCardList.get(i));
				for (CardLabel card : cards) {
					card.move(point);
					point.y += 15;
					cardPublishList.add(card);
					cardHoldList.remove(card);
				}
			}
			resetPosition();
		} else {
			frame.getPlayer(position).getClockFiled().setVisible(true);
			frame.getPlayer(position).getClockFiled().setText("不要");
		}
		for(CardLabel card : cardPublishList){
			card.turnUp();
		}
	}

	private Model getModel(List<CardLabel> list){
		List<CardLabel> list2=new ArrayList<CardLabel>(list);
		Model model=new Model();
		CardType.getT4(list2, model); 
		CardType.getT3(list2, model);
		CardType.getT111222(list2, model);
		CardType.getT2(list2, model);
		CardType.getT1122(list2, model);
		CardType.getT123(list2, model);
		CardType.getT1(list2, model);
		return model;
	}

	private List<CardLabel> getCardByName(List<CardLabel> list, String n) {
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

	//单牌，对子，3个，4个,通用
	private void AI_1(List<String> model,List<CardLabel> player,List<String> list,int position){
		//顶家
		if((position+1)%3 == frame.getDizhuPosition()){
			for(int i=0,len=model.size();i<len;i++){
				if(getValueInt(model.get(i)) > player.get(0).value()){
					list.add(model.get(i));
					break;
				}
			}
		}else {//偏家
			for(int len=model.size(),i=len-1;i>=0;i--){
				if(getValueInt(model.get(i)) > player.get(0).value()){
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
		player=getOrder2(player);
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
			if(getValueInt(model1.get(i)) > player.get(0).value())
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
		player=getOrder2(player);
		int len1=model1.size();
		int len2=model2.size();

		if(len1<1 || len2<2)
			return;
		for(int i=0;i<len1;i++){
			if(getValueInt(model1.get(i)) > player.get(0).value())
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
			if(s.length==player.size()&&getValueInt(model.get(i)) > player.get(0).value())
			{
				list.add(model.get(i));
				return;
			}
		}
	}

	//飞机带单，双
	private void AI_4(List<String> model1,List<String> model2,List<CardLabel> player,List<String> list,int role){
		//排序按重复数
		player=getOrder2(player);
		int len1=model1.size();
		int len2=model2.size();

		if(len1<1 || len2<1)
			return;
		for(int i=0;i<len1;i++){
			String []s=model1.get(i).split(",");
			String []s2=model2.get(0).split(",");
			if((s.length/3<=len2)&&(s.length*(3+s2.length)==player.size())&&getValueInt(model1.get(i)) > player.get(0).value())
			{
				list.add(model1.get(i));
				for(int j=1;j<=s.length/3;j++)
					list.add(model2.get(len2-j));
			}
		}
	}

	//按照重复次数排序
	private List<CardLabel> getOrder2(List<CardLabel> list){
		List<CardLabel> list2=new ArrayList<CardLabel>(list);
		List<CardLabel> list3=new ArrayList<CardLabel>();
		int len=list2.size();
		int a[]=new int[20];
		for(int i=0;i<20;i++)
			a[i]=0;
		for(int i=0;i<len;i++)
		{
			a[list2.get(i).value()]++;
		}
		int max=0;
		for(int i=0;i<20;i++){
			max=0;
			for(int j=19;j>=0;j--){
				if(a[j]>a[max])
					max=j;
			}

			for(int k=0;k<len;k++){
				if(list2.get(k).value() == max){
					list3.add(list2.get(k));
				}
			}
			list2.remove(list3);
			a[max]=0;
		}
		return list3;
	}
}
