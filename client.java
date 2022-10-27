
import java.io.*;
import java.net.*;
import java.util.Scanner;
  
public class client {
  
    public static void main(String args[])throws Exception{
        //44.208.139.146
        // Create client socket
        Scanner kb= new Scanner(System.in);
        System.out.print("type localhost or server address");
        String host= kb.nextLine();
        if(host.equals("localhost") == false){
            host="44.208.139.146";
        }else{
            host="localhost";
        }
        Socket s = new Socket(host, 4000);
        BufferedReader readIn= new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintStream readOut= new PrintStream(s.getOutputStream());
        
        //send message 
        Thread sendMessage=new Thread(new Runnable(){
            @Override
            public void run() {
                boolean cont=true;
                System.out.print("Send a message: ");
                while(cont==true){
                    String msg = kb.nextLine();
                    try{
                        readOut.println(msg); // send message
                        System.out.print("Send a message: ");
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
        //read message
        Thread readMessage=new Thread(new Runnable(){
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
        readMessage.start();
    }
}