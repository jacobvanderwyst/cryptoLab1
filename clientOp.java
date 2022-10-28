//class handles reading and writing to the streams
//class listens on the socket

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;

public class clientOp implements Runnable{
	public String password;
	public String user;
	Scanner kb= new Scanner(System.in);
	public String name;
	final DataInputStream readIn;
	final DataOutputStream readOut;
	Socket s;
	boolean connected;
	
	
	public clientOp(Socket s, String name, DataInputStream readIn, DataOutputStream readOut, String user, String password) {
		this.readIn=readIn;
		this.readOut=readOut;
		this.name=name;
		this.s=s;
		this.connected=true;
		this.user=user;
		this.password=password;
	}
	

	@Override
	public void run(){//checks if socket is running
		//clientskip sk=new clientskip();
		while(connected==true){
			try{
				readIn.readUTF();
			}catch(SocketException e){
				connected=false;
				//e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		try{// clean streams
			this.readIn.close();
			this.readOut.close();
			this.s.close();
		}catch(SocketException e){
			//e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}