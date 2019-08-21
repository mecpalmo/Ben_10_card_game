import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LifeMeter {

	private static BufferedImage FrameImage;
	private static BufferedImage CurrentFrameImage;
	
	private float FrameRatio;
	private int CurrentLifeIndex;
	private static final int numOfPositions = 11;
	
	private Position[] positions = new Position[numOfPositions];
	
	LifeMeter(){
	    try {
			FrameImage = ImageIO.read(new File("Images/lifeframe.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    FrameRatio = (float)FrameImage.getHeight()/(float)FrameImage.getWidth();
	    createPositions();
		CurrentLifeIndex = 10;
	}
	
	private void createPositions() {
		for(int i=0;i < numOfPositions; i++) {
			positions[i] = new Position();
		}
	}
	
	public int getCurrentLifeIndex() {
		return CurrentLifeIndex;
	}
	
	public void setCurrentLifeIndex(int i) {
		CurrentLifeIndex = i;
	}
	
	public int getCurrentX() {
		return positions[CurrentLifeIndex].getX();
	}
	
	public int getCurrentY() {
		return positions[CurrentLifeIndex].getY();
	}
	
	public int getXbyIndex(int index) {
		return positions[index].getX();
	}
	
	public int getYbyIndex(int index) {
		return positions[index].getY();
	}
	
	public int getNumOfPositions() {
		return numOfPositions;
	}
	
	public void setPosition(int index, int x, int y) {
		positions[index].setX(x);
		positions[index].setY(y);
	}
	
	public void setCurrentImage() {
		CurrentFrameImage = resizeFrame(FrameImage,Values.DEFAULT_X,Values.DEFAULT_Y);
	}
	
	public BufferedImage returnFrameImage() {
		return CurrentFrameImage;
	}
	
	private BufferedImage resizeFrame(BufferedImage img, int newW, int newH) {
		float ratio = (float) (43.0/1080.0);
		newH = (int)(newH * ratio);
		newW = (int)Math.round((newH)/FrameRatio);
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_4BYTE_ABGR);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}
	
	private class Position{
		
		private int positionX;
		private int positionY;
		
		Position(){
			positionX = 0;
			positionY = 0;
		}
		
		public void setX(int x) {
			positionX = x;
		}
		
		public void setY(int y) {
			positionY = y;
		}
		
		public int getX() {
			return positionX;
		}
		
		public int getY() {
			return positionY;
		}
	}
}
