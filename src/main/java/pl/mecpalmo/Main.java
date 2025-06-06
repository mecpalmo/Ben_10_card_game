package pl.mecpalmo;

import pl.mecpalmo.config.GameInfo;
import pl.mecpalmo.config.Values;
import pl.mecpalmo.menu.LoadingWindow;
import pl.mecpalmo.menu.StartWindow;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Main {

	public static void main(String[] args) {
		
		LoadingWindow loading = new LoadingWindow();
		loading.setVisible(true);
		
		setResolution();
		
		GameInfo.createCardsLibrary();
		
		loading.setVisible(false);
		loading.dispose();
		
		StartWindow myStartWindow = new StartWindow();
		myStartWindow.setVisible(true);
		
	}
	
	private static void setResolution() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		if(dim.height >= 1080) {
			Values.setBiggerScreen();
		}else {
			Values.setSmallerScreen();
		}
	}

}