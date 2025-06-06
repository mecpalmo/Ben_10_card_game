package pl.mecpalmo.util;

import pl.mecpalmo.config.GameInfo;
import pl.mecpalmo.config.Values;

import java.awt.image.BufferedImage;

public class Hover {

	private int DrawX;
	private int DrawY;
	
	private int differenceX;
	private int differenceY;
	
	private boolean Hovering;
	private int CardID;
	private int OriginID;
	private boolean wasOpen; //do przemieszczania z p�l bitwy, czy przemieszczona karta by�a otwarta
	private boolean wasHit; // do przemierszczenia z p�l bitwy, czy przemieszczona karta by�a uszkodzona
	private BufferedImage HoverImage;
	
	public Hover(){
		
		DrawX=0;
		DrawY=0;
		differenceX = 0;
		differenceY = 0;
		Hovering = false;
		OriginID = Values.HAND_ID;
		wasOpen = false;
		wasHit = false;
		
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
	
	public void setWasHit(boolean is) {
		wasHit = is;
	}
	
	public boolean wasHit() {
		return wasHit;
	}
	
	public void setImage() {
		if(wasHit==true) {
			HoverImage = GameInfo.rotateImage(GameInfo.CardsLibrary[CardID].returnFrontImage(),90);
		}else {
			HoverImage = GameInfo.CardsLibrary[CardID].returnFrontImage();
		}
	}
	
	public BufferedImage getImage() {
		return HoverImage;
	}
	
}
