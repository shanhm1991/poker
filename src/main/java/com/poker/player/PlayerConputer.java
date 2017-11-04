package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.poker.CardLabel;
import com.poker.CardType;
import com.poker.MainFrame;
import com.poker.Think;

public class PlayerConputer extends CardPlayer {

	public PlayerConputer(MainFrame frame, int position) {
		super(frame, position);
		switch(position){
		case POSITION_LEFT:
			lordPoint = new Point(80,20);
			clockFiled.setBounds(140, 230, 60, 20);
			break;
		case POSITION_RIGHT:
			lordPoint = new Point(700,20);
			clockFiled.setBounds(620, 230, 60, 20);
			break;
		default:;
		}
	}
	
	
	/**
	 * 接牌
	 * @param ownList
	 * @return
	 */

	public void publish() {
		Think model = getModel(cardHoldList);
		List<String> publishCardList = new ArrayList<String>();
		// 主动出牌


		if (frame.getPlayer((position + 1) % 3).isClockEnd()
				&& frame.getPlayer((position + 2) % 3).isClockEnd()) {



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

			switch(getType(cardList)) {
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
			case CardType.T123:
				AI_3(model.a123, cardList, publishCardList, position); break;
			case CardType.T22:
				AI_3(model.a112233, cardList, publishCardList, position); break;
			case CardType.T3312:
				AI_4(model.a111222,model.a1, cardList, publishCardList, position); break;
			case CardType.T3322:
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
			card.show();
		}
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
	
	
	private Think getModel(List<CardLabel> list){
		List<CardLabel> list2=new ArrayList<CardLabel>(list);
		Think model=new Think();
		CardType.getT4(list2, model); 
		CardType.getT3(list2, model);
		CardType.getT111222(list2, model);
		CardType.getT2(list2, model);
		CardType.getT1122(list2, model);
		CardType.getT123(list2, model);
		CardType.getT1(list2, model);
		return model;
	}
	
	//单牌，对子，3个，4个,通用
		private void AI_1(List<String> model,List<CardLabel> player,List<String> list,int position){
			//顶家
			if((position+1)%3 == frame.getLordPosition()){
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
			player=getOrder2(player);
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
