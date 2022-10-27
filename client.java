
import java.io.*;
import java.net.*;
import java.util.Scanner;
  
public class client {
  
    public static void main(String args[])throws Exception{
        //create objects
        HashCreator hash= new HashCreator();
        Scanner kb= new Scanner(System.in);

        // define client configurations
        
        System.out.print("type localhost or server address\n");
        String host= kb.nextLine();
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
        BufferedReader readIn= new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintStream readOut= new PrintStream(s.getOutputStream());
        
        // send credentials
        readOut.println(username);
        readOut.println(hash.createSHAHash(password)); // send hashed password
        if(s.isConnected()==false){ // 
            s.close();
            kb.close();
            System.out.println("Server error or Login Failed");
        }

        //read message
        Thread readMessage=new Thread(new Runnable(){
            @Override
            public void run(){
                boolean cont=true;
                while (cont==true){
                    try{
                        String msg=readIn.readLine();
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