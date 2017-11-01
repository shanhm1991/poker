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
public class CardLabel extends JLabel {

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
		setSize(71, 96);
		setVisible(true);
		addMouseListener(new MouseListener(){
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
					Point from = getLocation();
					move(new Point(from.x,from.y-step));
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
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
	public int color(){
		if(name == null){
			return 0;
		}
		return Integer.parseInt(name.substring(0,1));
	}

	public int value(){
		if(name == null){
			return 0;
		}
		int value = Integer.parseInt(name.substring(2,name.length()));
		if(color() == 5) {
			value += 50;
		}
		return value;
	}
	
	/**
	 * A字和2字单独出为大，也可以以小牌凑顺子
	 */
	public int singleValue() {
		if(name == null){
			return 0;
		}
		int value = Integer.parseInt(name.substring(2,name.length()));
		if(color() == 5) {
			value += 50;
		}else if(value == 1) {
			value += 13;
		}else if(value == 2) {
			value += 15;
		}
		return value; 
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
				double y=k*i+b;

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
}
