import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class CreatingDeckWindow extends JFrame{

	private JButton ClearDeck, SaveDeck, ReturnToMenu;
	private AllCardsPanel myAllCardsPanel;
	
	private int size_x, size_y;
	
	CreatingDeckWindow(){
		super("Ben 10 Karciana Gra Kolekcjonerska - mecpalmoGames");
		this.setBackground(Color.GRAY);
		
		size_x = (int) (Values.DEFAULT_X * 1.2);
		size_y = Values.DEFAULT_Y;
		
		JFrame temp = new JFrame();
		temp.pack();
		Insets insets = temp.getInsets();
		temp = null;
		this.setSize(insets.left + size_x, insets.top + size_y);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		
		myAllCardsPanel = new AllCardsPanel();
		
		JScrollPane ScrollAllCards = new JScrollPane(myAllCardsPanel);
		ScrollAllCards.getVerticalScrollBar().setUnitIncrement(23);
		add(ScrollAllCards);
		
		//okno na œrodku ekranu
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
	
	private class AllCardsPanel extends JPanel{
		
		private Field[] fields;
		private List<Field> deckFields = new ArrayList<Field>();
		private Graphics2D g2d;
		private JLabel label;
		
		private final float RightPanelXRatio = 0.4f;
		private final float SpaceRatio = 0.05f;
		private int CardWidth, CardHeight, CardsInRow, CardsInRow2, space;
		
		private ShowBigCard ShowBigCardObject = new ShowBigCard();
		
		AllCardsPanel(){
			
			setLayout(null);
			setBackground(Color.GREEN);
			fields = new Field[Values.CARDS_AMOUNT];
			CardWidth = GameInfo.CardsLibrary[0].returnBackImage().getWidth();
			CardHeight = GameInfo.CardsLibrary[0].returnBackImage().getHeight();
			
			int width = (int)(size_x*(1-RightPanelXRatio));
			space = (int) (SpaceRatio * CardWidth);
			CardsInRow = (int)((double)(width-space)/(double)(space+CardWidth));
			int Rows = (int) Math.ceil((double)Values.CARDS_AMOUNT/(double)CardsInRow);
			int height = space+(Rows*(space+CardHeight));
			
			int width2 = (int)(size_x*(RightPanelXRatio));
			CardsInRow2 = (int)((double)(width2-space)/(double)(space+CardWidth));
			int Rows2 = (int) Math.ceil((double)deckFields.size()/(double)CardsInRow2);
			int height2 = space+(Rows2*(space+CardHeight));
					
			setPreferredSize(new Dimension(size_x,Math.max(height,height2)));
			
			setFields();
			setLabel();
			setButtons();
			setBigCardObject();
			readDeck();
			repaint();
			
			HandlerClass handler = new HandlerClass();
	        this.addMouseListener(handler);
	        this.addMouseMotionListener(handler);
			
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
		
		private void setLabel() {
			label = new JLabel("Moja talia: ("+deckFields.size()+"/"+Values.MAX_DECK_CAPACITY+")");
			Font myFont = new Font("Arial",Font.BOLD,(int)Math.round(size_y*(0.025)));
			label.setFont(myFont);
			label.setBounds((int)(size_x*(1-RightPanelXRatio)), (int)(size_y*0.015), (int)(size_x*0.2), (int)(size_y*0.05));
			this.add(label);
		}

		private void setButtons() {
			Font font = new Font("Arial",Font.BOLD,(int)Math.round(size_y*(0.02)));
			int button_x = (int)(size_x*0.072);
			int button_y = (int)(size_y*0.035);
			
			ReturnToMenu = new JButton("Cofnij");
			ReturnToMenu.setBounds((int)(size_x*0.90),(int)(size_y*0.021),button_x,button_y);
			ReturnToMenu.setFont(font);
			ReturnToMenu.addActionListener(actList);
			this.add(ReturnToMenu);
			
			ClearDeck = new JButton("Wyczyœæ");
			ClearDeck.setBounds((int)(size_x*0.82),(int)(size_y*0.021),button_x,button_y);
			ClearDeck.setFont(font);
			ClearDeck.addActionListener(actList);
			this.add(ClearDeck);
			
			SaveDeck = new JButton("Zapisz");
			SaveDeck.setBounds((int)(size_x*0.74),(int)(size_y*0.021),button_x,button_y);
			SaveDeck.setFont(font);
			SaveDeck.addActionListener(actList);
			this.add(SaveDeck);
			
		}
		
		private void setBigCardObject() {
			ShowBigCardObject = new ShowBigCard();
			setBigCardPosition();
		}
		
		private void setBigCardPosition() {
			ShowBigCardObject.setCoordinates((int)Math.round(size_x*(0.15)), (int)Math.round(size_y*(0.05)));
		}
		
		public void saveMyDeck() {
			saveToGame();
			saveToFile();
		}
		
		private void saveToGame() {
			if(deckFields.size()==Values.MAX_DECK_CAPACITY) {
				for(int i=0;i<Values.MAX_DECK_CAPACITY;i++) {
					GameInfo.MyCards[i] = deckFields.get(i).getCardID();
				}
			}
		}
		
		private void saveToFile() {
			if(deckFields.size()==Values.MAX_DECK_CAPACITY) {
				//String fileName = JOptionPane.showInputDialog("Podaj nazwê pliku do zapisu (bez rozszerzenia)");
				
				String fileName = "Deck\\Deck.txt";
				String newline = System.getProperty("line.separator");
				
				try {
					Formatter myFormatter = new Formatter(fileName);
					for(int i=0;i<deckFields.size();i++) {
						String line = Integer.toString(deckFields.get(i).getCardID());
						myFormatter.format(line);
						if(i!=deckFields.size()-1) {
							myFormatter.format(newline);
						}
					}
					myFormatter.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "B³¹d zapisu");
				}
			}
		}
		
		private void readDeck() {
			clearMyDeck();
			for(int i=0; i<Values.MAX_DECK_CAPACITY; i++) {
				addCard(GameInfo.MyCards[i]);
			}
			updateMyDeck();
		}
		
		public void clearMyDeck() {
			while(deckFields.size()>0) {
				deckFields.remove(deckFields.size()-1);
			}
			updateMyDeck();
		}
		
		private void addCard(int id) {
			if(deckFields.size()<40) {
				Field field = new Field(0);
				field.addCard(id);
				field.openCard();
				deckFields.add(field);
				updateMyDeck();
			}
		}
		
		private void removeCard(int id) {
			if(deckFields.size()>0) {
				deckFields.remove(id);
				updateMyDeck();
			}
		}
		
		private void updateMyDeck() {
			for(int i=0; i<deckFields.size(); i++) {
				int x2 = space + (i%CardsInRow2)*(CardWidth+space) + (int)(size_x*(1-RightPanelXRatio));
				int y2 = (int)(space + Math.floor(i/CardsInRow2)*(CardHeight+space)) + (int)(size_y*0.1);
				deckFields.get(i).setPositions(x2, y2);
			}
			label.setText("Moja talia: ("+deckFields.size()+"/"+Values.MAX_DECK_CAPACITY+")");
			repaint();
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        for(int i=0; i<fields.length; i++) {
	        	g2d.drawImage(fields[i].returnFieldImage(), fields[i].returnX(), fields[i].returnY(), this);
	        }
	        for(int i=0; i<deckFields.size(); i++) {
	        	g2d.drawImage(deckFields.get(i).returnFieldImage(), deckFields.get(i).returnX(), deckFields.get(i).returnY(), this);
	        }
	        if(ShowBigCardObject.isShowing()) {
				g2d.drawImage(ShowBigCardObject.getImage(), ShowBigCardObject.getX(), ShowBigCardObject.getY(), this);
			}
		}
	
		private class HandlerClass implements MouseListener, MouseMotionListener{

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton()==MouseEvent.BUTTON3 && !ShowBigCardObject.isShowing()) {
					if(arg0.getX()<(size_x*(1-RightPanelXRatio))) {
						
						//klikniêcie we wszystkie karty
						for(int i=0; i<fields.length;i++) {
							if(arg0.getX()>fields[i].returnX() && arg0.getX()<fields[i].returnX()+CardWidth && arg0.getY()>fields[i].returnY() && arg0.getY()<fields[i].returnY()+CardHeight) {
								PopUpWindow miniPop = new PopUpWindow(true, false, true);
								miniPop.setCardID(i);
								miniPop.show(arg0.getComponent(), arg0.getX(), arg0.getY());
								break;
							}
						}
						
					}else {
						
						//klikniêcie w obecn¹ taliê
						for(int i=0; i<deckFields.size();i++) {
							if(arg0.getX()>deckFields.get(i).returnX() && arg0.getX()<deckFields.get(i).returnX()+CardWidth && arg0.getY()>deckFields.get(i).returnY() && arg0.getY()<deckFields.get(i).returnY()+CardHeight) {
								PopUpWindow miniPop = new PopUpWindow(false, true, true);
								miniPop.setOriginID(i);
								miniPop.setCardID(deckFields.get(i).getCardID());
								miniPop.show(arg0.getComponent(), arg0.getX(), arg0.getY());
								break;
							}
						}
					}
				}
				if(arg0.getButton()==MouseEvent.BUTTON1 && ShowBigCardObject.isShowing()) {
					ShowBigCardObject.setShowing(false);
					repaint();
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent arg0) {}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {}
			
		}
		
		private class PopUpWindow extends JPopupMenu {
			
			JMenuItem ShowCard;
			JMenuItem AddCard;
			JMenuItem RemoveCard;
			
			private int OriginID;
			private int CardID;
			
			PopUpWindow(){
				AddCard = new JMenuItem("Dodaj Kartê");
				RemoveCard = new JMenuItem("Odrzuæ Kartê");
				ShowCard = new JMenuItem("Poka¿ Kartê");
				ShowCard.addActionListener(popList);
				AddCard.addActionListener(popList);
				RemoveCard.addActionListener(popList);
				OriginID = 0;
				CardID = 0;
			}
			
			PopUpWindow(boolean a, boolean b, boolean c){
				if(a) {
					AddCard = new JMenuItem("Dodaj Kartê");
					AddCard.addActionListener(popList);
					add(AddCard);
				}
				if(b) {
					RemoveCard = new JMenuItem("Odrzuæ Kartê");
					RemoveCard.addActionListener(popList);
					add(RemoveCard);
				}
				if(c) {
					ShowCard = new JMenuItem("Poka¿ Kartê");
					ShowCard.addActionListener(popList);
					add(ShowCard);
				}
				OriginID = 0;
				CardID = 0;
			}
			
			public void setOriginID(int id) {
				OriginID = id;
			}
			
			public void setCardID(int id) {
				CardID = id;
			}
			
			private ActionListener popList = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(arg0.getSource()==ShowCard) {
						ShowBigCardObject.setImage(CardID);
						ShowBigCardObject.setShowing(true);
						repaint();
					}
					if(arg0.getSource()==AddCard) {
						addCard(CardID);
					}
					if(arg0.getSource()==RemoveCard) {
						removeCard(OriginID);
					}
				}
				
		    };
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
		myAllCardsPanel.clearMyDeck();
	}
	
	private void saveDeck() {
		myAllCardsPanel.saveMyDeck();
	}
	
	private void returnToMenu() {
		this.setVisible(false);
		StartWindow newStart = new StartWindow();
		newStart.setVisible(true);
		this.dispose();
	}
	
}
