import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class CommandServer extends Thread{
	public static String SERVER_DIRECTORY = System.getProperty("user.dir"); 
	Socket nSocket;
	int commandId;
	public Map<Integer, String> lockTable;
	public CommandServer(int port, Socket socket, Map<Integer, String> LockTable) {
		nSocket=socket;
		lockTable=LockTable;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
try {
		System.out.println("Client connected!");
		ObjectInputStream inputStream = new ObjectInputStream(nSocket.getInputStream());
		ObjectOutputStream outputStream = new ObjectOutputStream(nSocket.getOutputStream());
		
	while(true) {
		
	String inputCmd = (String) inputStream.readObject();
	System.out.println("Input command is : " + inputCmd);
	
	String[] userInput = inputCmd.split(" ", 2);

	if("quit".equalsIgnoreCase(userInput[0])){
		outputStream.writeObject("quit");
		 inputStream.close();
		 outputStream.close();
		 break;
	}
	switch(userInput[0]) {
	case "get":// get file
		  commandId = lockTable.size() +1;
		 outputStream.writeInt(commandId);
		while(true) {
			boolean processingCmd=false;
			for(Map.Entry<Integer,String> entry : lockTable.entrySet()) {
				if(entry.getValue().equals("In Process for put " + userInput[1])) {
					 processingCmd = true;
				}
				
			}
			if(!processingCmd)
				break;
			Thread.sleep(10000);
			System.out.println("Client need to wait as other request is already in process");
		}
		
		
		File file= new File(SERVER_DIRECTORY+"/"+userInput[1]);
		int offset=0;
		int length=(int)file.length();
		byte bGet[] = new byte[length];
		boolean terminated=false;
		if(file.exists()) {
			lockTable.put(commandId,"In Process for get " + userInput[1]);
			FileInputStream fileInStream = new FileInputStream(file);
			fileInStream.read(bGet, 0, bGet.length);
			fileInStream.close();
			outputStream.writeInt(length);
			if(length<1000) {
				outputStream.write(bGet, 0, bGet.length);
			}else {
				for(offset=0; offset<=length; offset+=1000 ) {
					
					String state = lockTable.get(Integer.parseInt( String.valueOf(commandId)));
					if(state.contains("Terminate")) {
						outputStream.writeObject("terminated");
						break;
					}else
			               outputStream.writeObject("Still Runnuing");
					if(length-offset>1000)
						outputStream.write(bGet,offset,1000);
					else
						outputStream.write(bGet, offset, length % 1000);
					Thread.sleep(5000);
					System.out.println("sleeping");
				}
			}
			System.out.println("File has been sent!");
		}
		else{
			
			System.out.println("File do not exist!");
		}
		outputStream.flush();
		break;

	case "put":// put file
		 
		while(true) {
			boolean processingCmd=false;
			for(Map.Entry<Integer,String> entry : lockTable.entrySet()) {
				if(entry.getValue().equals("In Process for put " + userInput[1])) {
					 processingCmd = true;
				}
				
			}
			if(!processingCmd)
				break;
			Thread.sleep(10000);
			System.out.println("Client need to wait as other request is already in process");
		}
		commandId = lockTable.size() +1;
		System.out.println("command id in out"+commandId);
		outputStream.writeObject(commandId);

		System.out.println("Wrote commandif");
		lockTable.put(commandId,"In Process for put " + userInput[1]);
		FileOutputStream fileStreamPut = new FileOutputStream(SERVER_DIRECTORY+"/copy"+userInput[1]);
		
		offset=0;
		length=inputStream.readInt();
		byte bPut[] = new byte[length];
		if(length < 1000) {
			System.out.println("Sending file");
			inputStream.read(bPut, 0, bPut.length);
		}else {
			System.out.println("Sending file 2");
			for(offset=0; offset<=length; offset+=1000 ) {
				String state = lockTable.get(Integer.parseInt( String.valueOf(commandId)));
				if(state.contains("Terminate")) {
					outputStream.writeObject("terminated");
					File fileP = new File("copy".concat(userInput[1]));
	                 fileP.delete();
					break;
				}else
		               outputStream.writeObject("Still Runnuing");
				if(length-offset>1000)
					inputStream.read(bPut,offset,1000);
				else
					inputStream.read(bPut, offset, length % 1000);
				Thread.sleep(5000);
				System.out.println("Sleeping");
			}
		}
		
		fileStreamPut.write(bPut,0,bPut.length);
		fileStreamPut.flush();
		System.out.println("waiting in sleep");
		lockTable.put(commandId,"Finished for put" + userInput[1]);
		break;
		
	case "delete": //Delete File
		File f2 = new File(SERVER_DIRECTORY+"/"+userInput[1]);
		System.out.println("file to be delted:"+userInput[1]);
		if (f2.delete())
		{
			System.out.println("File deleted successful");
		}
		else
		{
			System.out.println("File did not get deleted successful");
		}
		break;
		
	case "ls": //List files

		File f1 = new File(SERVER_DIRECTORY);
		String[] s=f1.list();
		//ObjectOutputStream outputStream = new  ObjectOutputStream(socket.getOutputStream());
		outputStream.writeObject(s);
		outputStream.flush();
		
		break;

	case "cd":// change directory
		if(userInput[1].equals("..")) {
			SERVER_DIRECTORY=SERVER_DIRECTORY.substring(0, SERVER_DIRECTORY.lastIndexOf("/"));
			System.out.println("Current directory chnaged to: "+SERVER_DIRECTORY);
		}
		else{
			SERVER_DIRECTORY=SERVER_DIRECTORY.concat("/"+userInput[1]);
			System.out.println("Current directory chnaged to: "+SERVER_DIRECTORY);
		}

		break;
	case "mkdir":// make directory
		File f = new File(SERVER_DIRECTORY.concat("/"+userInput[1]));
		if (f.mkdir()) {
			System.out.println("Directory" +userInput[1]+ "created");
		}
		else {
			System.out.println("Directory cannot be created");
		}
	break;
	case "pwd":// PWD
		//ObjectOutputStream outputStream1 = new  ObjectOutputStream(socket.getOutputStream());
		outputStream.writeObject(SERVER_DIRECTORY);
		outputStream.flush();
	break;	
	
	}	

	}
	}catch(Exception e) {
		e.printStackTrace();
	}
	}
}
