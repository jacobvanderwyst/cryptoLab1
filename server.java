//credentials must start on new empty line

import java.io.*;
import java.net.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import java.util.Vector;

import javax.crypto.SecretKey;

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
			DataInputStream readIn= new DataInputStream(s.getInputStream());
			DataOutputStream readOut= new DataOutputStream(s.getOutputStream());

			String logOrReg=readIn.readUTF();
			//get client credentials
			String user=readIn.readUTF();
			String password=readIn.readUTF();
			String nuser="";
			String npass="";
			if(logOrReg.equals("reg")){
				nuser=readIn.readUTF();
				npass=readIn.readUTF();
			}
			

			//validate credentials
			boolean pass=false;
			for(clientOp ops:arr){
				if(ops.password.equals(password) && (ops.user.equals(user))){
					pass=true;
				}
			}
			if(pass==true){
				readOut.writeUTF("Server: Connection established");
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
					readOut.writeUTF("Server: User registered");
					System.out.println(nuser+" has been registered");
					//add a new client
					arr.add(thisClient); // add client to database memory pool
					out.println(nuser+" "+npass); // add client to database storage
					out.flush(); // commit to storage
				}else{
					System.out.println("User "+nuser+" already registered");
					readOut.writeUTF("Server: "+nuser+" already registered");
				}
				readOut.writeUTF("Begin File Transfer");
				serverskip csk= new serverskip();

				//key exchanges
				KeyPair kp=csk.createServerKey();
				System.out.println("server key created");
				PublicKey pk=null;

				//get client key
				try {
					//System.out.println("about to read key");
					byte[] barr=new byte[readIn.readInt()];
					//System.out.println("key read int");
					readIn.readFully(barr);
					//System.out.println("key read all");
		
					try {
						KeyFactory kf= KeyFactory.getInstance("DH");
						X509EncodedKeySpec spec509= new X509EncodedKeySpec(barr);
						try {
							pk=kf.generatePublic(spec509);
						} catch (InvalidKeySpecException e) {
							
							e.printStackTrace();
						}
					} catch (NoSuchAlgorithmException e) {
						
						e.printStackTrace();
					}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				System.out.println("client key received");
				
				//send server key
				byte[] barr=kp.getPublic().getEncoded(); // get yKey
				try {
					readOut.writeInt(barr.length);  //send len yKey
					readOut.write(barr); // send string of yKey bytes
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("server key sent");
				//create secret session key
				byte[] ssk=csk.getSecretSessionKey(kp, pk);
				//System.out.println("Secret session key created");

				//file operations
				SecretKey sk=csk.getDeskey(ssk);
				csk.readFileOut(sk, readIn, readOut);
				csk.createFile();
				csk.writeFileOut(sk, readOut);
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
						readOut.writeUTF("Server: "+msg); // send message
						System.out.print("Send a message: ");
						if((msg.equals("exit"))||(msg==null)||(msg.equals(""))){
							//exit
							cont=false;
							break;
						}
					}
				}
			});
			//read message
			Thread readMessage=new Thread(new Runnable(){
				@Override
				public void run(){
					boolean cont=true;
					while (cont==true){
						try{
							String msg=readIn.readUTF();
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
