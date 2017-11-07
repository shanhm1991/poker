package com.poker;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"value"},callSuper = false)
public class Card {

	private  final String name;

	private  final Integer color;

	private  final Integer value;

	private  final Integer singleValue;

	private  final Integer continueValue;

	private boolean clicked;

	private boolean clickable;

	private Label label;

	public Card(String name){
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
	}

	public Card init() {
		label = new Label();
		return this;
	}

	public void hide() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					label.setVisible(false);
				}
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void show() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					label.setIcon(new ImageIcon("images/" + name + ".gif"));
				}
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void back() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					label.setIcon(new ImageIcon("images/rear.gif"));
				}
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void asynmove(Point to) {
		Point from = label.getLocation();
		if(to.x != from.x){
			double k = (1.0) * (to.y - from.y) / (to.x - from.x);
			double b = to.y - to.x * k;
			int flag = 0;
			if(from.x < to.x)
				flag = 20;
			else {
				flag = -20;
			}
			for(int i = from.x;Math.abs(i - to.x) > 20;i += flag){
				double y = k * i + b;
				final int x = i;
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							label.setLocation(x,(int)y);
						}
					});
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(5); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					label.setLocation(to);
				}
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	public void synmove(Point to) {
		label.synmove(to);
	}

	private class Label extends JLabel {

		private static final long serialVersionUID = 2576906960410567741L;

		public Label() {
			setSize(71, 96);
			setVisible(true); 
			setLocation(350, 50);
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
						synmove(new Point(from.x,from.y-step));
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
		}

		public void synmove(Point to){
			Point from = this.getLocation();
			if(to.x != from.x){
				double k = (1.0) * (to.y - from.y) / (to.x - from.x);
				double b = to.y - to.x * k;
				int flag = 0;
				if(from.x < to.x)
					flag = 20;
				else {
					flag = -20;
				}
				for(int i = from.x;Math.abs(i - to.x) > 20;i += flag){
					double y = k * i + b;
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
}
