import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Stack {

	private int Draw_X;
	private int Draw_Y;
	private List<Integer> Cards = new ArrayList<Integer>();
	private BufferedImage TopImage;
	private int RotateValue;
	private boolean isDeckOpen;
	
	Stack(){
		Draw_X = 0;
		Draw_Y = 0;
		RotateValue = 0;
		isDeckOpen = false;
	}
	
	Stack(int x, int y){
		Draw_X = x;
		Draw_Y = y;
		RotateValue = 0;
		isDeckOpen = false;
	}
	
	Stack(int rv, int x, int y){
		Draw_X = x;
		Draw_Y = y;
		RotateValue = rv;
		isDeckOpen = false;
	}
	
	Stack(int rv, int x, int y, boolean isOpen){
		Draw_X = x;
		Draw_Y = y;
		RotateValue = rv;
		isDeckOpen = isOpen;
	}
	
	Stack(int rv, boolean isOpen){
		Draw_X = 0;
		Draw_Y = 0;
		RotateValue = rv;
		isDeckOpen = isOpen;
	}
	
	public void addCard(int id) {
		Cards.add(id);
		if(isDeckOpen) {
			updateFrontImage();
		}
	}
	
	public void removeCard() {
		Cards.remove(Cards.size()-1);
		if(isDeckOpen && Cards.size()>0) {
			updateFrontImage();
		}
	}
	
	public void removeCard(int id) {
		Cards.remove(id);
		if(isDeckOpen && Cards.size()>0) {
			updateFrontImage();
		}
	}
	
	public int getCard(int id) {
		return Cards.get(id);
	}
	
	public int size() {
		return Cards.size();
	}
	
	public int returnX() {
		return Draw_X;
	}
	
	public int returnY() {
		return Draw_Y;
	}
	
	public void setPositions(int x, int y) {
		Draw_X = x;
		Draw_Y = y;
	}
	
	public int lastCardIndex() {
		if(Cards.size()==0) {
			return Values.EMPTY_FIELD_INDEX;
		}else {
			return Cards.get(Cards.size()-1);
		}
	}
	
	public BufferedImage returnStackImage() {
		return TopImage;
	}
	
	public void setImage(BufferedImage bi) {
		if(RotateValue==180) {
			TopImage = GameInfo.rotateImage(bi,RotateValue);
		}else {
			TopImage = bi;
		}
	}
	
	public void updateFrontImage() {
		if(Cards.size()>0) {
			if(RotateValue==180) {
				TopImage = GameInfo.rotateImage(GameInfo.CardsLibrary[Cards.get(Cards.size()-1)].returnFrontImage(),RotateValue);
			}else {
				TopImage = GameInfo.CardsLibrary[Cards.get(Cards.size()-1)].returnFrontImage();
			}
		}
	}
	
}
