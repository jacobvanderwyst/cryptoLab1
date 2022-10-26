import java.io.*;
import java.net.*;

public class makeServerSockets{
	// Create server Socket
	private ServerSocket ss = null;
	// connect it to client socket
	private static Socket s = null;
	// to send data to the client
	private PrintStream ps=null;
	// to read data coming from the client
	private static BufferedReader br= null;
	// to read data from the keyboard
	private BufferedReader kb=null;

	//sockets
	public ServerSocket[] setSS() throws IOException{
		int i=0;
		ServerSocket[] mSS=new ServerSocket[10];
		ServerSocket sSock=null;
		while(i<10){
			sSock=new ServerSocket(4000+i);
			mSS[i]=sSock;
			i++;
			System.out.println("Server started on "+sSock.getLocalSocketAddress());
		}
		
		return mSS;
	}
	public static ServerSocket getSS(ServerSocket[] socks, int snum){//return instance of ServerSocket for server socket
		return socks[snum];
	}
	public Socket cSocket() throws IOException{//return instance of Socket instance from client
		try{
			s=ss.accept();
		}catch (Exception e){
			System.out.println("Error: "+e);
		}
		System.out.println("Client "+s.getInetAddress()+":"+s.getPort()+" connected");
		return s;
	}
	//output to client
	public PrintStream createPS(Socket s) throws IOException{//return instance of PrintStream
		ps=new PrintStream(s.getOutputStream());
		return ps;
	}
	public BufferedReader cReadServer()throws IOException{//return instance of BufferedReader
		kb=new BufferedReader(new InputStreamReader(System.in));
		return kb;
	}
	public void sendClient(String msg) throws IOException{//send message to the client
		String s=kb.readLine();
		kb.close();
		ps.println(s);
		System.out.println("Message :"+msg+" sent to client");
	}
	//input from client
	public BufferedReader cReadClient()throws IOException{//return instance of BufferedReader for input from client
		br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		return br;
	}
	public static String[] ssState(ServerSocket ss) throws SocketException{// 0 connection, 1 bind, 2 buffer
		String[] state=new String[3];

		if(ss.isClosed()==true){
			state[0]="disconnected";
		}else{
			state[0]="connected";
		}
		if(ss.isBound()==true){
			state[1]="bound";
		}else{
			state[1]="unbound";
		}
		state[2]=Integer.toString(ss.getReceiveBufferSize());
		
		return state;
	}

	
}
