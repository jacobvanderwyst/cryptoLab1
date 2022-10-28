
import java.io.*;
import java.net.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

import javax.crypto.SecretKey;
  
public class client {
  
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
        csk.readFileOut(sk, readIn, readOut);
        csk.createFile();
        csk.writeFileOut(sk, readOut);

        //read message
       Thread readMessage=new Thread(new Runnable(){
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
        readMessage.start();
    }
}