
public class Hover {

	private int DrawX;
	private int DrawY;
	
	private int differenceX;
	private int differenceY;
	
	private boolean Hovering;
	private int CardID;
	private int OriginID;
	private boolean wasOpen; //do przemieszczania z pól bitwy, czy przemieszczona karta by³a otwarta
	
	Hover(){
		
		DrawX=0;
		DrawY=0;
		differenceX = 0;
		differenceY = 0;
		Hovering = false;
		OriginID = Values.HAND_ID;
		wasOpen = false;
		
	}
	
	public void setDifferences(int drawX, int drawY, int mouseX, int mouseY){
		differenceX = mouseX - drawX;
		differenceY = mouseY - drawY;
		DrawX = mouseX - differenceX;
		DrawY = mouseY - differenceY;
	}
	
	public void setDrawPositions(int x, int y) {
		DrawX = x - differenceX;
		DrawY = y - differenceY;
	}
	
	public void setHovering(boolean is) {
		Hovering = is;
	}
	
	public boolean isHovering() {
		return Hovering;
	}
	
	public void setCardID(int id) {
		CardID = id;
	}
	
	public int getCardID() {
		return CardID;
	}
	
	public int getX() {
		return DrawX;
	}
	
	public int getY() {
		return DrawY;
	}
	
	public void setOriginID(int id) {
		OriginID = id;
	}
	
	public int OriginID() {
		return OriginID;
	}
	
	public void setWasOpen(boolean is) {
		wasOpen = is;
	}
	
	public boolean wasOpen() {
		return wasOpen;
	}
}
