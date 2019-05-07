import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ShowBigCard {

	private BufferedImage image;
	private boolean ShowingCard;
	private int DrawX;
	private int DrawY;
	private float CardRatio;
	private int width;
	private int height;
	
	ShowBigCard(){
		ShowingCard = false;
		CardRatio = Card.CardRatio();
	}
	
	public int getX() {
		return DrawX;
	}
	
	public int getY() {
		return DrawY;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(int index) {
		image = resizeCard(GameInfo.CardsLibrary[index].returnOriginalFrontImage(), Values.DEFAULT_X, Values.DEFAULT_Y);
		width = image.getWidth();
		height = image.getHeight();
	}
	
	public void setShowing(boolean is) {
		ShowingCard = is;
	}
	
	public boolean isShowing() {
		return ShowingCard;
	}
	
	public void setCoordinates(int x, int y) {
		DrawX = x;
		DrawY = y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private BufferedImage resizeCard(BufferedImage img, int newW, int newH) {
		float ratio = (float)(930.0/1080.0);
		newH = (int)(newH*ratio);
		newW = (int)Math.round(newH/CardRatio);
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_4BYTE_ABGR);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}
}
