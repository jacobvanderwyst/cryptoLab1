// Server2 class that
// receives data and sends data

import java.io.*;
import java.net.*;
import java.util.Vector;

public class server {
	static Vector<clientOp>arr=new Vector<>();
	static int clientNum=0;
	public static void main(String args[]) throws Exception
	{
		ServerSocket ss = new ServerSocket(4000);
		System.out.println("Server started on 4000");
		
		Socket s; // Socket for client communication

		while (true){
			s=ss.accept();
			System.out.println("New client connected: "+s);

			//get I/O
			BufferedReader readIn= new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintStream readOut= new PrintStream(s.getOutputStream());
			readOut.println("Connection established");

			clientOp thisClient= new clientOp(s,"client "+clientNum, readIn, readOut);
			Thread thr= new Thread(thisClient);

			System.out.println("Client added");

			arr.add(thisClient);
			thr.start();

			clientNum++;
		}
	}
}
