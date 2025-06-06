package pl.mecpalmo.menu;

import pl.mecpalmo.config.GameInfo;
import pl.mecpalmo.config.Strings;
import pl.mecpalmo.config.Values;
import pl.mecpalmo.game.view.GameFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CreatingGameWindow extends JFrame{

	private int sizex = 400;  
	private int sizey = 400;
	
	JRadioButton hostButton, guestButton;
	JTextField textField;
	JButton backButton, confirmButton;
	
	CreatingGamePanel menuPanel;
	
	CreatingGameWindow(){
		super(Strings.s11);
		setSize(sizex,sizey);
		setResizable(false);
		menuPanel = new CreatingGamePanel();
		add(menuPanel);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
	}
	
	private void startGame() {
		menuPanel.saveIP();
		this.setVisible(false);
		GameInfo.shuffleMyCards();
		GameFrame myGameFrame = new GameFrame();
		myGameFrame.setVisible(true);
		this.dispose();
	}
	
	private void backToStart() {
		this.setVisible(false);
		StartWindow newStart = new StartWindow();
		newStart.setVisible(true);
		this.dispose();
	}
	
	private class CreatingGamePanel extends JPanel{
		
		CreatingGamePanel(){
			setSize(sizex,sizey);
			setLayout(null);
			setBackground(Color.LIGHT_GRAY);
			initRadioButtons();
			initTextField();
			initButtons();
		}
		
		private void initRadioButtons() {
			
			hostButton = new JRadioButton(Strings.s18);
			guestButton = new JRadioButton(Strings.s19);
			
			hostButton.setSelected(true);
			hostButton.setOpaque(false);
			guestButton.setOpaque(false);
			
			Font myFont = new Font(Values.FONT,Font.BOLD,16);
			hostButton.setFont(myFont);
			guestButton.setFont(myFont);
			
			int width = 90;
			int height = 40;
			hostButton.setBounds((int) ((sizex/2-width)/1.2), 40, width, height);
			guestButton.setBounds((int) ((sizex/2-width)/3.3+sizex/2), 40, width, height);
			
		    ButtonGroup group = new ButtonGroup();
		    group.add(hostButton);
		    group.add(guestButton);
		    hostButton.addActionListener(radioListener);
		    guestButton.addActionListener(radioListener);
		    this.add(hostButton);
		    this.add(guestButton);
		    
		}
		
		private void initTextField() {
			textField = new JTextField();
			textField.setEditable(false);
			int width = 200;
			int height = 30;
			textField.setBounds((sizex-width)/2, (sizey-height)/2, width, height);
			this.add(textField);
		}
		
		private void initButtons() {
			
			backButton = new JButton(Strings.s20);
			confirmButton = new JButton(Strings.s21);
			backButton.addActionListener(buttonListener);
			confirmButton.addActionListener(buttonListener);
			int width = 130;
			int height = 30;
			backButton.setBounds((sizex/2-width)/2, 300, width, height);
			confirmButton.setBounds((sizex/2-width)/2+sizex/2, 300, width, height);
			this.add(backButton);
			this.add(confirmButton);
		}
		
		public void saveIP() {
			if(!Values.HOST) {
				Values.IP_ADRESS=textField.getText();
			}
		}
	}
	
	private ActionListener radioListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==hostButton) {
				Values.HOST = true;
				textField.setEditable(false);
			}
			if(e.getSource()==guestButton) {
				Values.HOST = false;
				textField.setEditable(true);
			}
		}
	};
	
	private ActionListener buttonListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==backButton) {
				backToStart();
			}
			if(e.getSource()==confirmButton) {
				startGame();
			}
		}
	};
	
}
