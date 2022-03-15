import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TerminateServer extends Thread{
	ServerSocket serverTSocket;
	HashMap<Integer,String> lockTable;
	public TerminateServer(int tsocket,HashMap<Integer, String> lockTable) throws IOException {
		// TODO Auto-generated constructor stub
		serverTSocket=new ServerSocket(tsocket);
		this.lockTable=lockTable;
	}
	// TODO Auto-generated constructor stub
	@Override
	public void run() {
		while(true) {
			Socket socket;
			try {
				socket = serverTSocket.accept();
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				String commandID = 	(String)inputStream.readObject();
				System.out.println("Terminating Command with ID : " + commandID);
				lockTable.put(Integer.parseInt(commandID),"Terminate");
				System.out.println(lockTable);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}


	}
}
