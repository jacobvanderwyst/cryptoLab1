//credentials must start on new empty line

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;

public class server {
	static Vector<clientOp>arr=new Vector<>(); //stores client information
	static int clientNum=0;
	public static void main(String args[]) throws Exception
	{
		PrintWriter out = new PrintWriter(new FileOutputStream(new File("credentials.txt"),true));
		ServerSocket ss = new ServerSocket(4000);
		System.out.println("Server started on 4000");
		Scanner kb= new Scanner(new File("credentials.txt"));
		
		// load credentials into memory
		while(kb.hasNextLine()){
			String[] cred=kb.nextLine().split(" ");
			String user=cred[0];
			String password=cred[1];
			clientOp thisClient= new clientOp(null, null, null, null, user, password);
			arr.add(thisClient);
		}

		Socket s; // Socket for client communication
		boolean cont=true;
		while (cont==true){
			s=ss.accept();
			System.out.println("New client connected: "+s);

			//get I/O
			BufferedReader readIn= new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintStream readOut= new PrintStream(s.getOutputStream());

			String logOrReg=readIn.readLine();
			//get client credentials
			String user=readIn.readLine();
			String password=readIn.readLine();
			String nuser=readIn.readLine();
			String npass=readIn.readLine();

			//validate credentials
			boolean pass=false;
			for(clientOp ops:arr){
				if(ops.password.equals(password) && (ops.user.equals(user))){
					pass=true;
				}
			}
			if(pass==true){
				readOut.println("Server: Connection established");
				System.out.println("Client "+user+" connected");
			}else{
				System.out.println("Client "+user+" rejected for incorrect password");
				s.close();
			}

			// creates thread for each user
			clientOp thisClient= new clientOp(s,user, readIn, readOut, user, password);
			Thread thr= new Thread(thisClient);
			//register client
			if(logOrReg.equals("reg") && pass==true){// if the current user is able to login, let it create a user
				//determine if client is registered
				pass=false;
				for(clientOp ops:arr){
					if(ops.user.equals(nuser) && (ops.password.equals(npass))){
						pass=true; // user is registered
					}
				}
				if(pass==false){ // user is not registered
					readOut.println("Server: User registered");
					System.out.println(nuser+" has been registered");
					//add a new client
					arr.add(thisClient); // add client to database memory pool
					out.println(nuser+" "+npass); // add client to database storage
					out.flush(); // commit to storage
				}else{
					System.out.println("User "+nuser+" already registered");
					readOut.println("Server: "+nuser+" already registered");
				}
				
			}
			thr.start(); // start the user thread
			clientNum++;
			
			
			//send message
			/*Thread sendMessage=new Thread(new Runnable(){
				@Override
				public void run() {
					boolean cont=true;
					while(cont==true){
						String msg = kb.nextLine();
						readOut.println("Server: "+msg); // send message
						System.out.print("Send a message: ");
						if((msg.equals("exit"))||(msg==null)||(msg.equals(""))){
							//exit
							cont=false;
							break;
						}
					}
				}
			});*/
			//read message
			/*Thread readMessage=new Thread(new Runnable(){
				@Override
				public void run(){
					boolean cont=true;
					while (cont==true){
						try{
							String msg=readIn.readLine();
							if((msg.equals("exit"))||(msg==null)||(msg.equals(""))){
								//exit
								System.out.println("break");
								cont=false;
								break;
							}else{
								System.out.println(msg);
							}
							
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			});
			sendMessage.start();
			readMessage.start();*/
		}
		out.close();
		ss.close();
		kb.close();

	}
}
