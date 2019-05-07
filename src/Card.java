import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Card {
	
	private String id; // przyda siê tylko do rozpoznania efektu i triku do funkcji "U¯YJ"
	private BufferedImage FrontImage;
	private static BufferedImage BackImage;
	
	private BufferedImage CurrentFrontImage;
	private static BufferedImage CurrentBackImage;
	
	private static float CardRatio;
	
	Card(){
	}
	
	Card(int _id) {
		String path;
		if(0<=_id && _id<51) {
			path = "Images/Cards/C"+(_id+1)+".png";
			id = "C"+(_id+1);
		}else if(51<=_id && _id<69) {
			path = "Images/Cards/E"+(_id-50)+".png";
			id = "E"+(_id-50);
		}else {
			path = "Images/Cards/T"+(_id-68)+".png";
			id = "T"+(_id-68);
		}
		
		try {
			FrontImage = ImageIO.read(new File(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    try {
			BackImage = ImageIO.read(new File("Images/Cards/Back.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    CardRatio = (float)FrontImage.getHeight()/(float)FrontImage.getWidth();
	    setCurrentImages();
	}
	
	public void setCurrentImages() {
		CurrentFrontImage = resizeCard(FrontImage,Values.DEFAULT_X,Values.DEFAULT_Y);
		CurrentBackImage = resizeCard(BackImage,Values.DEFAULT_X,Values.DEFAULT_Y);
	}

	private BufferedImage resizeCard(BufferedImage img, int newW, int newH) {
		float ratio = (float) (198.0/1080.0);
		newH = (int)(newH * ratio);
		newW = (int)Math.round(newH/CardRatio);
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_4BYTE_ABGR);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}
	
	public String returnID() {
		return id;
	}
	
	public BufferedImage returnFrontImage() {
		return CurrentFrontImage;
	}
	
	public BufferedImage returnBackImage() {
		return CurrentBackImage;
	}
	
	public static float CardRatio() {
		return CardRatio;
	}
	
	public BufferedImage returnOriginalFrontImage() {
		return FrontImage;
	}
	
	public BufferedImage returnOriginalBackImage() {
		return BackImage;
	}
}
