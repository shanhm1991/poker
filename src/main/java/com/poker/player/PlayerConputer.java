package com.poker.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.poker.CardLabel;
import com.poker.CardType;
import com.poker.MainFrame;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
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

	@Override
	public void publish() {
		CardPlayer prePlayer = frame.getPlayer((position + 2) % 3);
		CardPlayer nextPlayer = frame.getPlayer((position + 1) % 3);
		cardPublishList.clear();

		
		ThinkingTask task = new ThinkingTask(this,prePlayer,nextPlayer);
		FutureTask<List<CardLabel>> future = new FutureTask<List<CardLabel>>(task);
        new Thread(future).start();
		clock(15);
		if(future.isDone()){
			future.cancel(true);
		}
		
		List<CardLabel> publishCardList;
		try {
			publishCardList = future.get(1, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			System.out.println("occured a exception:" + e.getMessage()); 
			publishCardList = new ArrayList<CardLabel>();
		}

		if (publishCardList.size() > 0) {
			Point point = new Point();
			if (position == 0)
				point.x = 200;
			if (position == 2)
				point.x = 550;
			point.y = (400 / 2) - (publishCardList.size() + 1) * 15 / 2;

			for(CardLabel card : publishCardList){
				card.move(point);
				point.y += 15;
				cardPublishList.add(card);
				cardHoldList.remove(card);
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

	private void choosePublish(){
		List<CardLabel> publishCardList = new ArrayList<CardLabel>();
		CardType type = new CardType(cardHoldList);

		if(type.listT1.size() > type.listT3.size()){
			//除3带外的单张 顺子TODO
			publishCardList.add(type.listT1.get(0));
		}else if(type.listT2.size() > type.listT3.size()){
			//出对子
		}
		//顺子
		//双顺
		//飞机
		//3带or3不带 （不要带王 A 2）
		//最后炸弹
	}

	private void comparePublish(List<CardLabel> oherList){
		CardType otherType = new CardType(oherList);
		int otherType_T = getType(oherList,otherType);

		switch(otherType_T) {
		case CardType.T1:
			AI_1(oherList); break;
		case CardType.T2:
			AI_1(oherList); break;
		case CardType.T3:
			AI_1(oherList); break;
		case CardType.T4:
			AI_1(oherList); break;
		case CardType.T31:
			AI_2(oherList); break;
		case CardType.T32:
			AI_2(oherList); break;
		case CardType.T123:
			AI_3(oherList); break;
		case CardType.T22:
			AI_3(oherList); break;
		case CardType.T3312:
			AI_4(oherList); break;
		case CardType.T3322:
			AI_4(oherList); break;
		}
	}

	//单牌，对子，3个，4个,通用
	private void AI_1(List<CardLabel> player){
		//顶家

		//偏家
	}

	//3带1,2
	private void AI_2(List<CardLabel> player){

	}

	//顺子
	private void AI_3(List<CardLabel> player){

	}

	//飞机带单，双
	private void AI_4(List<CardLabel> player){

	}

	//	private void getT123(List<CardLabel> list,Think model){
	//		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
	//		if(list.size()>0&&(list.get(0).getValue()<7 ||list.get(list.size()-1).getValue()>10))
	//			return;
	//		if(list.size()<5)
	//			return;
	//		for(int i=0,len=list.size();i<len;i++){
	//			int k=i;
	//			for(int j=i;j<len;j++){
	//				if(list.get(i).getValue() - list.get(j).getValue()==j-i)
	//				{
	//					k=j;
	//				}
	//			}
	//			if(k-i>=4){
	//				String s="";
	//				for(int j=i;j<k;j++){
	//					s+=list.get(j).getName()+",";
	//					del.add(list.get(j));
	//				}
	//				s+=list.get(k).getName();
	//				del.add(list.get(k));
	//				model.a123.add(s);
	//				i=k;
	//			}
	//		}
	//		list.removeAll(del);
	//	}

	//	private void getT1122(List<CardLabel> list,Think model){
	//		List<String> del=new ArrayList<String>();//要删除的Cards
	//		//从model里面的对子找
	//		List<String> l=model.a2;
	//		if(l.size()<3)
	//			return ;
	//		Integer s[]=new Integer[l.size()];
	//		for(int i=0,len=l.size();i<len;i++){
	//			String []name=l.get(i).split(",");
	//			s[i]=Integer.parseInt(name[0].substring(2,name[0].length()));
	//		}
	//		//s0,1,2,3,4  13,9,8,7,6
	//		for(int i=0,len=l.size();i<len;i++){
	//			int k=i;
	//			for(int j=i;j<len;j++){
	//				if(s[i]-s[j]==j-i)
	//					k=j;
	//			}
	//			if(k-i>=2){//说明从i到k是连队 k=4 i=1
	//				String ss="";
	//				for(int j=i;j<k;j++){
	//					ss+=l.get(j)+",";
	//					del.add(l.get(j));
	//				}
	//				ss+=l.get(k);
	//				model.a112233.add(ss);
	//				del.add(l.get(k));
	//				i=k;
	//			}
	//		}
	//		l.removeAll(del);
	//	}

	//	private void getT1(List<CardLabel> list,Think model){
	//		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
	//		//1
	//		for(int i=0,len=list.size();i<len;i++){
	//			model.a1.add(list.get(i).getName());
	//			del.add(list.get(i));
	//		}
	//		list.removeAll(del);
	//	}

	//	private void getT2(List<CardLabel> list,Think model){
	//		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
	//		//连续2张相同
	//		for(int i=0,len=list.size();i<len;i++){
	//			if(i+1<len&& list.get(i).getValue() == list.get(i+1).getValue())
	//			{
	//				String s=list.get(i).getName()+",";
	//				s+=list.get(i+1).getName();
	//				model.a2.add(s);
	//				for(int j=i;j<=i+1;j++)
	//					del.add(list.get(j));
	//				i=i+1;
	//			}
	//		}
	//		list.removeAll(del);
	//	}

	//	private void getT111222(List<CardLabel> list,Think model){
	//		List<String> del=new ArrayList<String>();//要删除的Cards
	//		//从model里面的3带找
	//		List<String> l=model.a3;
	//		if(l.size()<2)
	//			return ;
	//		Integer s[]=new Integer[l.size()];
	//		for(int i=0,len=l.size();i<len;i++){
	//			String []name=l.get(i).split(",");
	//			s[i]=Integer.parseInt(name[0].substring(2,name[0].length()));
	//		}
	//		for(int i=0,len=l.size();i<len;i++){
	//			int k=i;
	//			for(int j=i;j<len;j++){
	//				if(s[i]-s[j]==j-i)
	//					k=j;
	//			}
	//			if(k!=i){//说明从i到k是飞机
	//				String ss="";
	//				for(int j=i;j<k;j++){
	//					ss+=l.get(j)+",";
	//					del.add(l.get(j));
	//				}
	//				ss+=l.get(k);
	//				model.a111222.add(ss);
	//				del.add(l.get(k));
	//				i=k;
	//			}
	//		}
	//		l.removeAll(del);
	//	}

	//	private void getT3(List<CardLabel> list){
	//		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
	//		//连续3张相同
	//		for(int i=0,len=list.size();i<len;i++){
	//			if(i+2<len&&list.get(i).getValue() == list.get(i+2).getValue())
	//			{
	//				String s=list.get(i).getName()+",";
	//				s+=list.get(i+1).getName()+",";
	//				s+=list.get(i+2).getName();
	//				model.a3.add(s);
	//				for(int j=i;j<=i+2;j++)
	//					del.add(list.get(j));
	//				i=i+2;
	//			}
	//		}
	//		list.removeAll(del);
	//	}

	//	private void getT4(List<CardLabel> list){
	//		List<CardLabel> del=new ArrayList<CardLabel>();//要删除的Cards
	//		//王炸
	//		if(list.size()>=2 &&list.get(0).getColor()==5 && list.get(1).getColor()==5)
	//		{
	//			model.a4.add(list.get(0).getName()+","+list.get(1).getName()); //按名字加入
	//			del.add(list.get(0));
	//			del.add(list.get(1));
	//		}
	//		//如果王不构成炸弹咋先拆单
	//		if(list.get(0).getColor()==5 && list.get(1).getColor()!=5)
	//		{
	//			del.add(list.get(0));
	//			model.a1.add(list.get(0).getName());
	//		}
	//		list.removeAll(del);
	//		//一般的炸弹
	//		for(int i=0,len=list.size();i<len;i++){
	//			if(i+3<len && list.get(i).getValue() == list.get(i+3).getValue())
	//			{
	//				String s=list.get(i).getName()+",";
	//				s+=list.get(i+1).getName()+",";
	//				s+=list.get(i+2).getName()+",";
	//				s+=list.get(i+3).getName();
	//				model.a4.add(s);
	//				for(int j=i;j<=i+3;j++)
	//					del.add(list.get(j));
	//				i=i+3;
	//			}
	//		}
	//		list.removeAll(del);
	//	}



	/**
	 * 电脑思考线程，非swing线程，不能更新swing组件
	 */
	private class ThinkingTask implements Callable<List<CardLabel>>{

		private CardPlayer prePlayer;

		private CardPlayer nextPlayer;

		private CardPlayer currentPlayer;

		public ThinkingTask(CardPlayer currentPlayer,CardPlayer prePlayer,CardPlayer nextPlayer){
			this.prePlayer = prePlayer;
			this.nextPlayer = nextPlayer;
			this.currentPlayer = currentPlayer;
		}

		@Override
		public List<CardLabel> call() throws Exception {
			if (prePlayer.isClockEnd() && nextPlayer.isClockEnd()) {
				//				choosePublish();
				//			}else {
				//				List<CardLabel> cardList = prePlayer.getCardPublishList();
				//				if(cardList.isEmpty()){
				//					cardList = nextPlayer.getCardPublishList();
				//				}
				//				comparePublish(cardList);
				//				//炸弹
				//			}

				System.out.println(322);

				Thread.sleep(2000);

				currentPlayer.setClockEnd(true);
			}

			return new ArrayList<CardLabel>();

		}
	}
}
