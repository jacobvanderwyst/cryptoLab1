//credentials must start on new empty line

import java.io.*;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class server {
	static Vector<clientOp>arr=new Vector<>(); //stores client information
	static int clientNum=0;

	public static String getRandomValue() {
        String out="";
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random rnd = new Random();
        int index = (int) (rnd.nextFloat() * source.length()); // get index of random value in source
        out=out+source.charAt(index); // return the value
        return out;
    }
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
				readOut.writeUTF("Begin File Transfer\n");
				serverskip csk= new serverskip();

				//key exchanges
				KeyPair kp=csk.createServerKey();
				//System.out.println("server key created");
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
				//System.out.println("client key received");
				
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
				//created des key
				SecretKey sk=csk.getDeskey(ssk);
				//System.out.println("Secret des key created");

				//created server file to send
				try {
					PrintWriter pw=new PrintWriter(new FileOutputStream(new File("serverfile.txt")));
					int i=0;
					String sss="";
					while(i<100){
						sss=sss+getRandomValue();
						i+=1;
					}
					pw.print(sss);
					pw.flush();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("\ncreated server file");

				//encrypted and sent server file
				FileInputStream fis=new FileInputStream("serverfile.txt");
				try {
					Cipher des=Cipher.getInstance("DES/CBC/PKCS5Padding");
					des.init(Cipher.ENCRYPT_MODE, sk);
		
					//Start writing file out
					byte[] iV=des.getIV();
					try {
						readOut.writeInt(iV.length);
						readOut.write(iV);
		
						byte[] input=new byte[64];
						while(true){
							int byteRead=fis.read(input);
							if(byteRead==-1){
								break; // EOF
							}
							byte[] output=des.update(input,0, byteRead);
							if(output != null){
								readOut.write(output);
							}
							
						}
						byte[] output=des.doFinal();
							if(output!=null){
								readOut.write(output);
							}
						readOut.flush();
						readOut.close();
						readIn.close();
						
					} catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(InvalidKeyException e){
					e.printStackTrace();
				}
				fis.close();
				//System.out.println("sent server file");

				//decrypted client file and save it
				FileOutputStream fos=new FileOutputStream("clientfile.txt");
				try {
					//Start writing file out
					try {
						int ivSize=readIn.readInt();
						byte[]iv=new byte[ivSize];
						readIn.readFully(iv);
						IvParameterSpec ivps =new IvParameterSpec(iv);
		
						Cipher des=Cipher.getInstance("DES/CBC/PKCS5Padding");
						try {
							des.init(Cipher.DECRYPT_MODE, sk, ivps);
						} catch (InvalidAlgorithmParameterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		
						byte[] input=new byte[64];
						while(true){
							int byteRead=readIn.read(input);
							if(byteRead==-1){
								break; // EOF
							}
							byte[] outp=des.update(input,0, byteRead);
							if(outp != null){
								fos.write(outp);
								System.out.print(new String(outp));
							}
						}
						byte[] outpp=des.doFinal();
						if(out!=null){
							fos.write(outpp);
							System.out.print(new String(outpp));
						}
					} catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(InvalidKeyException e){
					e.printStackTrace();
				}
				fos.close();
				//System.out.println("read client file");

				
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
