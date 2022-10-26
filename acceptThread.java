class acceptThread extends Thread {
	@Override
	public void run(){
		try{
			makeServerSockets socks= new makeServerSockets();
			socks.cSocket();
			System.out.println("Thread " + Thread.currentThread().getId()+ " is running");
		}catch(Exception e){
			System.out.println("Exception "+e);
		}
	}
}