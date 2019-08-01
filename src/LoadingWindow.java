import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class LoadingWindow extends JFrame{
	
	private int size_x = 200;
	private int size_y = 110;
	
	private JLabel label;
	
	LoadingWindow(){
		
		super("Patience <3");
		this.setSize(size_x,size_y);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		this.setResizable(false);
		
		this.setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
		this.add(Box.createVerticalGlue());
		setLabel();
		this.add(Box.createVerticalGlue());
		
	}
	
	private void setLabel() {
		label = new JLabel("Loading...");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		Font myFont = new Font("Arial",Font.BOLD,20);
		label.setFont(myFont);
		this.add(label);
	}
}
