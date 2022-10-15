
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class server {
    private ServerSocket middleman;
    private int port = 4000;
    private Socket client;
    private InputStreamReader cliSockReader=null;
    private OutputStreamWriter cliSockWrite=null;
    private BufferedReader clisockreadWrapper=null;
    private Scanner inp=null;
    protected void createSocketServer()
    {
        try
        {
            while (true){
                System.out.println("Creating socket server on port: "+port+" ...");
                middleman = new ServerSocket(port);
                
                client = middleman.accept();
                cliSockWrite=(new OutputStreamWriter(client.getOutputStream(),"UTF8"));
                System.out.println("Accepted connection from "+client.getInetAddress().getHostAddress());
                middleman.close();
                inp= new Scanner(System.in);
                cliSockReader=(new InputStreamReader(client.getInputStream(),"UTF8"));
                clisockreadWrapper=new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF8"));
                String line;
                while((line = clisockreadWrapper.readLine()) != null)
                {
                    System.out.println("Client: "+line);
                    if((line.equals("stop"))==true){
                        System.exit(0);
                    }
                }
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

    public static void main(String[] args){
        server test = new server();
        test.createSocketServer();
    }
}