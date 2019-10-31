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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class CreatingDeckWindow extends JFrame{

	private JButton ClearDeck, SaveDeck, ReturnToMenu, CreateNewDeck;
	private AllCardsPanel myAllCardsPanel;
	JScrollPane ScrollAllCards;
	
	private int size_x, size_y;
	
	CreatingDeckWindow(){
		super(Strings.s11);
		this.setBackground(Color.GRAY);
		
		size_x = (int) (Values.DEFAULT_X * 1.265);
		size_y = Values.DEFAULT_Y;
		
		JFrame temp = new JFrame();
		temp.pack();
		Insets insets = temp.getInsets();
		temp = null;
		this.setSize(insets.left + size_x + insets.right + insets.right, insets.top + size_y);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		myAllCardsPanel = new AllCardsPanel();
		ScrollAllCards = new JScrollPane(myAllCardsPanel);
		ScrollAllCards.getVerticalScrollBar().setUnitIncrement(23);
		add(ScrollAllCards);
		preventScrollingIssue();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, 0);
	}
	
	private class AllCardsPanel extends JPanel{
		
		private Field[] fields;
		private List<Field> deckFields = new ArrayList<Field>();
		private Graphics2D g2d;
		private JLabel label;
		private BufferedImage Background;
		private JComboBox<String> comboBox;
		private String deckName;
		
		private final float RightPanelXRatio = 0.38f; // procentowa szerokoœæ czêœci panelu na w³asn¹ taliê
		private final float SpaceRatio = 0.05f; // procentowy odstêp miêdzy kartami odnosz¹cy siê do rozmiaru karty
		private final int MyDeckSpaceY = (int)(size_y*0.13);// odstêp pionowy od góry w³asnej talii
		private int CardWidth, CardHeight, CardsInRow, CardsInRow2, space, panelHeight;
		
		private ShowBigCard ShowBigCardObject = new ShowBigCard();
		
		AllCardsPanel(){
			
			setLayout(null);
			setBackground(Color.GREEN);
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
					
			panelHeight = Math.max(height,(height2+MyDeckSpaceY));
			setPreferredSize(new Dimension(size_x,panelHeight));
			
			setBackground();
			setFields();
			setLabel();
			setButtons();
			setComboBox();
			setBigCardObject();
			readDeck();
			repaint();
			
			HandlerClass handler = new HandlerClass();
	        this.addMouseListener(handler);
	        this.addMouseMotionListener(handler);
	        
	        System.out.println("Szerokoœæ: "+size_x+" Height: "+Math.max(height,(height2+MyDeckSpaceY)));
	        System.out.println("Pierwsza: "+(fields[CardsInRow-1].returnX()+CardWidth)+" Druga: "+deckFields.get(0).returnX());
		}
		
		private void setBackground() {
			try {
				Background = ImageIO.read(new File("Images/deckBackground.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Background.getWidth() != size_x || Background.getHeight() < panelHeight) {
				Background = resizeImage(Background, size_x, panelHeight);
			}
		}
		
		private void setComboBox() {
			File folder = new File("Deck\\");
			File[] listOfFiles = folder.listFiles();
			ArrayList<String> list = new ArrayList<String>();
			
			for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile()) {
			    String name = listOfFiles[i].getName();
			    if (name.contains(".txt")) {
			    	name = name.replace(".txt", "");
			    	if(!name.contains("Default")) {
			    		list.add(name);
			    	}
			    }
			  }
			}
			
			String[] List = new String[list.size()];
			list.toArray(List);
			
			comboBox = new JComboBox<String>(List);
			comboBox.setSelectedIndex(0);
			GameInfo.loadSpecificDeck((String)comboBox.getSelectedItem());
			comboBox.setBounds((int)(size_x*0.875),(int)(size_y*0.065), (int)(size_x*0.117), (int)(size_y*0.035));
			comboBox.addActionListener(new ComboBoxListener());
			this.add(comboBox);
		}
		
		public void updateComboList() {
			comboBox.addItem(deckName);
		}
		
 		private void setFields() {
			fields = new Field[Values.CARDS_AMOUNT];
			for(int i=0; i<Values.CARDS_AMOUNT; i++) {
				int x = space + (i%CardsInRow)*(CardWidth+space);
				int y = (int) (space + Math.floor(i/CardsInRow)*(CardHeight+space));
				fields[i] = new Field(0,x,y);
				fields[i].addCard(i);
				fields[i].openCard();
			}
		}
		
		private void setLabel() {
			label = new JLabel(Strings.s12+deckFields.size()+"/"+Values.MAX_DECK_CAPACITY+")");
			Font myFont = new Font(Values.FONT,Font.BOLD,(int)Math.round(size_y*(0.026)));
			label.setFont(myFont);
			label.setForeground(Color.WHITE);
			label.setBounds((int)(size_x*(1-RightPanelXRatio))+(5*space), (int)(size_y*0.015), (int)(size_x*0.2), (int)(size_y*0.05));
			this.add(label);
		}

		private void setButtons() {
			Font font = new Font(Values.FONT,Font.BOLD,(int)Math.round(size_y*(0.018)));
			int button_x = (int)(size_x*0.072);
			int button_y = (int)(size_y*0.035);
			
			ReturnToMenu = new JButton(Strings.s20);
			ReturnToMenu.setBounds((int)(size_x*0.92),(int)(size_y*0.021),button_x,button_y);
			ReturnToMenu.setFont(font);
			ReturnToMenu.addActionListener(actList);
			this.add(ReturnToMenu);
			
			ClearDeck = new JButton(Strings.s13);
			ClearDeck.setBounds((int)(size_x*0.84),(int)(size_y*0.021),button_x,button_y);
			ClearDeck.setFont(font);
			ClearDeck.addActionListener(actList);
			this.add(ClearDeck);
			
			SaveDeck = new JButton(Strings.s14);
			SaveDeck.setBounds((int)(size_x*0.76),(int)(size_y*0.021),button_x,button_y);
			SaveDeck.setFont(font);
			SaveDeck.addActionListener(actList);
			this.add(SaveDeck);
			
			CreateNewDeck = new JButton("Utwórz now¹ taliê");
			CreateNewDeck.setBounds((int)(size_x*0.76),(int)(size_y*0.065),(int)(button_x*1.5),button_y);
			CreateNewDeck.setFont(font);
			CreateNewDeck.addActionListener(actList);
			this.add(CreateNewDeck);
		}
		
		private void setBigCardObject() {
			ShowBigCardObject = new ShowBigCard();
			setBigCardPosition();
		}
		
		private void setBigCardPosition() {
			ShowBigCardObject.setCoordinates((int)Math.round(size_x*(0.18)), (int)Math.round(size_y*(0.05)));
		}
		
		public void saveMyDeck() {
			saveToGame();
			saveToFile();
			saveNewFile();
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
				
				String fileName = "Deck\\Default.txt";
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
					JOptionPane.showMessageDialog(null, Strings.s15);
				}
			}
		}
		
		private void saveNewFile() {
			if(deckFields.size()==Values.MAX_DECK_CAPACITY && deckName.length()>0) {
				//String fileName = JOptionPane.showInputDialog("Podaj nazwê pliku do zapisu (bez rozszerzenia)");
				
				String fileName = "Deck\\"+deckName+".txt";
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
					JOptionPane.showMessageDialog(null, Strings.s15);
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
			sortMyDeck();
			for(int i=0; i<deckFields.size(); i++) {
				int x2 = space + (i%CardsInRow2)*(CardWidth+space) + (int)(size_x*(1-RightPanelXRatio));
				int y2 = (int)(space + Math.floor(i/CardsInRow2)*(CardHeight+space)) + MyDeckSpaceY;
				deckFields.get(i).setPositions(x2, y2);
			}
			label.setText(Strings.s12+deckFields.size()+"/"+Values.MAX_DECK_CAPACITY+")");
			repaint();
		}
		
		private void sortMyDeck() {
			int n = deckFields.size();
			do {
				for(int i=0;i<n-1;i++) {
					if(deckFields.get(i).getCardID() > deckFields.get(i+1).getCardID()) {
						int id = deckFields.get(i).getCardID();
						deckFields.get(i).addCard(deckFields.get(i+1).getCardID());
						deckFields.get(i).openCard();
						deckFields.get(i+1).addCard(id);
						deckFields.get(i+1).openCard();
					}
				}
				n = n-1;
			}while(n > 1);	  
		}
		
		public void setDeckName(String name) {
			deckName = name;
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        
	        g2d.drawImage(Background,0,0,this);
	        for(int i=0; i<fields.length; i++) {
	        	g2d.drawImage(fields[i].returnFieldImage(), fields[i].returnX(), fields[i].returnY(), this);
	        }
	        for(int i=0; i<deckFields.size(); i++) {
	        	g2d.drawImage(deckFields.get(i).returnFieldImage(), deckFields.get(i).returnX(), deckFields.get(i).returnY(), this);
	        }
	        if(ShowBigCardObject.isShowing()) {
				g2d.drawImage(ShowBigCardObject.getImage(), ShowBigCardObject.getX(), ShowBigCardObject.getY()+ScrollAllCards.getVerticalScrollBar().getValue(), this);
			}
		}
		
		private BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
		    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_4BYTE_ABGR);
		    Graphics2D g2d = dimg.createGraphics();
		    g2d.drawImage(tmp, 0, 0, null);
		    g2d.dispose();
		    return dimg;
		}
	
		private class HandlerClass implements MouseListener, MouseMotionListener{

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton()==MouseEvent.BUTTON3 && !ShowBigCardObject.isShowing()) {
					if(arg0.getX()<(size_x*(1-RightPanelXRatio))) {
						
						//klikniêcie we wszystkie karty
						for(int i=0; i<fields.length;i++) {
							if(arg0.getX()>fields[i].returnX() && arg0.getX()<fields[i].returnX()+CardWidth && arg0.getY()>fields[i].returnY() && arg0.getY()<fields[i].returnY()+CardHeight) {
								PopUpWindow miniPop = new PopUpWindow(true, false);
								miniPop.setCardID(i);
								miniPop.show(arg0.getComponent(), arg0.getX(), arg0.getY());
								break;
							}
						}
						
					}else {
						
						//klikniêcie w obecn¹ taliê
						for(int i=0; i<deckFields.size();i++) {
							if(arg0.getX()>deckFields.get(i).returnX() && arg0.getX()<deckFields.get(i).returnX()+CardWidth && arg0.getY()>deckFields.get(i).returnY() && arg0.getY()<deckFields.get(i).returnY()+CardHeight) {
								PopUpWindow miniPop = new PopUpWindow(false, true);
								miniPop.setOriginID(i);
								miniPop.setCardID(deckFields.get(i).getCardID());
								miniPop.show(arg0.getComponent(), arg0.getX(), arg0.getY());
								break;
							}
						}
					}
				}
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getButton()==MouseEvent.BUTTON1 && !ShowBigCardObject.isShowing()) {
					if(arg0.getX()<(size_x*(1-RightPanelXRatio))) {
						
						//klikniêcie we wszystkie karty
						for(int i=0; i<fields.length;i++) {
							if(arg0.getX()>fields[i].returnX() && arg0.getX()<fields[i].returnX()+CardWidth && arg0.getY()>fields[i].returnY() && arg0.getY()<fields[i].returnY()+CardHeight) {
								ShowBigCardObject.setImage(i);
								ShowBigCardObject.setShowing(true);
								ScrollAllCards.repaint();
								break;
							}
						}
						
					}else {
						
						//klikniêcie w obecn¹ taliê
						for(int i=0; i<deckFields.size();i++) {
							if(arg0.getX()>deckFields.get(i).returnX() && arg0.getX()<deckFields.get(i).returnX()+CardWidth && arg0.getY()>deckFields.get(i).returnY() && arg0.getY()<deckFields.get(i).returnY()+CardHeight) {
								ShowBigCardObject.setImage(deckFields.get(i).getCardID());
								ShowBigCardObject.setShowing(true);
								ScrollAllCards.repaint();
								break;
							}
						}
					}
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
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
			public void mouseDragged(MouseEvent arg0) {}
			
		}
		
		private class PopUpWindow extends JPopupMenu {
			
			JMenuItem AddCard;
			JMenuItem RemoveCard;
			
			private int OriginID;
			private int CardID;
			
			PopUpWindow(boolean a, boolean b){
				if(a) {
					AddCard = new JMenuItem(Strings.s16);
					AddCard.addActionListener(popList);
					add(AddCard);
				}
				if(b) {
					RemoveCard = new JMenuItem(Strings.s17);
					RemoveCard.addActionListener(popList);
					add(RemoveCard);
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
					if(arg0.getSource()==AddCard) {
						addCard(CardID);
					}
					if(arg0.getSource()==RemoveCard) {
						removeCard(OriginID);
					}
				}
				
		    };
		}
	
		private class ComboBoxListener implements ActionListener {
		    
		    public void actionPerformed(ActionEvent e) {
		        JComboBox cb = (JComboBox)e.getSource();
		        
		        String x = (String)cb.getSelectedItem();
		        if(x != deckName) {
		        deckName = x;
		        GameInfo.loadSpecificDeck(deckName);
		        readDeck();
		        repaint();
		        }
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
			if(e.getSource()==CreateNewDeck) {
				createNewDeck();
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
	
	private void createNewDeck() {
		String filename = "";
		while(filename.length() <=0 || filename.contains("Default")) {
			filename = JOptionPane.showInputDialog("Podaj nazwê nowej talii (nie mo¿e zawieraæ \"Default\")");
		}
		myAllCardsPanel.setDeckName(filename);
		myAllCardsPanel.updateComboList();
		myAllCardsPanel.saveMyDeck();
	}
	
	private void preventScrollingIssue() {
		
		ScrollAllCards.getVerticalScrollBar().addAdjustmentListener(new java.awt.event.AdjustmentListener(){
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent ae){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        ScrollAllCards.repaint();
                    }
                });
            }
        });
		
        /*ScrollAllCards.getHorizontalScrollBar().addAdjustmentListener(new java.awt.event.AdjustmentListener(){
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent ae){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        ScrollAllCards.repaint();
                    }
                });
            }
        });*/
	}
}
