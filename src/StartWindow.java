import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class StartWindow extends JFrame {

	int size_x = 550;
	int size_y = 400;
	
	int button_x = 100;
	int button_y = 30;

	JButton StartGame, PrepareDeck, ExitGame;
	
	StartWindow(){
		super("Ben 10 Karciana Gra Kolekcjonerska - mecpalmoGames");
		this.setSize(size_x,size_y);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		
		StartPanel myStartPanel = new StartPanel();
		this.add(myStartPanel);
		
		GameInfo.loadDeckFromFile();
		
		//okno na œrodku ekranu
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
	
	private class StartPanel extends JPanel{
		
		JLabel titleLabel, subTitleLabel;
		JRadioButton HighRes, SmallRes;
		
		StartPanel(){
			
			setSize(size_x,size_y);
			this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
			setBackground(Color.GREEN);
			this.add(Box.createVerticalGlue());
			initLabels();
			this.add(Box.createVerticalGlue());
			initButtons();
			this.add(Box.createVerticalGlue());
			initRadioButtons();
			this.add(Box.createVerticalGlue());
			
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
				
		}
		
		private void initRadioButtons() {
			
			HighRes = new JRadioButton("Wysoka Rozdzielczoœæ");
			SmallRes = new JRadioButton("Œrednia Rozdzielczoœæ");
			
			if(Values.DEFAULT_X==Values.X_SIZE_BIG) {
				HighRes.setSelected(true);
			}else {
				SmallRes.setSelected(true);
			}
	
		    ButtonGroup group = new ButtonGroup();
		    group.add(HighRes);
		    group.add(SmallRes);
		    HighRes.setOpaque(false);
		    SmallRes.setOpaque(false);
		    HighRes.addActionListener(radioListener);
		    SmallRes.addActionListener(radioListener);
		    HighRes.setAlignmentX(Component.LEFT_ALIGNMENT);
		    SmallRes.setAlignmentX(Component.LEFT_ALIGNMENT);
		    this.add(HighRes);
		    this.add(SmallRes);
			
		}
		
		private ActionListener radioListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==HighRes) {
					StartGame.setEnabled(false);
					PrepareDeck.setEnabled(false);
					Values.setBiggerScreen();
					GameInfo.resizeAllCards();
					StartGame.setEnabled(true);
					PrepareDeck.setEnabled(true);
				}
				if(e.getSource()==SmallRes) {
					StartGame.setEnabled(false);
					PrepareDeck.setEnabled(false);
					Values.setSmallerScreen();
					GameInfo.resizeAllCards();
					StartGame.setEnabled(true);
					PrepareDeck.setEnabled(true);
				}
			}
		};
		
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
		this.setVisible(false);
		CreatingGameWindow creatingGame = new CreatingGameWindow();
		creatingGame.setVisible(true);
		this.dispose();
	}
	
	private void createDeck() {
		this.setVisible(false);
		CreatingDeckWindow myCreatingDeck = new CreatingDeckWindow();
		myCreatingDeck.setVisible(true);
		this.dispose();
	}
	
}
