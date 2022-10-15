
// client
import java.net.*;
import java.io.*;

public class tclient {
    private Socket socket = null; //socket instance
    private BufferedReader input = null; //from server
    private PrintWriter out = null; //
    private BufferedReader sc = null;
    private byte[] arr = null;
    public void sendMessage(String message) throws IOException{
        out= new PrintWriter(socket.getOutputStream());
        try {
            out.println(message);
            arr = input.readLine().getBytes();
            sc = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(new String(arr));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public tclient(String address, int port) throws IOException{

        try {
            socket = new Socket(address, port); 
            out = new PrintWriter(socket.getOutputStream(), true);
            input= new BufferedReader(new InputStreamReader(socket.getInputStream())); //from server
            sc = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("stream from server: "+out);
        } catch (IOException e) {
            System.out.println(e);
            input.close();
            out.close();
            socket.close();
        }
        System.out.println("attempting to connect\nSay someting");
        // user input
        String line = "";
        line = sc.readLine();
        if(line.equals("stop")){
            socket.close();
            input.close();
            out.close();
        }
        while (line.equals("stop") == false) {
            // encrypted eventually
            System.out.println("say someting");
            line = sc.readLine();
            if(line.equals("stop")){
                socket.close();
                input.close();
                out.close();
            }
            try {
                arr = line.getBytes("UTF8");
                out.println(arr);
            } catch (IOException e) {
                System.out.println(e);
                socket.close();
                input.close();
                out.close();
            }
            
        }
        try{//try to print out the server messages
            System.out.println("Server: " + input.readLine());
        }catch (IOException e) {
            System.out.println("Server error in client: "+e);
            socket.close();
            input.close();
            out.close();
            
        }
        System.out.println("loop ended");
        // close the connection
        try {
            System.out.println("Disconnected");
            input.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
            socket.close();
            input.close();
            out.close();
        }
    }

    public static void main(String args[])throws IOException {
        new tclient("44.208.139.146", 4001);
    }
}
