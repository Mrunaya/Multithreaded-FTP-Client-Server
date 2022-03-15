import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientT implements Runnable {
	String[] cmdVal;
	String input = "";		
		ObjectOutputStream outputStream = null;
		ObjectInputStream inputStream = null;
	Socket socket;
   String commandId;
	public ClientT(String serverName, String userCmd, int nPort) throws UnknownHostException, IOException {
		// TODO Auto-generated constructor stub
		this.input = userCmd;
		this.cmdVal = input.split(" ");
		socket = new Socket(serverName, nPort);
	}


	@Override
	public void run() {
		
	try {
		// TODO Auto-generated method stub
		outputStream = new  ObjectOutputStream(socket.getOutputStream());
		  inputStream = new ObjectInputStream(socket.getInputStream());
		  String[] cmdVal = input.split(" ", 3);
		switch(cmdVal[0]) {
	case "get": //  Get file from server->client
		
		outputStream.writeObject("get "+cmdVal[1]); 
		int commandID=(int)inputStream.readObject();
		System.out.println("Command ID is:"+commandID);
		FileOutputStream fileStreamGet = new FileOutputStream("copy".concat(cmdVal[1]));
		int length=inputStream.readInt();
		int offset=0;
		byte bGet[] = new byte[length];
		if(length<1000) {
			inputStream.read(bGet, 0, bGet.length);
		}else {
			for(offset=0; offset<=length; offset+=1000 ) {
				String state = (String)inputStream.readObject();
	             if(state.contains("terminated")) {
	            	 File file = new File("copy".concat(cmdVal[1]));
	                 file.delete();
	                 break;
	             }
				if(length-offset>1000)
					inputStream.read(bGet,offset,1000);
				else
					inputStream.read(bGet, offset, length % 1000);
			}
		}
		fileStreamGet.write(bGet, 0, bGet.length);
		//outputStream.flush();
		break;

	case "put": // Put file
		outputStream.writeObject("put "+cmdVal[1]);
		int commandId=(int) inputStream.readObject();
		System.out.println("Command ID is:"+commandId);
		File file= new File(cmdVal[1]);
		if(file.exists()) {
		FileInputStream fileStreamPut = new FileInputStream(file);
		
		length=(int)file.length();
		byte bPut[] = new byte[length];
		fileStreamPut.read(bPut, 0, bPut.length);
		outputStream.writeInt(length);
		outputStream.flush();
		
		 offset=0;
		if(length<1000) {
			outputStream.write(bPut, 0, bPut.length);
		}else {
			for(offset=0; offset<=length; offset+=1000 ) {
				String state = (String)inputStream.readObject();
				if(state.contains("terminated")) {
					System.out.println("Terminated by user!");
					break;
					}
				if(length-offset>1000) {
				outputStream.write(bPut,offset,1000);
				outputStream.flush();
				}
				else
					outputStream.write(bPut, offset, length % 1000);
			}
			
		}
		
		}else {
			System.out.println("File at the location do not exists!\n");
		}
		outputStream.flush();
		break;

	
	}
	}catch(Exception e) {
		e.printStackTrace();
	}
	}

}
