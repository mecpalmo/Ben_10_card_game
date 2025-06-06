package pl.mecpalmo.game.view;

import pl.mecpalmo.config.Strings;
import pl.mecpalmo.config.Values;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame{
	
	public GameFrame(){
		
		super(Strings.s11);
		setWindowSize();
		
		GamePanel myGamePanel = new GamePanel(this);
		add(myGamePanel);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, 0);
		
	}
	
	public void setWindowSize() {
		JFrame temp = new JFrame();
		temp.pack();
		Insets insets = temp.getInsets();
		temp = null;
		this.setSize(insets.left + Values.DEFAULT_X, insets.top + Values.DEFAULT_Y);
	}
	
}
