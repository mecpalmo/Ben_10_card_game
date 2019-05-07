import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class CreatingDeckWindow extends JFrame{

	private JButton ClearDeck, SaveDeck, ReturnToMenu;
	
	private CurrentDeckPanel myCurrentDeckPanel;
	private AllCardsPanel myAllCardsPanel;
	private ButtonPanel myButtonPanel;
	
	private JFrame Menu;
	
	private int size_x, size_y;
	
	private final float LeftPanelXRatio = 0.4f;
	private final float PanelYRatio = 0.9f;
	private final int delta = 0;
	
	CreatingDeckWindow(JFrame menu){
		super("Ben 10 Karciana Gra Kolekcjonerska - mecpalmoGames");
		Menu = menu;
		
		size_x = Values.DEFAULT_X;
		size_y = Values.DEFAULT_Y;
		
		this.setSize(size_x,size_y);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		myCurrentDeckPanel = new CurrentDeckPanel();
		myAllCardsPanel = new AllCardsPanel();
		
		JScrollPane ScrollCurrentDeck = new JScrollPane();
		ScrollCurrentDeck.setBounds(0,0,(int) (size_x*LeftPanelXRatio)-delta,size_y-delta);
		ScrollCurrentDeck.add(myCurrentDeckPanel);
		add(ScrollCurrentDeck);
		
		JScrollPane ScrollAllCards = new JScrollPane();
		ScrollAllCards.setBounds((int)(size_x*LeftPanelXRatio),(int)(size_y*(1-PanelYRatio)),(int)(size_x*(1-LeftPanelXRatio))-delta,(int)(size_y*PanelYRatio)-delta);
		ScrollAllCards.add(myAllCardsPanel);
		add(ScrollAllCards);
		
		
		//okno na œrodku ekranu
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
	
	private class ButtonPanel extends JPanel{
		
		ButtonPanel(){
			setLayout(null);
			setBackground(Color.BLACK);
			setSize((int)(size_x*(1-LeftPanelXRatio)),(int)(size_y*(1-PanelYRatio)));
			initButtons();
		}
		
		private void initButtons() {
			
			Font font = new Font("Verdana",Font.BOLD,11);
			
			ClearDeck = new JButton("Wyczyœæ ca³¹ taliê");
			ClearDeck.addActionListener(actList);
			ClearDeck.setFont(font);
			
			SaveDeck = new JButton("Wyczyœæ ca³¹ taliê");
			SaveDeck.addActionListener(actList);
			SaveDeck.setFont(font);
			
			ReturnToMenu = new JButton("Wyczyœæ ca³¹ taliê");
			ReturnToMenu.addActionListener(actList);
			ReturnToMenu.setFont(font);
			
		}
		
	}
	
	private class CurrentDeckPanel extends JPanel{
		
		CurrentDeckPanel(){
			setLayout(null);
			setBackground(Color.BLUE);
			setSize((int) (size_x*LeftPanelXRatio),2500);
		}
		
	}
	
	private class AllCardsPanel extends JPanel{
		
		public Field[] fields;
		private int CardWidth;
		private int CardHeight;
		private int CardsInRow;
		private int Rows;
		private final float SpaceRatio = 0.05f;
		private int space;
		private Graphics2D g2d;
		
		AllCardsPanel(){
			setLayout(null);
			setBackground(Color.GREEN);
			fields = new Field[Values.CARDS_AMOUNT];
			CardWidth = GameInfo.CardsLibrary[0].returnBackImage().getWidth();
			CardHeight = GameInfo.CardsLibrary[0].returnBackImage().getHeight();
			
			int width = (int)(size_x*(1-LeftPanelXRatio));
			space = (int) (SpaceRatio * CardWidth);
			CardsInRow = (int)((double)(width-space)/(double)(space+CardWidth));
			
			Rows = (int) Math.ceil((double)Values.CARDS_AMOUNT/(double)CardsInRow);
			int height = space+Rows*(space*CardHeight);
					
			setSize(width,height);
			
			setFields();
			
			repaint();
		}
		
		private void setFields() {
			for(int i=0; i<Values.CARDS_AMOUNT; i++) {
				int x = space + (i%CardsInRow)*(CardWidth+space);
				int y = (int) (space + Math.floor(i/CardsInRow)*(CardHeight+space));
				fields[i] = new Field(0,x,y);
				fields[i].addCard(i);
				fields[i].openCard();
			}
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        
	        for(int i=0; i<fields.length; i++) {
	        	g2d.drawImage(fields[i].returnFieldImage(), fields[i].returnX(), fields[i].returnY(), this);
	        }
		}
	}
	
	private ActionListener actList = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==ClearDeck) {
				clearDeck();
			}
			if(e.getSource()==SaveDeck) {
				saveDeck();
			}
			if(e.getSource()==ReturnToMenu) {
				returnToMenu();
			}
		}
	};
	
	private void clearDeck() {
		
	}
	
	private void saveDeck() {
		
	}
	
	private void returnToMenu() {
		this.setVisible(false);
		Menu.setVisible(true);
		this.dispose();
	}
	
}
