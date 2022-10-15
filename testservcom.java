import java.io.*;
import java.net.*;

public class testservcom{
        private ServerSocket midm;
        private int port=4001;
        private Socket client;

        public boolean checkvalid(String choice){
                if(choice==""){
                        return true;
                }else if(choice=="stop"){
                        return true;
                }else{
                        return false;
                }
        }

        protected void makeSS(){
                try{
                        while(true){
                                midm=new ServerSocket(port);
                                client=midm.accept();
                                System.out.println("Client connected");
                                midm.close();
                                PrintWriter out=new PrintWriter(client.getOutputStream(),true);
                                BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
                                BufferedReader stdIn=new BufferedReader(new InputStreamReader(System.in));
                                String line=in.readLine();
                                while(checkvalid(line)==false){
                                        System.out.println("echo: "+line);
                                        out.println("test");
                                        if(checkvalid(line)==true){
                                                System.exit(0);
                                        }
                                }
                            }
                    }catch(IOException e){
                            System.out.println("Server error: "+e);
                    }
            }
    
    
    
            public static void main(String[] ar)throws IOException{
                    testservcom test=new testservcom();
                    test.makeSS();
            }
    }
    