import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Hand {

	private boolean showing;
	private int indexOfFocus;
	private List<Integer> Cards = new ArrayList<Integer>();
	
	private static final float backCardPart = 0.3f;
	private static final float backUpParameter = 0.15f;
	private int singleCardWidth;
	private int width;
	private int CurrentBoardWidth;
	private int StartX;
	private int focusedX;
	private int ShowY;
	private int HideY;
	private int OpponentsY;
	
	private static BufferedImage OpponentsHandCard; 
	
	Hand() {
		showing = false;
		indexOfFocus = 0;
		singleCardWidth = 0;
		width = 0;
		CurrentBoardWidth = 0;
		StartX = 0;
		focusedX = 0;
		ShowY = 0;
		HideY = 0;
		OpponentsY = 0;
	}
	
	public void addCard(int _id) {
		Cards.add(_id);
		updateValues();
	}
	
	public void removeCard(int index) {
		if(indexOfFocus==(Cards.size()-1) && indexOfFocus>0) {
			indexOfFocus--;
		}
		Cards.remove(index);
		updateValues();
	}
	
	public int size() {
		return Cards.size();
	}
	
	public int getCard(int index) {
		return Cards.get(index);
	}
	
	public void setIndexOfFocus(int index) {
		indexOfFocus = index;
		updateValues();
	}
	
	public int getIndexOfFocus() {
		return indexOfFocus;
	}
	
	public void showHand() {
		showing = true;
	}
	
	public void hideHand() {
		showing = false;
	}
	
	public boolean isShowing() {
		return showing;
	}
	
	public void updateValues() {
		if(Cards.size()==0) {
			showing = false;
		}else {
		singleCardWidth = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
		width = (int) ((Cards.size()-1)*backCardPart*singleCardWidth) + singleCardWidth;
		StartX = (CurrentBoardWidth-width)/2;
		focusedX = (int) (StartX + (indexOfFocus*singleCardWidth*backCardPart));
		}
	}
	
	public void setCurrentBoardWidth(int width) {
		CurrentBoardWidth = width;
	}
	
	public int startX() {
		return StartX;
	}
	
	public int focusedX() {
		return focusedX;
	}
	
	public int singleCardWidth() {
		return singleCardWidth;
	}
	
	public int width() {
		return width;
	}
	
	public void setYs(int show, int hide, int opponents) {
		ShowY = show;
		HideY = hide;
		OpponentsY = opponents;
	}
	
	public int ShowY() {
		return ShowY;
	}
	
	public int HideY() {
		return HideY;
	}
	
	public int OpponentsY() {
		return OpponentsY;
	}
	
	public static float backCardPart() {
		return backCardPart;
	}
	
	public static float backUpParameter() {
		return backUpParameter;
	}
	
	public static BufferedImage OpponentsHandCard() {
		return OpponentsHandCard;
	}
	
	public static void setOpponentsHandCard() {
		OpponentsHandCard = rotateImage(GameInfo.CardsLibrary[0].returnBackImage(),180);
	}
	
	private static BufferedImage rotateImage(BufferedImage bimg, double angle) {
		BufferedImage newImage;
		AffineTransform tx = new AffineTransform();
		int w = bimg.getWidth();
		int h = bimg.getHeight();
        if(angle==90 || angle==270) {
        	tx.translate(h / 2, w / 2);
            tx.rotate(Math.toRadians(angle));
        	tx.translate(-w/2, -h/2);
        }else {
        	tx.translate(w/2, h/2);
            tx.rotate(Math.toRadians(angle));
        	tx.translate(-w/2, -h/2);
        }
		AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
		newImage = op.filter(bimg, null);
		return newImage;
	}
}
