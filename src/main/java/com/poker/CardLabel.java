package com.poker;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"value"},callSuper = false)
public class CardLabel extends JLabel {

	private static final long serialVersionUID = -4590127474545680976L;

	private final String name;
	
	private final Integer color;

	private final Integer value;

	private final Integer singleValue;
	
	private final Integer continueValue;

	private MainFrame main;

	private boolean clickable;

	private boolean clicked;

	public CardLabel(MainFrame main,String name,boolean init){
		this.name = name;
		color = Integer.parseInt(name.substring(0,1));
		if(color == 5){
			value = Integer.parseInt(name.substring(2,name.length())) + 50;
		}else{
			value = Integer.parseInt(name.substring(2,name.length()));
		}
		if(value == 1){
			singleValue = value + 13;
			continueValue = value + 13;
		}else if(value == 2){
			singleValue = value + 15;
			continueValue = value;
		}else{
			singleValue = value;
			continueValue = value;
		}
		if(init){
			this.main = main;
			setSize(71, 96);
			setVisible(true);
			setIcon(new ImageIcon("images/rear.gif"));
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
	}

	public void show() {
		this.setIcon(new ImageIcon("images/" + name + ".gif"));
	}

	public void hide() {
		this.setIcon(new ImageIcon("images/rear.gif"));
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
