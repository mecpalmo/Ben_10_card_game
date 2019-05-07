import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server{
	
	private static JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private boolean isConnected = false;
	
	public Server(){
		isConnected = false;
		chatWindow = new JTextArea();
	}
	
	public void startRunning() {
		try {
			server = new ServerSocket(Values.PORT, 100);
			while(true) {
				try {
					waitForConnection();
					setUpStream();
					whileChatting();
				}catch(EOFException eofException) {
					showMessage("\n Server Ended the connection!");
				}finally {
					closeCrap();
				}
			}
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage("Now connected to "+connection.getInetAddress().getHostName());
	}
	
	private void setUpStream() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup! \n");
	}
	
	private void whileChatting() throws IOException {
		String message = "You are now connected! ";
		isConnected = true;
		
		//sendMessage(message);
		do {
			try {
				message = (String)input.readObject();
				//showMessage("\n "+ message);
				readMessage(message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n idk wtf that user sent!");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	private void closeCrap() {
		showMessage("\n Closing connections... \n");
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void sendMessage() {
		if(isConnected) {
		String mess = prepareMessage();
		sendMessage(mess);
		}
	}
	
	private void sendMessage(String message) {
		try {
			output.writeObject(message);
			output.flush();
			//showMessage("\nSERVER - "+ message);
		}catch(IOException ioException) {
			chatWindow.append("\n ERROR: DUDE I CANT SEND THAT MESSAGE");
		}
	}
	
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(text);
				System.out.println(text);
			}
		});
	}
	
	private String prepareMessage() {
		String temp = "";
		for(int i=0;i<Values.MyFieldsValues.length;i++) {
			temp = temp + intToString(Values.MyFieldsValues[i]);
		}
		temp = temp + intToString(Values.MyDeckSize);
		temp = temp + intToString(Values.MyHandSize);
		temp = temp + intToString(Values.MyLifeIndex);
		temp = temp + intToString(Values.MyTopRejectedValue);
		temp = temp + intToString(Values.MyTransformationTime);
		System.out.println(temp); /////
		return temp;
	}
	
	private String intToString(int k) {
		Integer x = k;
		String a;
		if(x>=0 && x<=9) {
			a = "0"+ x.toString();
		}else {
			a = x.toString();
		}
		return a;
	}
	
	private int StringToInt(String k) {
		int x;
		if(k.charAt(0)=='0') {
			x = Integer.parseInt(k.substring(1, 2));
		}else {
			x = Integer.parseInt(k);
		}
		return x;
	}
	
	private void readMessage(String mess) {
		int len = Values.OpponentsFieldsValues.length;
		for(int i=0;i<len;i++) {
			Values.OpponentsFieldsValues[i] = StringToInt(mess.substring(i*2, i*2+2));
		}
		Values.OpponentsDeckSize = StringToInt(mess.substring(len*2,len*2+2));
		Values.OpponentsHandSize = StringToInt(mess.substring(len*2+2, len*2+4));
		Values.OpponentsLifeIndex = StringToInt(mess.substring(len*2+4, len*2+6));
		Values.OpponentsTopRejectedValue = StringToInt(mess.substring(len*2+6, len*2+8));
		Values.OpponentsTransformationTime = StringToInt(mess.substring(len*2+8, len*2+10));
	}
	
	public static JTextArea getTextArea() {
		return chatWindow;
	}
}
