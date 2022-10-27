// Server2 class that
// receives data and sends data

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;

public class server {
	static Vector<clientOp>arr=new Vector<>();
	static int clientNum=0;
	public static void main(String args[]) throws Exception
	{
		ServerSocket ss = new ServerSocket(4000);
		System.out.println("Server started on 4000");
		Scanner kb= new Scanner(System.in);
		
		Socket s; // Socket for client communication
		boolean cont=true;
		while (cont==true){
			s=ss.accept();
			System.out.println("New client connected: "+s);

			//get I/O
			BufferedReader readIn= new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintStream readOut= new PrintStream(s.getOutputStream());
			readOut.println("Connection established");

			clientOp thisClient= new clientOp(s,"client "+clientNum, readIn, readOut);
			Thread thr= new Thread(thisClient);

			System.out.println("Client: "+clientNum+" connected");

			arr.add(thisClient);
			thr.start();

			clientNum++;
			

			Thread sendMessage=new Thread(new Runnable(){
				@Override
				public void run() {
					boolean cont=true;
					while(cont==true){
						String msg = kb.nextLine();
						try{
							readOut.println("Server: "+msg); // send message
							if((msg.equals("exit"))||(msg==null)||(msg.equals(""))){
								//exit
								cont=false;
								break;
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			});
			sendMessage.start();
			for(clientOp a:server.arr){
				readOut.println(a+" ");
			}
		}
		
	}
}
