import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
		super("Ben 10 Karciana Gra Kolekcjonerska - mecpalmoGames");
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
			this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
			setBackground(Color.BLUE);
			this.add(Box.createVerticalGlue());
			initRadioButtons();
			this.add(Box.createVerticalGlue());
			initTextField();
			this.add(Box.createVerticalGlue());
			initButtons();
			this.add(Box.createVerticalGlue());
			textField.setPreferredSize(new Dimension(150,30));
		}
		
		private void initRadioButtons() {
			
			hostButton = new JRadioButton("Host");
			guestButton = new JRadioButton("Guest");
			
			hostButton.setSelected(true);

		    //Group the radio buttons.
		    ButtonGroup group = new ButtonGroup();
		    group.add(hostButton);
		    group.add(guestButton);

		    //Register a listener for the radio buttons.
		    hostButton.addActionListener(radioListener);
		    guestButton.addActionListener(radioListener);
		    
		    this.add(hostButton);
		    this.add(guestButton);
		    hostButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		    guestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		    
		}
		
		private void initTextField() {
			
			textField = new JTextField();
			textField.setEditable(false);
			textField.setPreferredSize(new Dimension(150,30));
			this.add(textField);
			textField.setAlignmentX(Component.CENTER_ALIGNMENT);
			
		}
		
		private void initButtons() {
			
			backButton = new JButton("Cofnij");
			confirmButton = new JButton("Zatwierdz");
			backButton.addActionListener(buttonListener);
			confirmButton.addActionListener(buttonListener);
			
			this.add(backButton);
			backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.add(Box.createVerticalGlue());
			this.add(confirmButton);
			confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			
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
