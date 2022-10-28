
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
  
public class client {
    public static String getRandomValue() {
        String out="";
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random rnd = new Random();
        int index = (int) (rnd.nextFloat() * source.length()); // get index of random value in source
        out=out+source.charAt(index); // return the value
        return out;
    }
    public static void main(String args[])throws Exception{
        //create objects
        HashCreator hash= new HashCreator();
        Scanner kb= new Scanner(System.in);

        // define client configurations
        boolean adduser=false;
        System.out.print("type localhost or server address\n");
        String host= kb.nextLine();
        System.out.println("login or (reg)ister user\nJacob is the only valid user at start, password is 1234");
        String logOrReg=kb.nextLine();
        if(logOrReg.equals("reg")){
            adduser=true;
        }
        System.out.print("Username: ");
        String username= kb.nextLine();
        System.out.print("Password: ");
        String password= kb.nextLine();
        

        if(host.equals("localhost") == false){// define host to connect to
            host="44.208.139.146";
        }else{
            host="localhost";
        }
        Socket s = new Socket(host, 4000);
        DataInputStream readIn= new DataInputStream(s.getInputStream());
        DataOutputStream readOut= new DataOutputStream(s.getOutputStream());
        
        // send credentials
        readOut.writeUTF(logOrReg);
        readOut.writeUTF(username);
        readOut.writeUTF(hash.createSHAHash(password)); // send hashed password

        if(adduser==true){ // send new user information is add user is true
            System.out.print("New users username: ");
            readOut.writeUTF(kb.nextLine());
            System.out.print("New users password: ");
            readOut.writeUTF(hash.createSHAHash(kb.nextLine()));
        }

        if(s.isConnected()==false){ // determine if the login was rejected or if there was an error
            s.close();
            kb.close();
            System.out.println("\nServer error or Login Failed");
        }
        System.out.println(readIn.readUTF()); // established
        System.out.println(readIn.readUTF()); // user registered
        System.out.println(readIn.readUTF());// file transfer

        clientskip csk= new clientskip();

        //key exchanges
        PublicKey pk=null;
        //create client key
        KeyPair kp=csk.createClientKey();
        //System.out.println("client key created");

        //send client key
        byte[] barr=kp.getPublic().getEncoded();
        try {
            readOut=new DataOutputStream(s.getOutputStream());
            readOut.writeInt(barr.length);  //send len yKey
            readOut.write(barr); // send string of yKey bytes
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("client key sent");

        //get server key
        byte[]bar=new byte[readIn.readInt()];
        readIn.readFully(bar);
        try {//get servers public key
            KeyFactory kf= KeyFactory.getInstance("DH");
            X509EncodedKeySpec spec509= new X509EncodedKeySpec(bar);
            try {
                pk=kf.generatePublic(spec509);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //System.out.println("server key received");
        //create secret session key
        byte[] ssk=csk.getSecretSessionKey(kp, pk);
        //System.out.println("Seceret session key creaed");

        //file operations
        SecretKey sk=csk.getDeskey(ssk);
        //System.out.println("Secret des key created");

        //read server file
        FileOutputStream fos=new FileOutputStream("clientfile.txt");
        try {
            //Start writing file out
            try {
                int ivSize=readIn.readInt();
                byte[]iv=new byte[ivSize];
                readIn.readFully(iv);
                IvParameterSpec ivps =new IvParameterSpec(iv);
                //System.out.println("params");

                Cipher des=Cipher.getInstance("DES/CBC/PKCS5Padding");
                try {
                    des.init(Cipher.DECRYPT_MODE, sk, ivps);
                } catch (InvalidAlgorithmParameterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //System.out.println("cipher");
                byte[] input=new byte[64];
                boolean cont=true;
                int count=0;
                int byteRead=readIn.read(input);
                while(cont==true){
                    
                    //System.out.println("bread "+byteRead);
                    if(byteRead==-1){
                        //System.out.println("end read");
                        cont=false;
                        break; // EOF
                    }
                    byte[] out=des.update(input,0, byteRead);
                    if(out != null){
                        fos.write(out);
                        System.out.print(new String(out));
                    }else{
                        cont=false;

                    }
                    //System.out.println("\nstuck in loop");
                    count++;
                    if(count==400){
                        break;
                    }
                    byteRead=readIn.read(input);
                }
                //System.out.println("\nread bytes");
                byte[] out=des.doFinal();
                if(out!=null){
                    fos.write(out);
                    
                    System.out.print(new String(out));
                }
            } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        fos.flush();
        fos.close();
        //readIn.close();
        //readOut.close();
        //System.out.println("");
        //System.out.println("read server file");

        //create file
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
        //System.out.println("created client file");

        // send client file
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
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                fis.close();
        //read message
       /*Thread readMessage=new Thread(new Runnable(){
            @Override
            public void run(){
                boolean cont=true;
                while (cont==true){
                    try{
                        String msg=readIn.readUTF();
                        if(msg==null){
                            cont=false;
                            break;
                        }
                        System.out.println(msg);
                    }catch(SocketException e){
                        try {
                            cont=false;
                            System.out.print("Error: "+e);
                            s.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        
                    }
                }
            }
        });
        readMessage.start();*/
    }catch(Exception e){
        e.printStackTrace();
    }
}
}