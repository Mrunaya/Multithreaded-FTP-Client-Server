import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TerminateServer extends Thread{
	ServerSocket serverNSocket;
	public TerminateServer(int tsocket) throws IOException {
		// TODO Auto-generated constructor stub
		serverNSocket=new ServerSocket(tsocket);
		}
			// TODO Auto-generated constructor stub
			@Override
			public void run() {
			while(true) {
				Socket socket;
				try {
					socket = serverNSocket.accept();
				}catch(Exception e) {
					
				}
	}

	
}
	}
