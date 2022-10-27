import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class clientOp implements Runnable{
	Scanner kb= new Scanner(System.in);
	private String name;
	final BufferedReader readIn;
	final PrintStream readOut;
	Socket s;
	boolean connected;
	
	public clientOp(Socket s, String name, BufferedReader readIn, PrintStream readOut) {
		this.readIn=readIn;
		this.readOut=readOut;
		this.name=name;
		this.s=s;
		this.connected=true;
	}

	@Override
	public void run(){
		String msg;
		boolean cont=true;
		while(cont==true){
			try{
				msg=readIn.readLine();
				System.out.println(msg);

				if((msg.equals("exit"))||(msg==null)||(msg.equals(""))){
					this.connected=false;
					cont=false;
					this.s.close();
					break;
				}
				msg=msg+"@";
				String[] splitMsg=msg.split("@");
				String sendThis=splitMsg[0];
				String destination=splitMsg[1];

				for(clientOp ops:server.arr){
					if(ops.name.equals(destination)){
						readOut.println(this.name+": "+sendThis);
						break;
					}else if(destination==""){
						readOut.println(this.name+": "+sendThis);
					}else{
						readOut.println(this.name+": "+sendThis);	
					}

				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		try{
			this.readIn.close();
			this.readOut.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}