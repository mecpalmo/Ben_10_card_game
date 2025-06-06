package pl.mecpalmo.config;

public class Values {
	
	public static final int X_SIZE_BIG = 1485;
	public static final int Y_SIZE_BIG = 1000;
	public static final int X_SIZE_SMALL = 1025;
	public static final int Y_SIZE_SMALL = 690;
	
	public static int DEFAULT_X = X_SIZE_BIG; // domy�lna szeroko�� okna gry 1545 1080
	public static int DEFAULT_Y = Y_SIZE_BIG; // domy�lna wysoko�� okna gry 1030 720
	
	public static final int CARDS_AMOUNT = 80; //liczba dost�pnych kart
	public static final int MAX_DECK_CAPACITY = 40; //rozmiar talii
	public static final int HAND_START_VALUE = 6; //pocz�tkowa ilo�� kart na r�ku
	public static final int FIELDS_AMOUNT = 64; // liczba p�l na planszy
	public static final int BACK_CARD_INDEX = 98;
	public static final int EMPTY_FIELD_INDEX = 99;
	public static final int HAND_ID = 97;
	public static final int REJECTED_ID = 96;
	
	public static final int PERIOD = 33; // czas od�wie�ania ekranu gry [ms]
	public static final int SEVER_PERIOD = 1000; // czas od�wie�ania serwera [ms]
	
	public static final String FONT = "Arial";
	public static final String FONT2 = "Comic Sans MS";
	
	public static int[] OpponentsFieldsValues;
	public static int OpponentsDeckSize;
	public static int OpponentsTopRejectedValue;
	public static int OpponentsHandSize;
	public static int OpponentsTransformationTime;
	public static int OpponentsLifeIndex;
	
	public static int[] MyFieldsValues;
	public static int MyDeckSize;
	public static int MyTopRejectedValue;
	public static int MyHandSize;
	public static int MyTransformationTime;
	public static int MyLifeIndex;
	
	public static boolean HOST = true;
	public static String IP_ADRESS = "127.0.0.1";
	public static int PORT = 6789;
	
	public static void resetOpponentsData() {
		OpponentsFieldsValues = new int[FIELDS_AMOUNT/2];
		for(int i=0; i<FIELDS_AMOUNT/2; i++) {
			OpponentsFieldsValues[i] = EMPTY_FIELD_INDEX;
		}
		OpponentsDeckSize = 0;
		OpponentsTopRejectedValue = EMPTY_FIELD_INDEX;
		OpponentsHandSize = 0;
		OpponentsTransformationTime = 0;
		OpponentsLifeIndex = 0;
	}
	
	public static void resetMyData() {
		MyFieldsValues = new int[FIELDS_AMOUNT/2];
		for(int i=0; i<FIELDS_AMOUNT/2; i++) {
			MyFieldsValues[i] = EMPTY_FIELD_INDEX;
		}
		MyDeckSize = 0;
		MyTopRejectedValue = 0;
		MyHandSize = 0;
		MyTransformationTime = 0;
		MyLifeIndex = 0;
	}
	
	public static void setBiggerScreen() {
		DEFAULT_X = X_SIZE_BIG;
		DEFAULT_Y = Y_SIZE_BIG;
	}
	
	public static void setSmallerScreen() {
		DEFAULT_X = X_SIZE_SMALL;
		DEFAULT_Y = Y_SIZE_SMALL;
	}
	
	public static void changeScreenSize() {
		if(DEFAULT_X == X_SIZE_SMALL) {
			DEFAULT_X = X_SIZE_BIG;
			DEFAULT_Y = Y_SIZE_BIG;
		}else {
			DEFAULT_X = X_SIZE_SMALL;
			DEFAULT_Y = Y_SIZE_SMALL;
		}
	}
}
