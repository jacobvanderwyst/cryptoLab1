import java.io.*; //Server Side Computing.
import java.net.*; //Classic single user at a time client-server processing.

public class EchoTelnet {
    public static void main(String[] args) {
        System.out.println("before try");
        try { // Attempt to set up a communications socket.
            System.out.println("creating socket");
            ServerSocket serv = new ServerSocket(6622);
            System.out.println(serv);
            Socket inOut = serv.accept(); // Sleep, wait, block till connection
            System.out.println("inout " + inOut);
            // Attach reader / writer streams.
            BufferedReader inPut = new BufferedReader(new InputStreamReader(inOut.getInputStream()));
            PrintWriter outPut = new PrintWriter(inOut.getOutputStream(), true /* autoFlush */);

            // Treat as simple file I/O
            outPut.println("Welcome to Echo Server");
            outPut.println("Enter 'Stop' to terminate server. ");

            boolean finished = false;
            System.out.println("Server started.  Start telnet process!");
            while (!finished) {
                String strIn = inPut.readLine(); // Sleep till input available.
                if (strIn == null)
                    finished = true;
                else {
                    outPut.println("Echo: " + strIn);
                    if (strIn.trim().equals("Stop"))
                        finished = true;
                }
            }
            System.out.println("closing");
            inPut.close(); // Closed by default when socket closed.
            outPut.close(); // Closed by default when socket closed.
            inOut.close(); // Socket Closed.
        } catch (Exception e) {
            System.out.println("caught an e");
            System.out.println(e);
        }
    }
}
