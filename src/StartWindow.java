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

	JButton StartGame, PrepareDeck, ExitGame;
	
	StartWindow(){
		super(Strings.s11);
		this.setSize(size_x,size_y);
		setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		StartPanel myStartPanel = new StartPanel();
		this.add(myStartPanel);
		
		GameInfo.loadDeckFromFile();
		
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
			
			titleLabel = new JLabel(Strings.s40);
			Font font1 = new Font(Values.FONT,Font.BOLD,50);
			titleLabel.setFont(font1);
			titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			subTitleLabel = new JLabel(Strings.s41);
			Font font2 = new Font(Values.FONT,Font.BOLD,25);
			subTitleLabel.setFont(font2);
			subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			add(titleLabel);
			add(subTitleLabel);
			
		}
		
		private void initButtons() {
			
			Font font = new Font(Values.FONT,Font.BOLD,14);
			
			StartGame = new JButton(Strings.s42);
			StartGame.addActionListener(actList);
			StartGame.setAlignmentX(Component.CENTER_ALIGNMENT);
			StartGame.setFont(font);
			StartGame.setFocusable(false);
			
			PrepareDeck = new JButton(Strings.s43);
			PrepareDeck.addActionListener(actList);
			PrepareDeck.setAlignmentX(Component.CENTER_ALIGNMENT);
			PrepareDeck.setFont(font);
			PrepareDeck.setFocusable(false);
			
			ExitGame = new JButton(Strings.s44);
			ExitGame.addActionListener(actList);
			ExitGame.setAlignmentX(Component.CENTER_ALIGNMENT);
			ExitGame.setFont(font);
			ExitGame.setFocusable(false);
			
			add(StartGame);
			this.add(Box.createVerticalGlue());
			add(PrepareDeck);
			this.add(Box.createVerticalGlue());
			add(ExitGame);
				
		}
		
		private void initRadioButtons() {
			
			HighRes = new JRadioButton(Strings.s45);
			SmallRes = new JRadioButton(Strings.s46);
			
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
