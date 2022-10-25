// Server2 class that
// receives data and sends data

import java.io.*;
import java.net.*;
import java.lang.*;

class server extends makeServerSockets {

	public static void main(String args[]) throws Exception
	{
		/*  Create server Socket
		ServerSocket ss = new ServerSocket(4000);
		System.out.println("Server started on 4000");
		// connect it to client socket
		Socket s = ss.accept();
		System.out.println("Connection established");

		// to send data to the client
		PrintStream ps
			= new PrintStream(s.getOutputStream());

		// to read data coming from the client
		BufferedReader br
			= new BufferedReader(
				new InputStreamReader(
					s.getInputStream()));

		// to read data from the keyboard
		BufferedReader kb
			= new BufferedReader(
				new InputStreamReader(System.in));

		// server executes continuously
		ps.println("server says \"hello world\"");
		while (true) {
		
			String str, str1;

			// repeat as long as the client
			// does not send a null string

			// read from client
			
			while ((str = br.readLine()) != null) {
				if((str = br.readLine()) != ""){
					System.out.println("Message received");
					System.out.println("Client: "+str);
					str1 = kb.readLine();
					ps.println("ayo");
	
					// send to client
					ps.println(str1);
					System.out.println("Message \"" + str1 + "\" sent\nWaiting for client message...");
				}else{
					// close connection
					ps.close();
					br.close();
					kb.close();
					ss.close();
					s.close();
					System.exit(1); //empty message
				}
			}

			// close connection
			ps.close();
			br.close();
			kb.close();
			ss.close();
			s.close();

			// terminate application
			System.exit(0);

		} // end of while */
		//server creation
		makeServerSockets sock= new makeServerSockets();
		ServerSocket ss=sock.cSSocket();
		sock.cSocket();

		while(((sock.ssState(ss)[1].equals("bound"))&&(sock.ssState(ss)[0].equals("connected")))==true) {
			
		}
	}
}
