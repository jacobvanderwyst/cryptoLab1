
import java.io.*;
import java.net.*;
import java.util.Scanner;
  
class client {
  
    public static void main(String args[])throws Exception{
        //44.208.139.146
        // Create client socket
        Scanner kb= new Scanner(System.in);
        Socket s = new Socket("44.208.139.146", 4000);
        BufferedReader readIn= new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintStream readOut= new PrintStream(s.getOutputStream());
        
        //send message 
        Thread sendMessage=new Thread(new Runnable(){
            @Override
            public void run() {
                while(true){
                    String msg = kb.nextLine();
                    try{
                        readOut.println(msg); // send message
                        if(msg.equals("exit")){
                            //exit
                            System.exit(0);
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
                while (true){
                    try{
                        String msg=readIn.readLine();
                        System.out.println("Recieved: " + msg);
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