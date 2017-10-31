package com.poker;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"name"},callSuper = true)
public class CardLabel extends JLabel implements MouseListener{

	private static final long serialVersionUID = -4590127474545680976L;

	private MainFrame main;

	private String name;

	private boolean positive;

	private boolean clickable;

	private boolean clicked;

	public CardLabel(MainFrame main,String name,boolean up){
		this.main = main;
		this.name = name;
		this.positive = up;
		if(this.positive)
			this.turnUp();
		else {
			this.turnBack();
		}
		this.setSize(71, 96);
		this.setVisible(true);
		this.addMouseListener(this);
	}

	public void turnUp() {
		this.setIcon(new ImageIcon("images/" + name + ".gif"));
		this.positive = true;
	}

	public void turnBack() {
		this.setIcon(new ImageIcon("images/rear.gif"));
		this.positive = false;
	}

	/**
	 * 花色 
	 * 1:黑桃 2:红桃 3:草花 4:方块 5:王
	 */
	public int getColor(){
		if(name == null){
			return 0;
		}
		return Integer.parseInt(name.substring(0,1));
	}

	/**
	 * 获取牌的大小
	 * A和2大于其他牌，王大于一切
	 */
	public int getValue(){
		if(name == null){
			return 0;
		}
		String str = name.substring(2,name.length());
		if("1".equals(str) || "2".equals(str)){ 
			return Integer.parseInt(str) + 13;
		}
		if(getColor() == 5){
			return Integer.parseInt(str) + 2;
		}
		return Integer.parseInt(str);
	}

	/**
	 * 选中时移动
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(clickable){
			int step; 
			if(clicked)
				step=-20;
			else {
				step=20;
			}
			clicked=!clicked; 
			Point from=this.getLocation();
			move(new Point(from.x,from.y-step));
		}
	}

	public void move(Point to){
		Point from = this.getLocation();
		if(to.x!=from.x){
			double k=(1.0)*(to.y-from.y)/(to.x-from.x);
			double b=to.y-to.x*k;
			int flag=0;//判断向左还是向右移动步幅
			if(from.x<to.x)
				flag=20;
			else {
				flag=-20;
			}
			for(int i=from.x;Math.abs(i-to.x)>20;i+=flag)
			{
				double y=k*i+b;//这里主要用的数学中的线性函数

				this.setLocation(i,(int)y);
				try {
					Thread.sleep(5); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		this.setLocation(to);
	}

	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
}
