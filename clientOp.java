//class handles reading and writing to the streams
//class listens on the socket

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class clientOp implements Runnable{
	public String password;
	public String user;
	Scanner kb= new Scanner(System.in);
	private String name;
	final BufferedReader readIn;
	final PrintStream readOut;
	Socket s;
	boolean connected;
	
	
	public clientOp(Socket s, String name, BufferedReader readIn, PrintStream readOut, String user, String password) {
		this.readIn=readIn;
		this.readOut=readOut;
		this.name=name;
		this.s=s;
		this.connected=true;
		this.user=user;
		this.password=password;
	}

	@Override
	public void run(){
		while(connected==true){
			try{
				

				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		try{// clean streams
			this.readIn.close();
			this.readOut.close();
			this.s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}