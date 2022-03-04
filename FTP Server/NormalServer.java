import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NormalServer extends Thread{ 

	public static ServerSocket serverNSocket;
	static Socket socket = null;
	static int port;
	public NormalServer(int nPort) throws IOException { 
		 serverNSocket=new ServerSocket(nPort);
		 port=nPort;
	}
		// TODO Auto-generated constructor stub
		@Override
		public void run() {
		while(true) {
			
			try {
				socket = serverNSocket.accept();
				
				CommandServer nThread = new CommandServer(port,socket);
				nThread.start();
				}catch(Exception e) {
			e.printStackTrace();
		}
		}
		
	}

	

}
