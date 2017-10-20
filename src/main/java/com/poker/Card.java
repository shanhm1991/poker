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
public class Card extends JLabel implements MouseListener{

	private static final long serialVersionUID = -4590127474545680976L;

	 private Main main;

	 private String name;

	 private boolean positive;

	 private boolean clickable;

	 private boolean clicked;

	public Card(Main main,String name,boolean up){
		this.main = main;
		this.name = name;
		this.positive = up;
		if(this.positive)
			this.turnFront();
		else {
			this.turnRear();
		}
		this.setSize(71, 96);
		this.setVisible(true);
		this.addMouseListener(this);
	}

	public void turnFront() {
		this.setIcon(new ImageIcon("images/" + name + ".gif"));
		this.positive = true;
	}

	public void turnRear() {
		this.setIcon(new ImageIcon("images/rear.gif"));
		this.positive = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(clickable){
			Point from=this.getLocation();
			int step; 
			if(clicked)
				step=-20;
			else {
				step=20;
			}
			clicked=!clicked; 
			//选中时，移动或者后退
			Common.move(this,from,new Point(from.x,from.y-step));
		}
	}

	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	

}
