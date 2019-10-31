

public class Main {

	public static void main(String[] args) {
		
		LoadingWindow loading = new LoadingWindow();
		loading.setVisible(true);
		
		GameInfo.createCardsLibrary();
		
		loading.setVisible(false);
		loading.dispose();
		
		StartWindow myStartWindow = new StartWindow();
		myStartWindow.setVisible(true);
		
	}

}