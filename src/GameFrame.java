import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame{
	
	GameFrame(){
		
		super("Ben 10 Karciana Gra Kolekcjonerska - mecpalmoGames");
		
		setWindowSize();
		
		GamePanel myGamePanel = new GamePanel(this);
		add(myGamePanel);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		
		//okno na srodku ekranu
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
	}
	
	public void setWindowSize() {
		JFrame temp = new JFrame();
		temp.pack();
		Insets insets = temp.getInsets();
		temp = null;
		this.setSize(insets.left + Values.DEFAULT_X, insets.top + Values.DEFAULT_Y);
	}
	
}
