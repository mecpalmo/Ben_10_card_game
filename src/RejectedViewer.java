
public class RejectedViewer {

	private boolean Showing;
	private int[] indexOfFocus;
	private int StartX;
	private int[] StartY;
	private int[] focusedX;
	private static final int SingleRowCapacity = 14;
	
	RejectedViewer(){
		Showing = false;
		StartY = new int[3];
		indexOfFocus = new int[3];
		focusedX = new int[3];
	}
	
	public boolean isShowing() {
		return Showing;
	}
	
	public void setShowing(boolean is) {
		Showing = is;
		if(is) {
			indexOfFocus[0]=0;
			indexOfFocus[1]=RejectedViewer.SingleRowCapacity();
			indexOfFocus[2]=RejectedViewer.SingleRowCapacity()*2;
		}
		updateViewer();
	}
	
	public void updateViewer() {
		int width = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
		focusedX[0] = (int) (StartX + (indexOfFocus[0]*width*backCardPart()));
		focusedX[1] = (int) (StartX + ((indexOfFocus[1]-SingleRowCapacity)*width*backCardPart()));
		focusedX[2] = (int) (StartX + ((indexOfFocus[2]-SingleRowCapacity*2)*width*backCardPart()));
		for(int i=0; i<3; i++) {
			if(focusedX[i]<0) {
				focusedX[i]=0;
			}
		}
	}
	
	public int focusedX(int index) {
		return focusedX[index];
	}
	
	public int getIndexOfFocus(int index) {
		return indexOfFocus[index];
	}
	
	public void setIndexOfFocus(int i, int index) {
		indexOfFocus[i] = index;
	}
	
	public void setX(int x) {
		StartX = x;
	}
	
	public int getX() {
		return StartX;
	}
	
	public void setYs(int y1, int y2, int y3) {
		StartY[0] = y1;
		StartY[1] = y2;
		StartY[2] = y3;
	}
	
	public int getY(int index) {
		return StartY[index];
	}
	
	public static float backCardPart() {
		return Hand.backCardPart();
	}
	
	public static float backUpParameter() {
		return Hand.backUpParameter();
	}
	
	public static int SingleRowCapacity() {
		return SingleRowCapacity;
	}
}
