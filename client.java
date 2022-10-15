
// client
import java.net.*;
import java.util.Scanner;
import java.io.*;

public class client {
    private Socket socket = null; //client instance
    private OutputStreamWriter cliSockWrite=null;
    BufferedReader clisockreadWrapper=null;
    private Scanner input= new Scanner(System.in);
    
    public client(String address, int port) throws IOException{
        try {
            socket = new Socket(address, port); 
            System.out.println("conncting to " + address + ":" + port);
            cliSockWrite=(new OutputStreamWriter(socket.getOutputStream(),"UTF8"));
            clisockreadWrapper=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF8"));
            
            System.out.println("Say something... stop to end connection");
            String mToServe=input.nextLine();
            //System.out.println((mToServe));
            while((mToServe.equals("stop"))== false){
                System.out.println("Say something... stop to close connection");
                mToServe=input.nextLine();
                try{
                    cliSockWrite.write(mToServe+"\r\n");
                    cliSockWrite.flush();
                    //cliSockWrite.close();
                    if(mToServe.equals("stop")){
                        System.out.println("breaking");
                        System.exit(0);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(mToServe);
            }
        } catch (IOException e) {
            System.out.println(e);
            cliSockWrite.close();
            socket.close();
        }
        
    }

    public static void main(String args[])throws IOException {
       
            client cl= new client("44.208.139.146", 4000);
        
    }
}
