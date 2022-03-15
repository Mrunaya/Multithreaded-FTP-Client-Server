import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class FTPServer {
	

	public static 	HashMap<Integer, String> lockTable= new HashMap<Integer, String>();
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		int nPort=Integer.parseInt(args[0]);
		int tPort=Integer.parseInt(args[1]);
		System.out.println("Server started!");
		
		NormalServer nThread = new NormalServer(nPort,lockTable);
		nThread.start();
		
		TerminateServer tThread = new TerminateServer(tPort,lockTable);
		tThread.start();
		
		

	}

}
