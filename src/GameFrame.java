import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameFrame extends JFrame{

	GamePanel myGamePanel;
	float ratio = (float)Values.DEFAULT_Y/(float)(Values.DEFAULT_X);
	
	GameFrame(){
		super("Ben 10 Karciana Gra Kolekcjonerska - mecpalmoGames");
		Values.resetOpponentsData();
		Values.resetMyData();
		setSize(Values.DEFAULT_X, Values.DEFAULT_Y);
		myGamePanel = new GamePanel(this);
		add(myGamePanel);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//okno na srodku ekranu
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
	
}
