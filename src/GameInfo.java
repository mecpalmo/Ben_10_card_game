import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

public class GameInfo {

	public static Card[] CardsLibrary = new Card[Values.CARDS_AMOUNT];
	
	public static int[] MyCards = new int[Values.MAX_DECK_CAPACITY];
	
	public static void resizeAllCards() {
		for(int i=0;i<Values.CARDS_AMOUNT;i++) {
			CardsLibrary[i].setCurrentImages();
		}
	}
	
	public static void createCardsLibrary() {
		for(int i=0;i<Values.CARDS_AMOUNT;i++) {
			CardsLibrary[i] = new Card(i);
		}
	}
	
	public static void shuffleMyCards() {
		
		// do skoñczenia
		Integer[] temp = new Integer[Values.MAX_DECK_CAPACITY];
		for(int i=0;i<temp.length;i++) {
			temp[i] = MyCards[i];
		}
		
		List<Integer> list = Arrays.asList(temp);
		
		Collections.shuffle(list);
		
		for(int i=0;i<temp.length;i++) {
			MyCards[i] = list.get(i);
		}
		
	}
	
	public static void loadDeckFromFile() {
		String fileName = "Deck\\Deck.txt";
		//fileName = JOptionPane.showInputDialog("Podaj nazwê pliku");
		
		BufferedReader reader;
		FileReader feader;
		try {
			feader = new FileReader(fileName);
			reader = new BufferedReader(feader);
			for(int i=0; i<Values.MAX_DECK_CAPACITY; i++) {
				String id = reader.readLine();
				if(id == null) {
					break;
				}else {
					int ID = Integer.parseInt(id);
					MyCards[i] = ID;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Nie znaleziono zapisanej talii");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "B³¹d wczytywania talii");
		}
		
	}
}
