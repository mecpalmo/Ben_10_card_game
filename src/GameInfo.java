import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
}
