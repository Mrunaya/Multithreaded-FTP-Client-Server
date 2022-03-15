import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NormalServer extends Thread{ 

	public static ServerSocket serverNSocket;
	public static Map<Integer, String> lockTable;
	static Socket socket = null;
	static int port;
	static int clientNo=0;
	public NormalServer(int nPort, HashMap<Integer, String> lockTable) throws IOException { 
		serverNSocket=new ServerSocket(nPort);
		this.port=nPort;
		this.lockTable=lockTable;
	}
	// TODO Auto-generated constructor stub
	@Override
	public void run() {
		while(true) {

			try {
				socket = serverNSocket.accept();
				clientNo++;
				CommandServer nThread = new CommandServer(port,socket,lockTable,clientNo);
				nThread.start();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

	}



}
