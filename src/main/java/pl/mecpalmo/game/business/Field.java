package pl.mecpalmo.game.business;

import pl.mecpalmo.config.GameInfo;
import pl.mecpalmo.config.Values;

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
	
	public Field(int angle){
		Empty = true;
		RotateValue = angle;
		Draw_X = 0;
		Draw_Y = 0;
	}
	
	public Field(int angle, int x, int y){
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
			FieldImage = GameInfo.rotateImage(GameInfo.CardsLibrary[CardID].returnBackImage(),RotateValue);
		}else {
			FieldImage = GameInfo.CardsLibrary[CardID].returnBackImage();
		}
	}
	
	public void openCard() {
		Open = true;
		if(RotateValue!=0) {
			FieldImage = GameInfo.rotateImage(GameInfo.CardsLibrary[CardID].returnFrontImage(),RotateValue);
		}else {
			FieldImage = GameInfo.CardsLibrary[CardID].returnFrontImage();
		}
		
	}
	
	public void closeCard() {
		Open = false;
		if(RotateValue!=0) {
			FieldImage = GameInfo.rotateImage(GameInfo.CardsLibrary[CardID].returnBackImage(),RotateValue);
		}else {
			FieldImage = GameInfo.CardsLibrary[CardID].returnBackImage();
		}
	}
	
	public void getImage() {
		if(!Empty) {
			if(Open) {
				if(RotateValue!=0) {
					FieldImage = GameInfo.rotateImage(GameInfo.CardsLibrary[CardID].returnFrontImage(),RotateValue);
				}else {
					FieldImage = GameInfo.CardsLibrary[CardID].returnFrontImage();
				}
			}else {
				if(RotateValue!=0) {
					FieldImage = GameInfo.rotateImage(GameInfo.CardsLibrary[CardID].returnBackImage(),RotateValue);
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
	
}
