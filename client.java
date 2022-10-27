
import java.io.*;
import java.net.*;
import java.util.Scanner;
  
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
        BufferedReader readIn= new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintStream readOut= new PrintStream(s.getOutputStream());
        
        // send credentials
        readOut.println(logOrReg);
        readOut.println(username);
        readOut.println(hash.createSHAHash(password)); // send hashed password

        if(adduser==true){ // send new user information is add user is true
            System.out.print("New users username: ");
            readOut.println(kb.nextLine());
            System.out.print("New users password: ");
            readOut.println(hash.createSHAHash(kb.nextLine()));
        }

        if(s.isConnected()==false){ // determine if the login was rejected or if there was an error
            s.close();
            kb.close();
            System.out.println("\nServer error or Login Failed");
        }

        //read message
        Thread readMessage=new Thread(new Runnable(){
            @Override
            public void run(){
                boolean cont=true;
                while (cont==true){
                    try{
                        String msg=readIn.readLine();
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