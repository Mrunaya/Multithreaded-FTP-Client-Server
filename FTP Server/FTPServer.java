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

public class FTPServer {
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		int nPort=Integer.parseInt(args[0]);
		int tPort=Integer.parseInt(args[1]);
		System.out.println("Server thread starting...");
		
		NormalServer nThread = new NormalServer(nPort);
		nThread.start();
		
		TerminateServer tThread = new TerminateServer(tPort);
		tThread.start();
		
		

	}

}