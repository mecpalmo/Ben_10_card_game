import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Field {
	
	private int CardID;
	private boolean Empty;
	private boolean Open;
	private int RotateValue;
	private int Draw_X;
	private int Draw_Y;
	private BufferedImage FieldImage;
	
	Field(){
		Empty = true;
		RotateValue = 0;
		Draw_X = 0;
		Draw_Y = 0;
	}
	
	Field(int angle){
		Empty = true;
		RotateValue = angle;
		Draw_X = 0;
		Draw_Y = 0;
	}
	
	Field(int angle,int x,int y){
		Empty = true;
		RotateValue = angle;
		Draw_X = x;
		Draw_Y = y;
	}
	
	public void addCard(int _id) {
		Empty = false;
		CardID = _id;
		Open = false;
		if(RotateValue!=0) {
			FieldImage = rotateImage(GameInfo.CardsLibrary[CardID].returnBackImage(),RotateValue);
		}else {
			FieldImage = GameInfo.CardsLibrary[CardID].returnBackImage();
		}
	}
	
	public void openCard() {
		Open = true;
		if(RotateValue!=0) {
			FieldImage = rotateImage(GameInfo.CardsLibrary[CardID].returnFrontImage(),RotateValue);
		}else {
			FieldImage = GameInfo.CardsLibrary[CardID].returnFrontImage();
		}
		
	}
	
	public void closeCard() {
		Open = false;
		if(RotateValue!=0) {
			FieldImage = rotateImage(GameInfo.CardsLibrary[CardID].returnBackImage(),RotateValue);
		}else {
			FieldImage = GameInfo.CardsLibrary[CardID].returnBackImage();
		}
	}
	
	public void getImage() {
		if(!Empty) {
			if(Open) {
				if(RotateValue!=0) {
					FieldImage = rotateImage(GameInfo.CardsLibrary[CardID].returnFrontImage(),RotateValue);
				}else {
					FieldImage = GameInfo.CardsLibrary[CardID].returnFrontImage();
				}
			}else {
				if(RotateValue!=0) {
					FieldImage = rotateImage(GameInfo.CardsLibrary[CardID].returnBackImage(),RotateValue);
				}else {
					FieldImage = GameInfo.CardsLibrary[CardID].returnBackImage();
				}
			}
		}
	}
	
	public void removeCard() {
		Empty = true;
	}
	
	public int returnRotateValue() {
		return RotateValue;
	}
	
	public void setPositions(int x, int y) {
		Draw_X = x;
		Draw_Y = y;
	}
	
	public boolean isEmpty() {
		return Empty;
	}
	
	public boolean isOpen() {
		return Open;
	}
	
	public BufferedImage returnFieldImage() {
		return FieldImage;
	}
	
	public int returnX() {
		return Draw_X;
	}
	
	public int returnY() {
		return Draw_Y;
	}
	
	public int getCardID() {
		return CardID;
	}
	
	public int getFieldContent() {
		if(Empty) {
			return Values.EMPTY_FIELD_INDEX;
		}else {
			if(Open) {
				return CardID;
			}else {
				return Values.BACK_CARD_INDEX;
			}
		}
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
