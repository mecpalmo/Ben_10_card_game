import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class GamePanel extends JPanel{

	private BufferedImage Board, CurrentBoard, Background, CurrentBackground;
	private Timer timer, timer2;
	private Graphics2D g2d;
	private int TransformationTime = 0;
	private Field[] BoardFields = new Field[Values.FIELDS_AMOUNT];
	private Stack MyDeck, MyRejected, OpponentsDeck, OpponentsRejected;
	private Hand MyHand;
	private LifeMeter MyLife, OpponentsLife;
	private JLabel MyStackInfo, MyTransformationInfo, OpponentsStackInfo, OpponentsTransformationInfo, MyTransformationValue, OpponentsTransformationValue;
	private JButton AddingTransTime, DecreasingTransTime, ResizeButton, ResetConnectionButton, CloseConnectionButton;
	private Hover HoverObject;
	private JFrame upperFrame;
	private ShowBigCard ShowBigCardObject;
	private RejectedViewer RejectedViewerObject;
	private Server myServer;
	private Client myClient;
	private JScrollPane myScrollPane;
	private JTextArea myTextArea;
	
	
	GamePanel(JFrame GameFrame){
		
		upperFrame = GameFrame;
		setSize(Values.DEFAULT_X,Values.DEFAULT_Y);
		setLayout(null);
		loadImage();
		loadBackground();
		CurrentBoard = resizeImage(Board,Values.DEFAULT_X,Values.DEFAULT_Y);
		CurrentBackground = resizeImage(Background,Values.DEFAULT_X,Values.DEFAULT_Y);
		setBackground(Color.BLACK);
		setFields();
		setStacks();
		setLabels();
		setButtons();
		setLifeMeters();
		setHands();
		setHoverObject();
		setBigCardObject();
		setRejectedViewer();
		
		Values.resetOpponentsData();
		Values.resetMyData();
		
        if(Values.HOST) {
        	myServer = new Server();
        }else {
        	myClient = new Client(Values.IP_ADRESS);
        }
        Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if(Values.HOST) {
					myServer.startRunning();
				}else {
					myClient.StartRunning();
				}
			}
		});
        //t.start();
        setConnectionElements();
        //prepareMyCards();
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), 0, Values.PERIOD);
        
        //timer2 = new Timer();
        //timer2.scheduleAtFixedRate(new ScheduleTask2(), 0, Values.SEVER_PERIOD);
        HandlerClass handler = new HandlerClass();
        this.addMouseListener(handler);
        this.addMouseMotionListener(handler);
        
        /// TESTS ///
        
        MyDeck.addCard(0);
        MyRejected.addCard(0);
        OpponentsDeck.addCard(0);
        OpponentsRejected.addCard(0);
        
        /// TESTS ///
        
	}
	
	private void prepareMyCards() {
		
		GameInfo.shuffleMyCards();
		
		for(int i=0;i<Values.MAX_DECK_CAPACITY-Values.HAND_START_VALUE;i++) {
			MyDeck.addCard(GameInfo.MyCards[i]);
		}
		
		for(int i=Values.MAX_DECK_CAPACITY-Values.HAND_START_VALUE;i<Values.MAX_DECK_CAPACITY;i++) {
			MyHand.addCard(GameInfo.MyCards[i]);
		}
		
	}
	
	private void loadImage() {
		try {
			Board = ImageIO.read(new File("Images/Board.png"));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadBackground() {
		try {
			Background = ImageIO.read(new File("Images/Background.png"));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        g2d.drawImage(CurrentBoard,0,0,this);
        drawCardsOnFields(g2d);
        drawStacks(g2d);
        drawLifeMeters(g2d);
        drawMyHand(g2d);
        drawOpponentsHand(g2d);
        drawAllRejected(g2d);
        drawBigCard(g2d);
        drawHover(g2d);
	}
	
	private void drawCardsOnFields(Graphics2D g) {
		for(int i=0; i<Values.FIELDS_AMOUNT;i++) {
			if(BoardFields[i].isEmpty()==false) {
				g.drawImage(BoardFields[i].returnFieldImage(), BoardFields[i].returnX(), BoardFields[i].returnY(), this);
			}
		}
	}
	
	private void drawStacks(Graphics2D g) {
		if(MyDeck.size()>0) {
			g.drawImage(MyDeck.returnStackImage(),MyDeck.returnX(),MyDeck.returnY(),this);
		}
		if(MyRejected.size()>0) {
			g.drawImage(MyRejected.returnStackImage(),MyRejected.returnX(),MyRejected.returnY(),this);
		}
		if(Values.OpponentsDeckSize>0) {
			g.drawImage(OpponentsDeck.returnStackImage(), OpponentsDeck.returnX(), OpponentsDeck.returnY(), this);
		}
		if(OpponentsRejected.size()>0) {
			g.drawImage(OpponentsRejected.returnStackImage(), OpponentsRejected.returnX(), OpponentsRejected.returnY(), this);
		}
	}
	
	private void drawLifeMeters(Graphics2D g) {
		g.drawImage(MyLife.returnFrameImage(), MyLife.getCurrentX(), MyLife.getCurrentY(), this);
		g.drawImage(OpponentsLife.returnFrameImage(), OpponentsLife.getCurrentX(), OpponentsLife.getCurrentY(), this);
	}
	
	private void drawMyHand(Graphics2D g) {
		if(MyHand.size()!=0) {
			int focused = MyHand.getIndexOfFocus();
			int singleCardWidth = MyHand.singleCardWidth();
			float backCardPart = Hand.backCardPart();
			int y;
			
			if(MyHand.isShowing()) {
				y = MyHand.ShowY();
			}else {
				y = MyHand.HideY();
			}
			
			int currentX = MyHand.startX();
			for(int i=0;i<focused;i++) {
				g.drawImage(GameInfo.CardsLibrary[MyHand.getCard(i)].returnFrontImage(),currentX,y,this);
				currentX += singleCardWidth*backCardPart;
			}
			
			currentX += (MyHand.size()-1-focused)*(singleCardWidth*backCardPart);
			
			for(int i=MyHand.size()-1;i>focused;i--) {
				g.drawImage(GameInfo.CardsLibrary[MyHand.getCard(i)].returnFrontImage(),currentX,y,this);
				currentX -= singleCardWidth*backCardPart;
			}
			
			g.drawImage(GameInfo.CardsLibrary[MyHand.getCard(focused)].returnFrontImage(),MyHand.focusedX(),y,this);
				
		}
	}
	
	private void drawOpponentsHand(Graphics2D g) {
		int OpponentsHandSize = Values.OpponentsHandSize;
		float BackCardPart = Hand.backCardPart();
		int singleCardWidth = MyHand.singleCardWidth();
		int width = (int) (OpponentsHandSize*BackCardPart*singleCardWidth);
		int currentX = (CurrentBoard.getWidth()-width)/2;
		int OneCardStep = (int) (singleCardWidth*BackCardPart);
		for(int i=0;i<OpponentsHandSize;i++) {
			g.drawImage(Hand.OpponentsHandCard(),currentX,MyHand.OpponentsY(),this);
			currentX += OneCardStep;
		}
	}
	
	private void drawHover(Graphics2D g) {
		if(HoverObject.isHovering()) {
			g.drawImage(GameInfo.CardsLibrary[HoverObject.getCardID()].returnFrontImage(),HoverObject.getX(),HoverObject.getY(), this);
		}
	}
	
	private void drawBigCard(Graphics2D g) {
		if(ShowBigCardObject.isShowing()) {
			g.drawImage(CurrentBackground, 0, 0, this);
			g.drawImage(ShowBigCardObject.getImage(), ShowBigCardObject.getX(), ShowBigCardObject.getY(), this);
		}
	}
	
	private void drawAllRejected(Graphics2D g) {
		if(RejectedViewerObject.isShowing()) {
			g.drawImage(CurrentBackground, 0, 0, this);
			//a tu ci¹g dalszy
			int amount = MyRejected.size();
			if(amount>0 && amount<=RejectedViewer.SingleRowCapacity()) {
				drawFirstRow(amount, g);
			}else if(amount>RejectedViewer.SingleRowCapacity() && amount<=RejectedViewer.SingleRowCapacity()*2) {
				drawFirstRow(amount, g);
				drawSecondRow(amount, g);
			}else if(amount>RejectedViewer.SingleRowCapacity()*2) {
				drawFirstRow(amount, g);
				drawSecondRow(amount, g);
				drawThirdRow(amount, g);
			}
		}
	}
	
	private void drawFirstRow(int amount, Graphics2D g) {
		int focused = RejectedViewerObject.getIndexOfFocus(0);
		int currentX = RejectedViewerObject.getX();
		int singleCardWidth = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
		float backCardPart = RejectedViewer.backCardPart();
		int y = RejectedViewerObject.getY(0);
		if(amount>RejectedViewer.SingleRowCapacity()) {
			amount = RejectedViewer.SingleRowCapacity();
		}
		
		for(int i=0;i<focused;i++) {
			g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(i)].returnFrontImage(),currentX,y,this);
			currentX += singleCardWidth*backCardPart;
		}
		currentX += (amount-1-focused)*(singleCardWidth*backCardPart);
		for(int i=amount-1;i>focused;i--) {
			g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(i)].returnFrontImage(),currentX,y,this);
			currentX -= singleCardWidth*backCardPart;
		}
		g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(focused)].returnFrontImage(),RejectedViewerObject.focusedX(0),y,this);
	}
	
	private void drawSecondRow(int amount, Graphics2D g) {
		int focused = RejectedViewerObject.getIndexOfFocus(1);
		int currentX = RejectedViewerObject.getX();
		int singleCardWidth = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
		float backCardPart = RejectedViewer.backCardPart();
		int y = RejectedViewerObject.getY(1);
		if(amount>RejectedViewer.SingleRowCapacity()*2) {
			amount = RejectedViewer.SingleRowCapacity()*2;
		}
		
		for(int i=RejectedViewer.SingleRowCapacity();i<focused;i++) {
			g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(i)].returnFrontImage(),currentX,y,this);
			currentX += singleCardWidth*backCardPart;
		}
		currentX += (amount-1-focused)*(singleCardWidth*backCardPart);
		for(int i=amount-1;i>focused;i--) {
			g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(i)].returnFrontImage(),currentX,y,this);
			currentX -= singleCardWidth*backCardPart;
		}
		g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(focused)].returnFrontImage(),RejectedViewerObject.focusedX(1),y,this);
	}
	
	private void drawThirdRow(int amount, Graphics2D g) {
		int focused = RejectedViewerObject.getIndexOfFocus(2);
		int currentX = RejectedViewerObject.getX();
		int singleCardWidth = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
		float backCardPart = RejectedViewer.backCardPart();
		int y = RejectedViewerObject.getY(2);
		
		for(int i=RejectedViewer.SingleRowCapacity()*2;i<focused;i++) {
			g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(i)].returnFrontImage(),currentX,y,this);
			currentX += singleCardWidth*backCardPart;
		}
		currentX += (amount-1-focused)*(singleCardWidth*backCardPart);
		for(int i=amount-1;i>focused;i--) {
			g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(i)].returnFrontImage(),currentX,y,this);
			currentX -= singleCardWidth*backCardPart;
		}
		g.drawImage(GameInfo.CardsLibrary[MyRejected.getCard(focused)].returnFrontImage(),RejectedViewerObject.focusedX(2),y,this);
	}
	
	private BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
		newW = (int)Math.round( (float)(newH) / ((float)img.getHeight()/(float)img.getWidth()) );
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_4BYTE_ABGR);
	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();
	    return dimg;
	}
	
	private class ScheduleTask extends TimerTask {
        @Override
        public void run() {
        	repaint();
        	updateLabels();
        }
    }
	
	private class ScheduleTask2 extends TimerTask{
		@Override
		public void run() {
			sendValues();
        	receiveValues();
			if(Values.HOST) {
				myServer.sendMessage();
			}else {
				myClient.sendMessage();
			}
			updateScrollPane();
		}
	}
	
	private void sendValues() {
		for(int i=0;i<Values.FIELDS_AMOUNT/2;i++) {
			if(BoardFields[i].isEmpty()) {
				Values.MyFieldsValues[i] = Values.EMPTY_FIELD_INDEX;
			}else {
				if(BoardFields[i].isOpen()) {
					Values.MyFieldsValues[i] = BoardFields[i].getCardID();
				}else {
					Values.MyFieldsValues[i] = Values.BACK_CARD_INDEX;
				}
			}
		}
		Values.MyDeckSize = MyDeck.size();
		if(MyRejected.size()>0) {
			Values.MyTopRejectedValue = MyRejected.lastCardIndex();
		}else {
			Values.MyTopRejectedValue = Values.EMPTY_FIELD_INDEX;
		}
		Values.MyHandSize = MyHand.size();
		Values.MyTransformationTime = TransformationTime;
		Values.MyLifeIndex = MyLife.getCurrentLifeIndex();
	}
	
	private void receiveValues() {
		
		opponentsFieldDecision(Values.OpponentsFieldsValues[0],52);
		opponentsFieldDecision(Values.OpponentsFieldsValues[1],53);		
		opponentsFieldDecision(Values.OpponentsFieldsValues[2],54);
		opponentsFieldDecision(Values.OpponentsFieldsValues[3],55);
		opponentsFieldDecision(Values.OpponentsFieldsValues[4],48);
		opponentsFieldDecision(Values.OpponentsFieldsValues[5],49);
		opponentsFieldDecision(Values.OpponentsFieldsValues[6],50);
		opponentsFieldDecision(Values.OpponentsFieldsValues[7],51);
		opponentsFieldDecision(Values.OpponentsFieldsValues[8],60);
		opponentsFieldDecision(Values.OpponentsFieldsValues[9],61);
		opponentsFieldDecision(Values.OpponentsFieldsValues[10],62);
		opponentsFieldDecision(Values.OpponentsFieldsValues[11],63);
		opponentsFieldDecision(Values.OpponentsFieldsValues[12],56);
		opponentsFieldDecision(Values.OpponentsFieldsValues[13],57);
		opponentsFieldDecision(Values.OpponentsFieldsValues[14],58);
		opponentsFieldDecision(Values.OpponentsFieldsValues[15],59);
		opponentsFieldDecision(Values.OpponentsFieldsValues[16],36);
		opponentsFieldDecision(Values.OpponentsFieldsValues[17],37);
		opponentsFieldDecision(Values.OpponentsFieldsValues[18],38);
		opponentsFieldDecision(Values.OpponentsFieldsValues[19],39);
		opponentsFieldDecision(Values.OpponentsFieldsValues[20],32);
		opponentsFieldDecision(Values.OpponentsFieldsValues[21],33);
		opponentsFieldDecision(Values.OpponentsFieldsValues[22],34);
		opponentsFieldDecision(Values.OpponentsFieldsValues[23],35);
		opponentsFieldDecision(Values.OpponentsFieldsValues[24],44);
		opponentsFieldDecision(Values.OpponentsFieldsValues[25],45);
		opponentsFieldDecision(Values.OpponentsFieldsValues[26],46);
		opponentsFieldDecision(Values.OpponentsFieldsValues[27],47);
		opponentsFieldDecision(Values.OpponentsFieldsValues[28],40);
		opponentsFieldDecision(Values.OpponentsFieldsValues[29],41);
		opponentsFieldDecision(Values.OpponentsFieldsValues[30],42);
		opponentsFieldDecision(Values.OpponentsFieldsValues[31],43);
		
		if(Values.OpponentsTopRejectedValue==Values.EMPTY_FIELD_INDEX) {
			while(OpponentsRejected.size()>0) {
				OpponentsRejected.removeCard();
			}
		}else {
			if(OpponentsRejected.lastCardIndex()!=Values.OpponentsTopRejectedValue) {
				OpponentsRejected.addCard(Values.OpponentsTopRejectedValue);
			}
		}
		
		OpponentsLife.setCurrentLifeIndex(Values.OpponentsLifeIndex);
		
	}
	
	private void opponentsFieldDecision(int value, int index) {
		if(value!=BoardFields[index].getFieldContent()) {	
			if(value == Values.EMPTY_FIELD_INDEX) {
				BoardFields[index].removeCard();
			}else {
				if(value == Values.BACK_CARD_INDEX) {
					BoardFields[index].addCard(0);
				}else {
					BoardFields[index].addCard(value);
					BoardFields[index].openCard();
				}
			}
		}
	}
		
	private void resizeWindow() {
		ResizeButton.setEnabled(false);
		timer.cancel();
		Values.changeScreenSize(); //przed wszystkim, zmienia wartoœci DEFAULT_X i DEFAULT_Y
		
		CurrentBoard = resizeImage(Board,Values.DEFAULT_X,Values.DEFAULT_Y);
		CurrentBackground = resizeImage(Background,Values.DEFAULT_X,Values.DEFAULT_Y);
		GameInfo.resizeAllCards();
		setFieldsPositions();
		setStacksPositions(); //musi byæ po przeskalowaniu kart
		setLabelsPositions();
		setButtonsPositions();
		setLifeMetersPositions();
		setBigCardPosition();
		setRejectedViewerPositions();
		setConnectionElementsPositions();
		updateHands(); //musi byæ po przeskalowaniu CurrentBoard oraz po przeskalowaniu kart
		for(int i=0;i<Values.FIELDS_AMOUNT;i++) {
			BoardFields[i].getImage();
		}
		
		setSize(Values.DEFAULT_X,Values.DEFAULT_Y);
		JFrame temp = new JFrame();
		temp.pack();
		Insets insets = temp.getInsets();
		temp = null;
		upperFrame.setSize(insets.left + Values.DEFAULT_X, insets.top + Values.DEFAULT_Y);
		
		timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), 0, Values.PERIOD);
        ResizeButton.setEnabled(true);
	}
	
	private void setFields() {
		for(int i=0;i<31;i+=2) {
			BoardFields[i] = new Field(0);
		}
		for(int i=1;i<32;i+=2) {
			BoardFields[i] = new Field(90);
		}
		for(int i=32;i<63;i+=2) {
			BoardFields[i] = new Field(180);
		}
		for(int i=33;i<64;i+=2) {
			BoardFields[i] = new Field(270);
		}
		setFieldsPositions();
	}
	
	private void setFieldsPositions() {
		BoardFields[0].setPositions((int)Math.round(Values.DEFAULT_X*(0.133)),(int)Math.round(Values.DEFAULT_Y*(0.532)));
		BoardFields[2].setPositions((int)Math.round(Values.DEFAULT_X*(0.133)),(int)Math.round(Values.DEFAULT_Y*(0.516)));
		BoardFields[4].setPositions((int)Math.round(Values.DEFAULT_X*(0.266)),(int)Math.round(Values.DEFAULT_Y*(0.532)));
		BoardFields[6].setPositions((int)Math.round(Values.DEFAULT_X*(0.266)),(int)Math.round(Values.DEFAULT_Y*(0.516)));
		BoardFields[8].setPositions((int)Math.round(Values.DEFAULT_X*(0.407)),(int)Math.round(Values.DEFAULT_Y*(0.532)));
		BoardFields[10].setPositions((int)Math.round(Values.DEFAULT_X*(0.407)),(int)Math.round(Values.DEFAULT_Y*(0.516)));
		BoardFields[12].setPositions((int)Math.round(Values.DEFAULT_X*(0.540)),(int)Math.round(Values.DEFAULT_Y*(0.532)));
		BoardFields[14].setPositions((int)Math.round(Values.DEFAULT_X*(0.540)),(int)Math.round(Values.DEFAULT_Y*(0.516)));
		
		BoardFields[16].setPositions((int)Math.round(Values.DEFAULT_X*(0.133)),(int)Math.round(Values.DEFAULT_Y*(0.775)));
		BoardFields[18].setPositions((int)Math.round(Values.DEFAULT_X*(0.133)),(int)Math.round(Values.DEFAULT_Y*(0.758)));
		BoardFields[20].setPositions((int)Math.round(Values.DEFAULT_X*(0.266)),(int)Math.round(Values.DEFAULT_Y*(0.775)));
		BoardFields[22].setPositions((int)Math.round(Values.DEFAULT_X*(0.266)),(int)Math.round(Values.DEFAULT_Y*(0.758)));
		BoardFields[24].setPositions((int)Math.round(Values.DEFAULT_X*(0.407)),(int)Math.round(Values.DEFAULT_Y*(0.775)));
		BoardFields[26].setPositions((int)Math.round(Values.DEFAULT_X*(0.407)),(int)Math.round(Values.DEFAULT_Y*(0.758)));
		BoardFields[28].setPositions((int)Math.round(Values.DEFAULT_X*(0.540)),(int)Math.round(Values.DEFAULT_Y*(0.775)));
		BoardFields[30].setPositions((int)Math.round(Values.DEFAULT_X*(0.540)),(int)Math.round(Values.DEFAULT_Y*(0.758)));
		
		
		
		BoardFields[1].setPositions((int)Math.round(Values.DEFAULT_X*(0.116)),(int)Math.round(Values.DEFAULT_Y*(0.556)));
		BoardFields[3].setPositions((int)Math.round(Values.DEFAULT_X*(0.116)),(int)Math.round(Values.DEFAULT_Y*(0.524)));
		BoardFields[5].setPositions((int)Math.round(Values.DEFAULT_X*(0.250)),(int)Math.round(Values.DEFAULT_Y*(0.556)));
		BoardFields[7].setPositions((int)Math.round(Values.DEFAULT_X*(0.250)),(int)Math.round(Values.DEFAULT_Y*(0.524)));
		BoardFields[9].setPositions((int)Math.round(Values.DEFAULT_X*(0.389)),(int)Math.round(Values.DEFAULT_Y*(0.556)));
		BoardFields[11].setPositions((int)Math.round(Values.DEFAULT_X*(0.389)),(int)Math.round(Values.DEFAULT_Y*(0.524)));
		BoardFields[13].setPositions((int)Math.round(Values.DEFAULT_X*(0.522)),(int)Math.round(Values.DEFAULT_Y*(0.556)));
		BoardFields[15].setPositions((int)Math.round(Values.DEFAULT_X*(0.522)),(int)Math.round(Values.DEFAULT_Y*(0.524)));
		
		BoardFields[17].setPositions((int)Math.round(Values.DEFAULT_X*(0.116)),(int)Math.round(Values.DEFAULT_Y*(0.801)));
		BoardFields[19].setPositions((int)Math.round(Values.DEFAULT_X*(0.116)),(int)Math.round(Values.DEFAULT_Y*(0.769)));
		BoardFields[21].setPositions((int)Math.round(Values.DEFAULT_X*(0.250)),(int)Math.round(Values.DEFAULT_Y*(0.801)));
		BoardFields[23].setPositions((int)Math.round(Values.DEFAULT_X*(0.250)),(int)Math.round(Values.DEFAULT_Y*(0.769)));
		BoardFields[25].setPositions((int)Math.round(Values.DEFAULT_X*(0.389)),(int)Math.round(Values.DEFAULT_Y*(0.801)));
		BoardFields[27].setPositions((int)Math.round(Values.DEFAULT_X*(0.389)),(int)Math.round(Values.DEFAULT_Y*(0.769)));
		BoardFields[29].setPositions((int)Math.round(Values.DEFAULT_X*(0.522)),(int)Math.round(Values.DEFAULT_Y*(0.801)));
		BoardFields[31].setPositions((int)Math.round(Values.DEFAULT_X*(0.522)),(int)Math.round(Values.DEFAULT_Y*(0.769)));
		
		
		
		BoardFields[32].setPositions((int)Math.round(Values.DEFAULT_X*(0.133)),(int)Math.round(Values.DEFAULT_Y*(0.040)));
		BoardFields[34].setPositions((int)Math.round(Values.DEFAULT_X*(0.133)),(int)Math.round(Values.DEFAULT_Y*(0.056)));
		BoardFields[36].setPositions((int)Math.round(Values.DEFAULT_X*(0.266)),(int)Math.round(Values.DEFAULT_Y*(0.040)));
		BoardFields[38].setPositions((int)Math.round(Values.DEFAULT_X*(0.266)),(int)Math.round(Values.DEFAULT_Y*(0.056)));
		BoardFields[40].setPositions((int)Math.round(Values.DEFAULT_X*(0.407)),(int)Math.round(Values.DEFAULT_Y*(0.040)));
		BoardFields[42].setPositions((int)Math.round(Values.DEFAULT_X*(0.407)),(int)Math.round(Values.DEFAULT_Y*(0.056)));
		BoardFields[44].setPositions((int)Math.round(Values.DEFAULT_X*(0.540)),(int)Math.round(Values.DEFAULT_Y*(0.040)));
		BoardFields[46].setPositions((int)Math.round(Values.DEFAULT_X*(0.540)),(int)Math.round(Values.DEFAULT_Y*(0.056)));
		
		BoardFields[48].setPositions((int)Math.round(Values.DEFAULT_X*(0.133)),(int)Math.round(Values.DEFAULT_Y*(0.284)));
		BoardFields[50].setPositions((int)Math.round(Values.DEFAULT_X*(0.133)),(int)Math.round(Values.DEFAULT_Y*(0.301)));
		BoardFields[52].setPositions((int)Math.round(Values.DEFAULT_X*(0.266)),(int)Math.round(Values.DEFAULT_Y*(0.284)));
		BoardFields[54].setPositions((int)Math.round(Values.DEFAULT_X*(0.266)),(int)Math.round(Values.DEFAULT_Y*(0.301)));
		BoardFields[56].setPositions((int)Math.round(Values.DEFAULT_X*(0.407)),(int)Math.round(Values.DEFAULT_Y*(0.284)));
		BoardFields[58].setPositions((int)Math.round(Values.DEFAULT_X*(0.407)),(int)Math.round(Values.DEFAULT_Y*(0.301)));
		BoardFields[60].setPositions((int)Math.round(Values.DEFAULT_X*(0.540)),(int)Math.round(Values.DEFAULT_Y*(0.284)));
		BoardFields[62].setPositions((int)Math.round(Values.DEFAULT_X*(0.540)),(int)Math.round(Values.DEFAULT_Y*(0.301)));
		
		
		
		BoardFields[33].setPositions((int)Math.round(Values.DEFAULT_X*(0.116)),(int)Math.round(Values.DEFAULT_Y*(0.066)));
		BoardFields[35].setPositions((int)Math.round(Values.DEFAULT_X*(0.116)),(int)Math.round(Values.DEFAULT_Y*(0.098)));
		BoardFields[37].setPositions((int)Math.round(Values.DEFAULT_X*(0.250)),(int)Math.round(Values.DEFAULT_Y*(0.066)));
		BoardFields[39].setPositions((int)Math.round(Values.DEFAULT_X*(0.250)),(int)Math.round(Values.DEFAULT_Y*(0.098)));
		BoardFields[41].setPositions((int)Math.round(Values.DEFAULT_X*(0.389)),(int)Math.round(Values.DEFAULT_Y*(0.066)));
		BoardFields[43].setPositions((int)Math.round(Values.DEFAULT_X*(0.389)),(int)Math.round(Values.DEFAULT_Y*(0.098)));
		BoardFields[45].setPositions((int)Math.round(Values.DEFAULT_X*(0.522)),(int)Math.round(Values.DEFAULT_Y*(0.066)));
		BoardFields[47].setPositions((int)Math.round(Values.DEFAULT_X*(0.522)),(int)Math.round(Values.DEFAULT_Y*(0.098)));
		
		BoardFields[49].setPositions((int)Math.round(Values.DEFAULT_X*(0.116)),(int)Math.round(Values.DEFAULT_Y*(0.311)));
		BoardFields[51].setPositions((int)Math.round(Values.DEFAULT_X*(0.116)),(int)Math.round(Values.DEFAULT_Y*(0.343)));
		BoardFields[53].setPositions((int)Math.round(Values.DEFAULT_X*(0.250)),(int)Math.round(Values.DEFAULT_Y*(0.311)));
		BoardFields[55].setPositions((int)Math.round(Values.DEFAULT_X*(0.250)),(int)Math.round(Values.DEFAULT_Y*(0.343)));
		BoardFields[57].setPositions((int)Math.round(Values.DEFAULT_X*(0.389)),(int)Math.round(Values.DEFAULT_Y*(0.311)));
		BoardFields[59].setPositions((int)Math.round(Values.DEFAULT_X*(0.389)),(int)Math.round(Values.DEFAULT_Y*(0.343)));
		BoardFields[61].setPositions((int)Math.round(Values.DEFAULT_X*(0.522)),(int)Math.round(Values.DEFAULT_Y*(0.311)));
		BoardFields[63].setPositions((int)Math.round(Values.DEFAULT_X*(0.522)),(int)Math.round(Values.DEFAULT_Y*(0.343)));
	}

	private void setStacks() {
		MyDeck = new Stack(0,false);
		MyRejected = new Stack(0,true);
		OpponentsDeck = new Stack(180,false);
		OpponentsRejected = new Stack(180,true);
		setStacksPositions();
	}
	
	private void setStacksPositions() {
		MyDeck.setPositions((int)Math.round(Values.DEFAULT_X*(0.79)),(int)Math.round(Values.DEFAULT_Y*(0.77)));
		MyDeck.setImage(GameInfo.CardsLibrary[0].returnBackImage());
		MyRejected.setPositions((int)Math.round(Values.DEFAULT_X*(0.895)),(int)Math.round(Values.DEFAULT_Y*(0.77)));
		MyRejected.updateFrontImage();
		OpponentsDeck.setPositions((int)Math.round(Values.DEFAULT_X*(0.79)),(int)Math.round(Values.DEFAULT_Y*(0.05)));
		OpponentsDeck.setImage(GameInfo.CardsLibrary[0].returnBackImage());
		OpponentsRejected.setPositions((int)Math.round(Values.DEFAULT_X*(0.895)),(int)Math.round(Values.DEFAULT_Y*(0.05)));
		OpponentsRejected.updateFrontImage();
	}
	
	private void setLabels() {
		MyStackInfo = new JLabel("Kart w talii: "+MyDeck.size());
		MyStackInfo.setForeground(Color.WHITE);
		MyTransformationInfo = new JLabel("Czas transformacji");
		MyTransformationInfo.setForeground(Color.GREEN);
		OpponentsStackInfo = new JLabel("Kart w talii: "+Values.OpponentsDeckSize);
		OpponentsStackInfo.setForeground(Color.WHITE);
		OpponentsTransformationInfo = new JLabel("Czas transformacji");
		OpponentsTransformationInfo.setForeground(Color.GREEN);
		MyTransformationValue = new JLabel(Integer.toString(TransformationTime));
		OpponentsTransformationValue = new JLabel(Integer.toString(Values.OpponentsTransformationTime));
		setLabelsPositions();
		this.add(MyStackInfo);
		this.add(MyTransformationInfo);
        this.add(OpponentsStackInfo);
        this.add(OpponentsTransformationInfo);
        //this.add(MyTransformationValue);
        //this.add(OpponentsTransformationValue);
	}
	
	private void updateLabels() {
		MyStackInfo.setText("Kart w talii: "+MyDeck.size());
		OpponentsStackInfo.setText("Kart w talii: "+Values.OpponentsDeckSize);
		MyTransformationValue.setText(Integer.toString(TransformationTime));
		OpponentsTransformationValue.setText(Integer.toString(Values.OpponentsTransformationTime));
		if(TransformationTime<1) {
			MyTransformationValue.setForeground(Color.RED);
		}else {
			MyTransformationValue.setForeground(Color.GREEN);
		}
		
		if(Values.OpponentsTransformationTime<1) {
			OpponentsTransformationValue.setForeground(Color.RED);
		}else {
			OpponentsTransformationValue.setForeground(Color.GREEN);
		}
	}
	
	private void setLabelsPositions() {
		Font LabelsFont = new Font("Arial",Font.BOLD,(int)Math.round(Values.DEFAULT_Y*(0.017)));
		Font bigFont = new Font("Arial",Font.BOLD,(int)Math.round(Values.DEFAULT_Y*(0.030)));
		MyStackInfo.setFont(LabelsFont);
		MyTransformationInfo.setFont(LabelsFont);
		OpponentsStackInfo.setFont(LabelsFont);
		OpponentsTransformationInfo.setFont(LabelsFont);
		MyTransformationValue.setFont(bigFont);
		OpponentsTransformationValue.setFont(bigFont);
		MyStackInfo.setBounds((int)Math.round(Values.DEFAULT_X*(0.84)), (int)Math.round(Values.DEFAULT_Y*(0.64)), (int)Math.round(Values.DEFAULT_X*(0.14)), (int)Math.round(Values.DEFAULT_Y*(0.02)));
		MyTransformationInfo.setBounds((int)Math.round(Values.DEFAULT_X*(0.77)), (int)Math.round(Values.DEFAULT_Y*(0.64)), (int)Math.round(Values.DEFAULT_X*(0.18)), (int)Math.round(Values.DEFAULT_X*(0.02)));
		OpponentsStackInfo.setBounds((int)Math.round(Values.DEFAULT_X*(0.77)), (int)Math.round(Values.DEFAULT_Y*(0.25)), (int)Math.round(Values.DEFAULT_X*(0.14)), (int)Math.round(Values.DEFAULT_Y*(0.02)));
		OpponentsTransformationInfo.setBounds((int)Math.round(Values.DEFAULT_X*(0.77)), (int)Math.round(Values.DEFAULT_Y*(0.29)), (int)Math.round(Values.DEFAULT_X*(0.18)), (int)Math.round(Values.DEFAULT_Y*(0.02)));
		
	}
	
	private void setButtons() {
		Font font = new Font("Arial",Font.BOLD,(int)Math.round(Values.DEFAULT_X*(0.015)));
		AddingTransTime = new JButton("+");
		AddingTransTime.setFont(font);
		AddingTransTime.setEnabled(true);
		AddingTransTime.setBackground(Color.WHITE);
		AddingTransTime.setMargin(null);
		AddingTransTime.setBorder(null);
		AddingTransTime.addActionListener(actList);
		AddingTransTime.setFocusable(false);
		
		DecreasingTransTime = new JButton("-");
		DecreasingTransTime.setFont(font);
		DecreasingTransTime.setEnabled(true);
		DecreasingTransTime.setBackground(Color.WHITE);
		DecreasingTransTime.setMargin(null);
		DecreasingTransTime.setBorder(null);
		DecreasingTransTime.addActionListener(actList);
		DecreasingTransTime.setFocusable(false);
		
		ResizeButton = new JButton("Zmieñ Rozdzielczoœæ");
		ResizeButton.setEnabled(true);
		ResizeButton.setBackground(Color.WHITE);
		ResizeButton.setMargin(null);
		ResizeButton.setFocusable(false);
		ResizeButton.addActionListener(actList);
		setButtonsPositions();
		this.add(AddingTransTime);
        this.add(DecreasingTransTime);
        this.add(ResizeButton);
	}
	
	private void setButtonsPositions() {
		AddingTransTime.setBounds((int)Math.round(Values.DEFAULT_X*(0.955)), (int)Math.round(Values.DEFAULT_Y*(0.621)),(int)Math.round(Values.DEFAULT_X*(0.02)),(int)Math.round(Values.DEFAULT_X*(0.02)));
		DecreasingTransTime.setBounds((int)Math.round(Values.DEFAULT_X*(0.955)), (int)Math.round(Values.DEFAULT_Y*(0.661)),(int)Math.round(Values.DEFAULT_X*(0.02)),(int)Math.round(Values.DEFAULT_X*(0.02)));
		int ResButWidth = (int)Math.round(Values.DEFAULT_X*(0.100));
		int ResButHeight = (int)Math.round(Values.DEFAULT_Y*(0.017));
		ResizeButton.setBounds(Values.DEFAULT_X-ResButWidth, 0, ResButWidth, ResButHeight);
	}
	
	private void setConnectionElements() {
		myScrollPane = new JScrollPane();
		myTextArea = new JTextArea();
		CloseConnectionButton = new JButton("Zamknij Po³¹czenie");
		ResetConnectionButton = new JButton("Zresetuj Po³¹czenie");
		setConnectionElementsPositions();
		CloseConnectionButton.addActionListener(actList);
		ResetConnectionButton.addActionListener(actList);
		myScrollPane.add(myTextArea);
		this.add(myScrollPane);
		this.add(CloseConnectionButton);
		this.add(ResetConnectionButton);
	}
	
	private void updateScrollPane() {
		if(Values.HOST) {
			myTextArea = myServer.getTextArea();
		}else {
			myTextArea = myClient.getTextArea();
		}
	}
	
	private void setConnectionElementsPositions() {
		CloseConnectionButton.setBounds((int)Math.round(Values.DEFAULT_X*(0.87)), (int)Math.round(Values.DEFAULT_Y*(0.51)), (int)Math.round(Values.DEFAULT_X*(0.11)), (int)Math.round(Values.DEFAULT_Y*(0.03)));
		ResetConnectionButton.setBounds((int)Math.round(Values.DEFAULT_X*(0.76)), (int)Math.round(Values.DEFAULT_Y*(0.51)), (int)Math.round(Values.DEFAULT_X*(0.11)), (int)Math.round(Values.DEFAULT_Y*(0.03)));
		myScrollPane.setBounds((int)Math.round(Values.DEFAULT_X*(0.76)), (int)Math.round(Values.DEFAULT_Y*(0.34)), (int)Math.round(Values.DEFAULT_X*(0.22)), (int)Math.round(Values.DEFAULT_Y*(0.17)));
	}
	
	private ActionListener actList = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==AddingTransTime) {
				if(TransformationTime<9) {
					TransformationTime++;
				}
			}
			if(e.getSource()==DecreasingTransTime) {
				if(TransformationTime>0) {
					TransformationTime--;
				}
			}
			if(e.getSource()==ResizeButton) {
				resizeWindow();
			}
			if(e.getSource()==ResetConnectionButton) {
				
			}
			if(e.getSource()==CloseConnectionButton) {
				
			}
		}
	};
	
	private void setLifeMeters() {
		MyLife = new LifeMeter();
		OpponentsLife = new LifeMeter();
		setLifeMetersPositions();
	}
	
	private void setLifeMetersPositions() {
		MyLife.setCurrentImage();
		OpponentsLife.setCurrentImage();
		
		MyLife.setPosition(0, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.9)));
		MyLife.setPosition(1, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.865)));
		MyLife.setPosition(2, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.829)));
		MyLife.setPosition(3, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.793)));
		MyLife.setPosition(4, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.757)));
		MyLife.setPosition(5, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.721)));
		MyLife.setPosition(6, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.686)));
		MyLife.setPosition(7, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.65)));
		MyLife.setPosition(8, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.615)));
		MyLife.setPosition(9, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.579)));
		MyLife.setPosition(10, (int)Math.round(Values.DEFAULT_X*(0.65)), (int)Math.round(Values.DEFAULT_Y*(0.544)));
		
		OpponentsLife.setPosition(0, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.035)));
		OpponentsLife.setPosition(1, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.071)));
		OpponentsLife.setPosition(2, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.107)));
		OpponentsLife.setPosition(3, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.143)));
		OpponentsLife.setPosition(4, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.178)));
		OpponentsLife.setPosition(5, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.214)));
		OpponentsLife.setPosition(6, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.249)));
		OpponentsLife.setPosition(7, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.285)));
		OpponentsLife.setPosition(8, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.321)));
		OpponentsLife.setPosition(9, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.356)));
		OpponentsLife.setPosition(10, (int)Math.round(Values.DEFAULT_X*(0.0075)), (int)Math.round(Values.DEFAULT_Y*(0.391)));
	}

	private void setHands() {
		MyHand = new Hand();
		updateHands();
	}
	
	private void updateHands() {
		MyHand.setCurrentBoardWidth(CurrentBoard.getWidth());
		Hand.setOpponentsHandCard();
		MyHand.updateValues();
		MyHand.setYs(CurrentBoard.getHeight() - GameInfo.CardsLibrary[0].returnFrontImage().getHeight(), (int)Math.round(Values.DEFAULT_Y*(0.94)), -(int)Math.round(Values.DEFAULT_Y*(0.15)));
	}

	private void setHoverObject() {
		HoverObject = new Hover();
	}
	
	private void setBigCardObject() {
		ShowBigCardObject = new ShowBigCard();
		setBigCardPosition();
	}
	
	private void setBigCardPosition() {
		ShowBigCardObject.setCoordinates((int)Math.round(Values.DEFAULT_X*(0.2)), (int)Math.round(Values.DEFAULT_Y*(0.05)));
	}
	
	private void setRejectedViewer() {
		RejectedViewerObject = new RejectedViewer();
		setRejectedViewerPositions();
	}
	
	private void setRejectedViewerPositions() {
		RejectedViewerObject.setX((int)Math.round(Values.DEFAULT_X*(0.15)));
		RejectedViewerObject.setYs((int)Math.round(Values.DEFAULT_Y*(0.15)), (int)Math.round(Values.DEFAULT_Y*(0.38)), (int)Math.round(Values.DEFAULT_Y*(0.61)));
		RejectedViewerObject.updateViewer();
	}
	
	private class HandlerClass implements MouseListener, MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent arg0) {
			
			if(HoverObject.isHovering()) {
				
				dragHoverUpdate(arg0);
				
			}
			
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			
			if(!HoverObject.isHovering() && !ShowBigCardObject.isShowing() && !RejectedViewerObject.isShowing()) {
				checkMyHand(arg0);	
			}
			if(RejectedViewerObject.isShowing()) {
				int amount = MyRejected.size();
				if(amount>0 && amount<=RejectedViewer.SingleRowCapacity()) {
					checkFirstRow(arg0);
				}else if(amount>RejectedViewer.SingleRowCapacity() && amount<=RejectedViewer.SingleRowCapacity()*2) {
					checkFirstRow(arg0);
					checkSecondRow(arg0);
				}else if(amount>RejectedViewer.SingleRowCapacity()*2) {
					checkFirstRow(arg0);
					checkSecondRow(arg0);
					checkThirdRow(arg0);
				}
			}
			
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			
			if(ShowBigCardObject.isShowing() || RejectedViewerObject.isShowing()) {
				if(ShowBigCardObject.isShowing()) {
					checkHideBigCard(arg0);
				}else if(RejectedViewerObject.isShowing()) {
					checkHideRejectedViewer(arg0);
				}
			}else {
				if(!MyHand.isShowing()) {
					checkMyLifeMeter(arg0);
					checkMyDeck(arg0);
					checkMyRejected(arg0);
					checkFields(arg0);
					checkOpponentsRejected(arg0);
					checkOpponentsFields(arg0);
				}else {
					checkFocused(arg0);
				}
				if(HoverObject.isHovering()) {
					cancelHovering(arg0);
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			
			if(!ShowBigCardObject.isShowing() && !RejectedViewerObject.isShowing()) {	
				if(MyHand.isShowing()) {
					pickCardFromHand(arg0);
				}else {
					pickCardFromField(arg0);
				}
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
			if(HoverObject.isHovering()) {
				releaseHoveringCard(arg0);
			}
			
		}
		
		private void checkOpponentsFields(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON3) {
				int x = event.getX();
				int y = event.getY();
				int width = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
				int height = GameInfo.CardsLibrary[0].returnFrontImage().getHeight();
				for(int i=8;i<16;i++) {
					if((!BoardFields[4*i+2].isEmpty()) && x>BoardFields[4*i+2].returnX() && x<(BoardFields[4*i+2].returnX()+width) && y>BoardFields[4*i+2].returnY() && y<(BoardFields[4*i+2].returnY()+height)) {
						runPopUpOnOpponentsField(4*i+2, x, y, event);
					}else if((!BoardFields[4*i+3].isEmpty()) && x>BoardFields[4*i+3].returnX() && x<(BoardFields[4*i+3].returnX()+height) && y>BoardFields[4*i+3].returnY() && y<(BoardFields[4*i+3].returnY()+width)) {
						runPopUpOnOpponentsField(4*i+3, x, y, event);
					}else if((!BoardFields[4*i].isEmpty()) && x>BoardFields[4*i].returnX() && x<(BoardFields[4*i].returnX()+width) && y>BoardFields[4*i].returnY() && y<(BoardFields[4*i].returnY()+height)) {
						runPopUpOnOpponentsField(4*i, x, y, event);
					}else if((!BoardFields[4*i+1].isEmpty()) && x>BoardFields[4*i+1].returnX() && x<(BoardFields[4*i+1].returnX()+height) && y>BoardFields[4*i+1].returnY() && y<(BoardFields[4*i+1].returnY()+width)) {
						runPopUpOnOpponentsField(4*i+1, x, y, event);
					}
				}
			}
		}
		
		private void runPopUpOnOpponentsField(int index, int x, int y, MouseEvent event) {
			if(BoardFields[index].isOpen()) {
				PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, false);
				miniPop.setCardID(BoardFields[index].getCardID());
				miniPop.setOriginID(index);
				miniPop.show(event.getComponent(), x, y);
			}
		}
		
		private void checkOpponentsRejected(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON3 && OpponentsRejected.size()>0) {
				int x = event.getX();
				int y = event.getY();
				if(x>=OpponentsRejected.returnX() && x<=OpponentsRejected.returnX()+OpponentsRejected.returnStackImage().getWidth() && y>=OpponentsRejected.returnY() && y<=OpponentsRejected.returnY()+OpponentsRejected.returnStackImage().getHeight() ) {
					PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, false);
					miniPop.setCardID(OpponentsRejected.lastCardIndex());
					miniPop.setOriginID(0);
					miniPop.show(event.getComponent(), x, y);
				}
			}
		}
		
		private void checkFirstRow(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			int amount = MyRejected.size();
			if(amount>RejectedViewer.SingleRowCapacity()) {
				amount=RejectedViewer.SingleRowCapacity();
			}
			
			int currentX = RejectedViewerObject.getX();
			int backCardWidth = (int) (MyRejected.returnStackImage().getWidth()*RejectedViewer.backCardPart());
			int tinyBackUp = (int) (MyRejected.returnStackImage().getWidth()*RejectedViewer.backUpParameter()); //parametr pomocniczy
			
			for(int i=0;i<RejectedViewerObject.getIndexOfFocus(0);i++) {
				int hupi = 0;
				if(i==RejectedViewerObject.getIndexOfFocus(0)-1) {
					hupi=tinyBackUp;
				}else {
					hupi=0;
				}
				if( (x>=currentX && x<(currentX+backCardWidth+hupi)) && (y>=RejectedViewerObject.getY(0) && y<=RejectedViewerObject.getY(0)+MyRejected.returnStackImage().getHeight()) ) {
					RejectedViewerObject.setIndexOfFocus(0,i);
				}
				currentX += backCardWidth;
			}
			
			currentX = RejectedViewerObject.focusedX(0)+MyRejected.returnStackImage().getWidth();
			
			for(int i=RejectedViewerObject.getIndexOfFocus(0)+1;i<amount;i++) {
				int hupi = 0;
				if(i==RejectedViewerObject.getIndexOfFocus(0)+1) {
					hupi=tinyBackUp;
				}else {
					hupi=0;
				}
				if( (x>=currentX-hupi && x<(currentX+backCardWidth)) && (y>=RejectedViewerObject.getY(0) && y<=RejectedViewerObject.getY(0)+MyRejected.returnStackImage().getHeight()) ) {
					RejectedViewerObject.setIndexOfFocus(0,i);
				}
				currentX += backCardWidth;
			}
			
			RejectedViewerObject.updateViewer();
		}
		
		private void checkSecondRow(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			int amount = MyRejected.size();
			if(amount>RejectedViewer.SingleRowCapacity()*2) {
				amount=RejectedViewer.SingleRowCapacity()*2;
			}
			
			int currentX = RejectedViewerObject.getX();
			int backCardWidth = (int) (MyRejected.returnStackImage().getWidth()*RejectedViewer.backCardPart());
			int tinyBackUp = (int) (MyRejected.returnStackImage().getWidth()*RejectedViewer.backUpParameter()); //parametr pomocniczy
			
			for(int i=RejectedViewer.SingleRowCapacity();i<RejectedViewerObject.getIndexOfFocus(1);i++) {
				int hupi = 0;
				if(i==RejectedViewerObject.getIndexOfFocus(1)-1) {
					hupi=tinyBackUp;
				}else {
					hupi=0;
				}
				if( (x>=currentX && x<(currentX+backCardWidth+hupi)) && (y>=RejectedViewerObject.getY(1) && y<=RejectedViewerObject.getY(1)+MyRejected.returnStackImage().getHeight()) ) {
					RejectedViewerObject.setIndexOfFocus(1,i);
				}
				currentX += backCardWidth;
			}
			
			currentX = RejectedViewerObject.focusedX(1)+MyRejected.returnStackImage().getWidth();
			
			for(int i=RejectedViewerObject.getIndexOfFocus(1)+1;i<amount;i++) {
				int hupi = 0;
				if(i==RejectedViewerObject.getIndexOfFocus(1)+1) {
					hupi=tinyBackUp;
				}else {
					hupi=0;
				}
				if( (x>=currentX-hupi && x<(currentX+backCardWidth)) && (y>=RejectedViewerObject.getY(1) && y<=RejectedViewerObject.getY(1)+MyRejected.returnStackImage().getHeight()) ) {
					RejectedViewerObject.setIndexOfFocus(1,i);
				}
				currentX += backCardWidth;
			}
			
			RejectedViewerObject.updateViewer();
		}
		
		private void checkThirdRow(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			int amount = MyRejected.size();
			
			int currentX = RejectedViewerObject.getX();
			int backCardWidth = (int) (MyRejected.returnStackImage().getWidth()*RejectedViewer.backCardPart());
			int tinyBackUp = (int) (MyRejected.returnStackImage().getWidth()*RejectedViewer.backUpParameter()); //parametr pomocniczy
			
			for(int i=RejectedViewer.SingleRowCapacity()*2;i<RejectedViewerObject.getIndexOfFocus(2);i++) {
				int hupi = 0;
				if(i==RejectedViewerObject.getIndexOfFocus(2)-1) {
					hupi=tinyBackUp;
				}else {
					hupi=0;
				}
				if( (x>=currentX && x<(currentX+backCardWidth+hupi)) && (y>=RejectedViewerObject.getY(2) && y<=RejectedViewerObject.getY(2)+MyRejected.returnStackImage().getHeight()) ) {
					RejectedViewerObject.setIndexOfFocus(2,i);
				}
				currentX += backCardWidth;
			}
			
			currentX = RejectedViewerObject.focusedX(2)+MyRejected.returnStackImage().getWidth();
			
			for(int i=RejectedViewerObject.getIndexOfFocus(2)+1;i<amount;i++) {
				int hupi = 0;
				if(i==RejectedViewerObject.getIndexOfFocus(2)+1) {
					hupi=tinyBackUp;
				}else {
					hupi=0;
				}
				if( (x>=currentX-hupi && x<(currentX+backCardWidth)) && (y>=RejectedViewerObject.getY(2) && y<=RejectedViewerObject.getY(2)+MyRejected.returnStackImage().getHeight()) ) {
					RejectedViewerObject.setIndexOfFocus(2,i);
				}
				currentX += backCardWidth;
			}
			
			RejectedViewerObject.updateViewer();
		}
		
		private void checkHideRejectedViewer(MouseEvent event) {
			int amount = MyRejected.size();
			int x = event.getX();
			int y = event.getY();
			int width = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
			int height = GameInfo.CardsLibrary[0].returnFrontImage().getHeight();
			if(amount>0 && amount<=RejectedViewer.SingleRowCapacity()) {
				if(x>RejectedViewerObject.focusedX(0) && x<RejectedViewerObject.focusedX(0)+width && y>RejectedViewerObject.getY(0) && y<RejectedViewerObject.getY(0)+height) {
					if(event.getButton()==MouseEvent.BUTTON3) {
						PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, true);
						miniPop.setCardID(MyRejected.getCard(RejectedViewerObject.getIndexOfFocus(0)));
						miniPop.setOriginID(RejectedViewerObject.getIndexOfFocus(0));
						miniPop.show(event.getComponent(), x, y);
					}
				}else {
					if(event.getButton()==MouseEvent.BUTTON1) {
						RejectedViewerObject.setShowing(false);
					}
				}
			}else if(amount>RejectedViewer.SingleRowCapacity() && amount<=RejectedViewer.SingleRowCapacity()*2) {
				if(x>RejectedViewerObject.focusedX(0) && x<RejectedViewerObject.focusedX(0)+width && y>RejectedViewerObject.getY(0) && y<RejectedViewerObject.getY(0)+height) {
					if(event.getButton()==MouseEvent.BUTTON3) {
						PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, true);
						miniPop.setCardID(MyRejected.getCard(RejectedViewerObject.getIndexOfFocus(0)));
						miniPop.setOriginID(RejectedViewerObject.getIndexOfFocus(0));
						miniPop.show(event.getComponent(), x, y);
					}
				}else if(x>RejectedViewerObject.focusedX(1) && x<RejectedViewerObject.focusedX(1)+width && y>RejectedViewerObject.getY(1) && y<RejectedViewerObject.getY(1)+height) {
					if(event.getButton()==MouseEvent.BUTTON3) {
						PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, true);
						miniPop.setCardID(MyRejected.getCard(RejectedViewerObject.getIndexOfFocus(1)));
						miniPop.setOriginID(RejectedViewerObject.getIndexOfFocus(1));
						miniPop.show(event.getComponent(), x, y);
					}
				}else {
					if(event.getButton()==MouseEvent.BUTTON1) {
						RejectedViewerObject.setShowing(false);
					}
				}
			}else if(amount>RejectedViewer.SingleRowCapacity()*2) {
				if(x>RejectedViewerObject.focusedX(0) && x<RejectedViewerObject.focusedX(0)+width && y>RejectedViewerObject.getY(0) && y<RejectedViewerObject.getY(0)+height) {
					if(event.getButton()==MouseEvent.BUTTON3) {
						PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, true);
						miniPop.setCardID(MyRejected.getCard(RejectedViewerObject.getIndexOfFocus(0)));
						miniPop.setOriginID(RejectedViewerObject.getIndexOfFocus(0));
						miniPop.show(event.getComponent(), x, y);
					}
				}else if(x>RejectedViewerObject.focusedX(1) && x<RejectedViewerObject.focusedX(1)+width && y>RejectedViewerObject.getY(1) && y<RejectedViewerObject.getY(1)+height) {
					if(event.getButton()==MouseEvent.BUTTON3) {
						PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, true);
						miniPop.setCardID(MyRejected.getCard(RejectedViewerObject.getIndexOfFocus(1)));
						miniPop.setOriginID(RejectedViewerObject.getIndexOfFocus(1));
						miniPop.show(event.getComponent(), x, y);
					}
				}else if(x>RejectedViewerObject.focusedX(2) && x<RejectedViewerObject.focusedX(2)+width && y>RejectedViewerObject.getY(2) && y<RejectedViewerObject.getY(2)+height){
					if(event.getButton()==MouseEvent.BUTTON3) {
						PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, true);
						miniPop.setCardID(MyRejected.getCard(RejectedViewerObject.getIndexOfFocus(2)));
						miniPop.setOriginID(RejectedViewerObject.getIndexOfFocus(2));
						miniPop.show(event.getComponent(), x, y);
					}
				}else {
					if(event.getButton()==MouseEvent.BUTTON1) {
						RejectedViewerObject.setShowing(false);
					}
				}
			}else {
				if(event.getButton()==MouseEvent.BUTTON1) {
					RejectedViewerObject.setShowing(false);
				}
			}
		}
		
		//wy³¹czenie pokazywania karty
		private void checkHideBigCard(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			if( (x>=0 && x<ShowBigCardObject.getX()) || (x>ShowBigCardObject.getX()+ShowBigCardObject.getWidth() && x<=Values.DEFAULT_X) || (y>=0 && y<ShowBigCardObject.getY()) || (y>ShowBigCardObject.getY()+ShowBigCardObject.getHeight() && y<=Values.DEFAULT_Y)) {
				ShowBigCardObject.setShowing(false);
			}
		}
		
		//anulowanie przenoszenia karty
		private void cancelHovering(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON3) {
				HoverObject.setHovering(false);
				if(HoverObject.OriginID()==Values.HAND_ID) {
					MyHand.addCard(HoverObject.getCardID());
				}else if(HoverObject.OriginID()==Values.REJECTED_ID){
					MyRejected.addCard(HoverObject.getCardID());
				}else {
					BoardFields[HoverObject.OriginID()].addCard(HoverObject.getCardID());
					if(HoverObject.wasOpen()) {
						BoardFields[HoverObject.OriginID()].openCard();
					}
				}
			}
		}
		
		//aktualizacja danych przy przenoszeniu
		private void dragHoverUpdate(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			HoverObject.setDrawPositions(x, y);
			if(MyHand.isShowing()) {
				if( (x<MyHand.startX() && x>=0) || (x>(MyHand.startX()+MyHand.width()) && x<Values.DEFAULT_X) || (y>0 && y<CurrentBoard.getHeight() - GameInfo.CardsLibrary[0].returnFrontImage().getHeight())) {
					MyHand.hideHand();
				}
			}
		}
		
		//podniesienie karty z pola
		private void pickCardFromField(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON1) {
				//podnoszenie kart z pól
				int x = event.getX();
				int y = event.getY();
				int width = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
				int height = GameInfo.CardsLibrary[0].returnFrontImage().getHeight();
				for(int i=0;i<8;i++) {
					if((!BoardFields[4*i+2].isEmpty()) && x>BoardFields[4*i+2].returnX() && x<(BoardFields[4*i+2].returnX()+width) && y>BoardFields[4*i+2].returnY() && y<(BoardFields[4*i+2].returnY()+height)) {
						liftCardFromField(4*i+2,x,y);
					}else if((!BoardFields[4*i+3].isEmpty()) && x>BoardFields[4*i+3].returnX() && x<(BoardFields[4*i+3].returnX()+height) && y>BoardFields[4*i+3].returnY() && y<(BoardFields[4*i+3].returnY()+width)) {
						liftCardFromField(4*i+3,x,y);
					}else if((!BoardFields[4*i].isEmpty()) && x>BoardFields[4*i].returnX() && x<(BoardFields[4*i].returnX()+width) && y>BoardFields[4*i].returnY() && y<(BoardFields[4*i].returnY()+height)) {
						liftCardFromField(4*i,x,y);
					}else if((!BoardFields[4*i+1].isEmpty()) && x>BoardFields[4*i+1].returnX() && x<(BoardFields[4*i+1].returnX()+height) && y>BoardFields[4*i+1].returnY() && y<(BoardFields[4*i+1].returnY()+width)) {
						liftCardFromField(4*i+1,x,y);
					}
				}
			}
		}
		
		//podniesienie karty z rêki
		private void pickCardFromHand(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON1) {
				//podnoszenie kart z rêki
				int x = event.getX();
				int y = event.getY();
				int width = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
				int height = GameInfo.CardsLibrary[0].returnFrontImage().getHeight();
				if((x>=MyHand.focusedX() && x<=(MyHand.focusedX()+width)) && (y>=MyHand.ShowY() && y<=CurrentBoard.getHeight())) {
					HoverObject.setCardID(MyHand.getCard(MyHand.getIndexOfFocus()));
					HoverObject.setDifferences(MyHand.focusedX(),MyHand.ShowY(), x, y);
					HoverObject.setHovering(true);
					MyHand.removeCard(MyHand.getIndexOfFocus());
					HoverObject.setOriginID(Values.HAND_ID);
					HoverObject.setWasOpen(false);
				}
			}
		}
		
		//postawienie karty na polu
		private void releaseHoveringCard(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON1) {
				if(MyHand.isShowing()) {
					HoverObject.setHovering(false);
					MyHand.addCard(HoverObject.getCardID());
				}else {
					int x = event.getX();
					int y = event.getY();
					int width = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
					int height = GameInfo.CardsLibrary[0].returnFrontImage().getHeight();
					HoverObject.setDrawPositions(x,y);
					int middleX = HoverObject.getX() + (width/2);
					int middleY = HoverObject.getY() + (height/2);
					HoverObject.setHovering(false);
					if((middleX>BoardFields[0].returnX() && middleX<BoardFields[0].returnX()+width) && (middleY>BoardFields[0].returnY() && middleY<BoardFields[0].returnY()+height)) {
						tryToAddField(0);
					}else if((middleX>BoardFields[4].returnX() && middleX<BoardFields[4].returnX()+width) && (middleY>BoardFields[4].returnY() && middleY<BoardFields[4].returnY()+height)) {
						tryToAddField(4);
					}else if((middleX>BoardFields[8].returnX() && middleX<BoardFields[8].returnX()+width) && (middleY>BoardFields[8].returnY() && middleY<BoardFields[8].returnY()+height)) {
						tryToAddField(8);
					}else if((middleX>BoardFields[12].returnX() && middleX<BoardFields[12].returnX()+width) && (middleY>BoardFields[12].returnY() && middleY<BoardFields[12].returnY()+height)) {
						tryToAddField(12);
					}else if((middleX>BoardFields[16].returnX() && middleX<BoardFields[16].returnX()+width) && (middleY>BoardFields[16].returnY() && middleY<BoardFields[16].returnY()+height)) {
						tryToAddField(16);
					}else if((middleX>BoardFields[20].returnX() && middleX<BoardFields[20].returnX()+width) && (middleY>BoardFields[20].returnY() && middleY<BoardFields[20].returnY()+height)) {
						tryToAddField(20);
					}else if((middleX>BoardFields[24].returnX() && middleX<BoardFields[24].returnX()+width) && (middleY>BoardFields[24].returnY() && middleY<BoardFields[24].returnY()+height)) {
						tryToAddField(24);
					}else if((middleX>BoardFields[28].returnX() && middleX<BoardFields[28].returnX()+width) && (middleY>BoardFields[28].returnY() && middleY<BoardFields[28].returnY()+height)) {
						tryToAddField(28);
					}else if((middleX>MyRejected.returnX() && middleX<MyRejected.returnX()+width) && (middleY>MyRejected.returnY() && middleY<MyRejected.returnY()+height)){
						MyRejected.addCard(HoverObject.getCardID());
					}else {
						if(HoverObject.OriginID()==Values.HAND_ID) {
							MyHand.addCard(HoverObject.getCardID());
						}else if(HoverObject.OriginID()==Values.REJECTED_ID){
							MyRejected.addCard(HoverObject.getCardID());
						}else {
							BoardFields[HoverObject.OriginID()].addCard(HoverObject.getCardID());
							if(HoverObject.wasOpen()) {
								BoardFields[HoverObject.OriginID()].openCard();
							}
						}
					}
				}
			}
		}
		
		//próba postawienia przenoszonej karty na jednym z pól lub na odrzuconych
		private void tryToAddField(int index) {
			if(BoardFields[index].isEmpty()) {
				if(BoardFields[index+1].isEmpty()) {
					// nie ma ¿adnej dolnej
					BoardFields[index].addCard(HoverObject.getCardID());
					if(HoverObject.wasOpen()) {
						BoardFields[index].openCard();
					}
				}else {
					// dolna uszkodzona
					if(BoardFields[index+2].isEmpty() && BoardFields[index+3].isEmpty()) {
						//nie ma ¿adnej górnej
						BoardFields[index+2].addCard(HoverObject.getCardID());
						BoardFields[index+2].openCard();
					}else {
						//jest górna
						if(HoverObject.OriginID()==Values.HAND_ID) {
							MyHand.addCard(HoverObject.getCardID());
						}else if(HoverObject.OriginID()==Values.REJECTED_ID){
							MyRejected.addCard(HoverObject.getCardID());
						}else {
							BoardFields[HoverObject.OriginID()].addCard(HoverObject.getCardID());
							if(HoverObject.wasOpen()) {
								BoardFields[HoverObject.OriginID()].openCard();
							}
						}
					}
				}
			}else {
				//dolna zdrowa
				if(BoardFields[index+2].isEmpty() && BoardFields[index+3].isEmpty()) {
					//nie ma ¿adnej górnej
					if(BoardFields[index].isOpen()) {
						BoardFields[index+2].addCard(HoverObject.getCardID());
						BoardFields[index+2].openCard();
					}else {
						if(HoverObject.OriginID()==Values.HAND_ID) {
							MyHand.addCard(HoverObject.getCardID());
						}else if(HoverObject.OriginID()==Values.REJECTED_ID){
							MyRejected.addCard(HoverObject.getCardID());
						}else {
							BoardFields[HoverObject.OriginID()].addCard(HoverObject.getCardID());
							if(HoverObject.wasOpen()) {
								BoardFields[HoverObject.OriginID()].openCard();
							}
						}
					}
				}else {
					//jest górna
					if(HoverObject.OriginID()==Values.HAND_ID) {
						MyHand.addCard(HoverObject.getCardID());
					}else if(HoverObject.OriginID()==Values.REJECTED_ID){
						MyRejected.addCard(HoverObject.getCardID());
					}else {
						BoardFields[HoverObject.OriginID()].addCard(HoverObject.getCardID());
						if(HoverObject.wasOpen()) {
							BoardFields[HoverObject.OriginID()].openCard();
						}
					}
				}
			}
		}
		
		//próba podniesienia karty z pola
		private void liftCardFromField(int index, int x, int y) {
			HoverObject.setCardID(BoardFields[index].getCardID());
			HoverObject.setDifferences(BoardFields[index].returnX(),BoardFields[index].returnY(), x, y);
			HoverObject.setHovering(true);
			if(BoardFields[index].isOpen()) {
				HoverObject.setWasOpen(true);
			}else {
				HoverObject.setWasOpen(false);
			}
			BoardFields[index].removeCard();
			HoverObject.setOriginID(index);
		}
		
		//pokaz lub ukryj reke
		private void checkMyHand(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			
			
			if(MyHand.isShowing()) {
				if( (x<MyHand.startX() && x>=0) || (x>(MyHand.startX()+MyHand.width()) && x<Values.DEFAULT_X) || (y>0 && y<MyHand.ShowY())) {
					MyHand.hideHand();
				}else {
					int currentX = MyHand.startX();
					int backCardWidth = (int) (MyHand.singleCardWidth()*Hand.backCardPart());
					int tinyBackUp = (int) (MyHand.singleCardWidth()*Hand.backUpParameter()); //parametr pomocniczy
					
					for(int i=0;i<MyHand.getIndexOfFocus();i++) {
						int hupi = 0;
						if(i==MyHand.getIndexOfFocus()-1) {
							hupi=tinyBackUp;
						}else {
							hupi=0;
						}
						
						if( (x>=currentX && x<(currentX+backCardWidth+hupi)) && (y>=MyHand.ShowY() && y<=Values.DEFAULT_Y) ) {
							MyHand.setIndexOfFocus(i);
						}
						currentX += backCardWidth;
					}
					
					currentX = MyHand.focusedX()+MyHand.singleCardWidth();
					
					for(int i=MyHand.getIndexOfFocus()+1;i<MyHand.size();i++) {
						int hupi = 0;
						if(i==MyHand.getIndexOfFocus()+1) {
							hupi=tinyBackUp;
						}else {
							hupi=0;
						}
						
						if( (x>=currentX-hupi && x<(currentX+backCardWidth)) && (y>=MyHand.ShowY() && y<=Values.DEFAULT_Y) ) {
							MyHand.setIndexOfFocus(i);
						}
						currentX += backCardWidth;
					}
				}
			}else {
				if((x>=MyHand.startX() && x<=(MyHand.startX()+MyHand.width())) && (y>=MyHand.HideY() && y<=Values.DEFAULT_Y)){
					MyHand.showHand();
				}
			}
		}
		
		//ustaw zycie
		private void checkMyLifeMeter(MouseEvent event) {
			if(event.getClickCount()==2 && event.getButton()==MouseEvent.BUTTON1) {
				int LifeFrameWidth = MyLife.returnFrameImage().getWidth();
				int LifeFrameHeight = MyLife.returnFrameImage().getHeight();
				int x = event.getX();
				int y = event.getY();
				
				for(int i=0;i<MyLife.getNumOfPositions();i++) {
					if((x>MyLife.getXbyIndex(i) && x<(MyLife.getXbyIndex(i)+LifeFrameWidth))&& (y>MyLife.getYbyIndex(i) && y<MyLife.getYbyIndex(i)+LifeFrameHeight)) {
						MyLife.setCurrentLifeIndex(i);
					}
				}
			}
		}
		
		//klikniêcie na taliê
		private void checkMyDeck(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON3) {
				if(MyDeck.size()>0) {
					int x = event.getX();
					int y = event.getY();
					if((x>= MyDeck.returnX() && x<=(MyDeck.returnX()+MyDeck.returnStackImage().getWidth())) && (y>=MyDeck.returnY() && y<=(MyDeck.returnY()+MyDeck.returnStackImage().getHeight()))) {
						PopUpWindow miniPop = new PopUpWindow(false, false, false, false, false, false, true, false, false);
						miniPop.setCardID(MyDeck.lastCardIndex());
						miniPop.setOriginID(0);
						miniPop.show(event.getComponent(), x, y);
					}
				}
			}
		}
		
		//klikniecie na odrzucone
		private void checkMyRejected(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON3) {
				if(MyRejected.size()>0) {
					int x = event.getX();
					int y = event.getY();
					if((x>= MyRejected.returnX() && x<=(MyRejected.returnX()+MyRejected.returnStackImage().getWidth())) && (y>=MyRejected.returnY() && y<=(MyRejected.returnY()+MyRejected.returnStackImage().getHeight()) ) ) {
						PopUpWindow miniPop = new PopUpWindow(false, false, false, false, false, false, false, true, false);
						miniPop.setCardID(0);
						miniPop.setOriginID(0);
						miniPop.show(event.getComponent(), x, y);
					}
				}
			}
		}
		
		//klikniêcie na pola
		private void checkFields(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON3) {
				int x = event.getX();
				int y = event.getY();
				int width = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
				int height = GameInfo.CardsLibrary[0].returnFrontImage().getHeight();
				for(int i=0;i<8;i++) {
					if((!BoardFields[4*i+2].isEmpty()) && x>BoardFields[4*i+2].returnX() && x<(BoardFields[4*i+2].returnX()+width) && y>BoardFields[4*i+2].returnY() && y<(BoardFields[4*i+2].returnY()+height)) {
						runPopUpOnField(4*i+2, x, y, true, event);
						break;
					}else if((!BoardFields[4*i+3].isEmpty()) && x>BoardFields[4*i+3].returnX() && x<(BoardFields[4*i+3].returnX()+height) && y>BoardFields[4*i+3].returnY() && y<(BoardFields[4*i+3].returnY()+width)) {
						runPopUpOnField(4*i+3, x, y, false, event);
						break;
					}else if((!BoardFields[4*i].isEmpty()) && x>BoardFields[4*i].returnX() && x<(BoardFields[4*i].returnX()+width) && y>BoardFields[4*i].returnY() && y<(BoardFields[4*i].returnY()+height)) {
						runPopUpOnField(4*i, x, y, true, event);
						break;
					}else if((!BoardFields[4*i+1].isEmpty()) && x>BoardFields[4*i+1].returnX() && x<(BoardFields[4*i+1].returnX()+height) && y>BoardFields[4*i+1].returnY() && y<(BoardFields[4*i+1].returnY()+width)) {
						runPopUpOnField(4*i+1, x, y, false, event);
						break;
					}
				}
			}
		}
		
		//odpalenie okienka na polu
		private void runPopUpOnField(int index, int x, int y, boolean healthy, MouseEvent event) {
			PopUpWindow miniPop;
			if(BoardFields[index].isOpen()) {
				if(healthy) {
					miniPop = new PopUpWindow(true, false, true, false, true, true, false, false, false);
				}else {
					miniPop = new PopUpWindow(true, false, false, true, false, true, false, false, false);
				}
			}else {
				miniPop = new PopUpWindow(true, true, false, false, false, true, false, false, false);
			}
			miniPop.setCardID(BoardFields[index].getCardID());
			miniPop.setOriginID(index);
			miniPop.show(event.getComponent(), x, y);
		}
		
		//klikniêcie na kartê na rêku
		private void checkFocused(MouseEvent event) {
			if(event.getButton()==MouseEvent.BUTTON3) {
				if(MyHand.size()>0) {
					int x = event.getX();
					int y = event.getY();
					int width = GameInfo.CardsLibrary[0].returnFrontImage().getWidth();
					int height = GameInfo.CardsLibrary[0].returnFrontImage().getHeight();
					if(x>= MyHand.focusedX() && x<=(MyHand.focusedX()+width) && y>=MyHand.ShowY() && y<=(MyHand.ShowY()+height))  {
						PopUpWindow miniPop = new PopUpWindow(true, false, false, false, false, false, false, false, false);
						miniPop.setCardID(MyHand.getCard(MyHand.getIndexOfFocus()));
						miniPop.setOriginID(MyHand.getIndexOfFocus());
						miniPop.show(event.getComponent(), x, y);
					}
				}
			}
		}
	}
	
	private class PopUpWindow extends JPopupMenu {
		
		JMenuItem ShowCard;
		JMenuItem OpenCard;
		JMenuItem CloseCard;
		JMenuItem HealCard;
		JMenuItem DamageCard;
		JMenuItem ReturnToHandFromField;
		JMenuItem DrawFromDeck;
		JMenuItem ShowRejected;
		JMenuItem ReturnToHandFromRejected;
		
		private int OriginID;
		private int CardID;
		private int x;
		private int y;
		
		
		PopUpWindow(){
			ShowCard = new JMenuItem("Poka¿ Kartê");
			OpenCard = new JMenuItem("Otwórz Kartê");
			CloseCard = new JMenuItem("Zamknij Kartê");
			HealCard = new JMenuItem("Ulecz Postaæ");
			DamageCard = new JMenuItem("Zadaj uszkodzenie");
			ReturnToHandFromField = new JMenuItem("Zwróæ kartê do rêki");
			DrawFromDeck = new JMenuItem("Dobierz Kartê");
			ShowRejected = new JMenuItem("Poka¿ Odrzucone Karty");
			ReturnToHandFromRejected = new JMenuItem("Zwróæ kartê do rêki");
			ShowCard.addActionListener(popList);
			OpenCard.addActionListener(popList);
			CloseCard.addActionListener(popList);
			HealCard.addActionListener(popList);
			DamageCard.addActionListener(popList);
			ReturnToHandFromField.addActionListener(popList);
			DrawFromDeck.addActionListener(popList);
			ShowRejected.addActionListener(popList);
			ReturnToHandFromRejected.addActionListener(popList);
			x = 0;
			y = 0;
			OriginID = 0;
			CardID = 0;
		}
		
		PopUpWindow(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h, boolean i){
			if(a) {
				ShowCard = new JMenuItem("Poka¿ Kartê");
				ShowCard.addActionListener(popList);
				add(ShowCard);
			}
			if(b) {
				OpenCard = new JMenuItem("Otwórz Kartê");
				OpenCard.addActionListener(popList);
				add(OpenCard);
			}
			if(c) {
				CloseCard = new JMenuItem("Zamknij Kartê");
				CloseCard.addActionListener(popList);
				add(CloseCard);
			}
			if(d) {
				HealCard = new JMenuItem("Ulecz Postaæ");
				HealCard.addActionListener(popList);
				add(HealCard);
			}
			if(e) {
				DamageCard = new JMenuItem("Zadaj uszkodzenie");
				DamageCard.addActionListener(popList);
				add(DamageCard);
			}
			if(f) {
				ReturnToHandFromField = new JMenuItem("Zwróæ kartê do rêki");
				ReturnToHandFromField.addActionListener(popList);
				add(ReturnToHandFromField);
			}
			if(g) {
				DrawFromDeck = new JMenuItem("Dobierz Kartê");
				DrawFromDeck.addActionListener(popList);
				add(DrawFromDeck);
			}
			if(h) {
				ShowRejected = new JMenuItem("Poka¿ Odrzucone Karty");
				ShowRejected.addActionListener(popList);
				add(ShowRejected);
			}
			if(i) {
				ReturnToHandFromRejected = new JMenuItem("Zwróæ kartê do rêki");
				ReturnToHandFromRejected.addActionListener(popList);
				add(ReturnToHandFromRejected);
			}
			x = 0;
			y = 0;
			OriginID = 0;
			CardID = 0;
		}
		
		public void setOriginID(int id) {
			OriginID = id;
		}
		
		public void setCardID(int id) {
			CardID = id;
		}
		
		public void setX(int _x) {
			x = _x;
		}
		
		public void setY(int _y) {
			y = _y;
		}
		
		private ActionListener popList = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getSource()==ShowCard) {
					ShowBigCardObject.setShowing(true);
					ShowBigCardObject.setImage(CardID);
				}
				if(arg0.getSource()==OpenCard) {
					BoardFields[OriginID].openCard();
				}
				if(arg0.getSource()==CloseCard) {
					BoardFields[OriginID].closeCard();
				}
				if(arg0.getSource()==HealCard) {
					BoardFields[OriginID-1].addCard(CardID);
					BoardFields[OriginID-1].openCard();
					BoardFields[OriginID].removeCard();
				}
				if(arg0.getSource()==DamageCard) {
					BoardFields[OriginID+1].addCard(CardID);
					BoardFields[OriginID+1].openCard();
					BoardFields[OriginID].removeCard();
				}
				if(arg0.getSource()==ReturnToHandFromField) {
					BoardFields[OriginID].removeCard();
					MyHand.addCard(CardID);
				}
				if(arg0.getSource()==DrawFromDeck) {
					MyDeck.removeCard();
					MyHand.addCard(CardID);
					if(TransformationTime>0) {
						TransformationTime--;
					}
				}
				if(arg0.getSource()==ShowRejected) {
					RejectedViewerObject.setShowing(true);
				}
				if(arg0.getSource()==ReturnToHandFromRejected) {
					MyRejected.removeCard(OriginID);
					MyHand.addCard(CardID);
					RejectedViewerObject.setShowing(false);
				}
			}
	    	
	    };
	}
}
