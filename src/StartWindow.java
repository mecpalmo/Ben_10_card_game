import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartWindow extends JFrame {

	int size_x = 550;
	int size_y = 400;
	
	int button_x = 100;
	int button_y = 30;
	
	StartPanel myStartPanel;
	
	JButton StartGame, PrepareDeck, ExitGame;
	
	StartWindow(){
		super("Ben 10 Karciana Gra Kolekcjonerska - mecpalmoGames");
		this.setSize(size_x,size_y);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		myStartPanel = new StartPanel();
		this.add(myStartPanel);
		
		loadMyDeck();
		
		//okno na œrodku ekranu
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
	
	private void loadMyDeck() {
		
		
		
	}
	
	private class StartPanel extends JPanel{
		
		JLabel titleLabel;
		JLabel subTitleLabel;
		
		StartPanel(){
			
			setSize(size_x,size_y);
			this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
			setBackground(Color.GREEN);
			this.add(Box.createVerticalGlue());
			initLabels();
			this.add(Box.createVerticalGlue());
			initButtons();
			
		}
		
		private void initLabels() {
			
			titleLabel = new JLabel("Ben 10");
			Font font1 = new Font("Verdana",Font.BOLD,45);
			titleLabel.setFont(font1);
			titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			subTitleLabel = new JLabel("Karciana Gra Kolekcjonerska");
			Font font2 = new Font("Verdana",Font.BOLD,20);
			subTitleLabel.setFont(font2);
			subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			add(titleLabel);
			add(subTitleLabel);
			
		}
		
		private void initButtons() {
			
			Font font = new Font("Verdana",Font.BOLD,14);
			
			StartGame = new JButton("Rozpocznij Grê");
			StartGame.addActionListener(actList);
			StartGame.setSize(button_x, button_y);
			StartGame.setAlignmentX(Component.CENTER_ALIGNMENT);
			StartGame.setFont(font);
			
			PrepareDeck = new JButton("Stwórz taliê do gry");
			PrepareDeck.addActionListener(actList);
			PrepareDeck.setSize(button_x, button_y);
			PrepareDeck.setAlignmentX(Component.CENTER_ALIGNMENT);
			PrepareDeck.setFont(font);
			
			ExitGame = new JButton("Wyjdz");
			ExitGame.addActionListener(actList);
			ExitGame.setSize(button_x, button_y);
			ExitGame.setAlignmentX(Component.CENTER_ALIGNMENT);
			ExitGame.setFont(font);
			
			add(StartGame);
			this.add(Box.createVerticalGlue());
			add(PrepareDeck);
			this.add(Box.createVerticalGlue());
			add(ExitGame);
			this.add(Box.createVerticalGlue());
				
		}
		
	}
	
	private ActionListener actList = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==StartGame) {
				startTheGame();
			}
			if(e.getSource()==PrepareDeck) {
				createDeck();
			}
			if(e.getSource()==ExitGame) {
				System.exit(0);
			}
		}
	};
	
	private void startTheGame() {
		
		//okno ³adowania (ewentualne)
		
		//zamkniêcie menu
		this.setVisible(false);
		
		//przetasowanie kart
		GameInfo.shuffleMyCards();
		
		GameFrame myGameFrame = new GameFrame();
		myGameFrame.setVisible(true);
	}
	
	private void createDeck() {
		this.setVisible(false);
		CreatingDeckWindow myCreatingDeck = new CreatingDeckWindow(this);
		myCreatingDeck.setVisible(true);
	}
	
}
