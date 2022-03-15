import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class FTPClient {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String currentWorkingDirectory = "myftp>";
		String serverName=args[0];
		int nPort=Integer.parseInt(args[1]);
		int tPort=Integer.parseInt(args[2]);
		
		Socket nsocket = new Socket(serverName,nPort);
		String userCmd = "";
		ObjectOutputStream outputStream = new  ObjectOutputStream(nsocket.getOutputStream());
		ObjectInputStream inputStream = new ObjectInputStream(nsocket.getInputStream());
		
		
		
		try  {
			Scanner sc = new Scanner(System.in);
			while(!userCmd.equalsIgnoreCase("8")) {
				System.out.print(currentWorkingDirectory);
				//USER COMMANDS BEGIN FROM HERE----->
				userCmd = sc.nextLine();
				String[] cmdVal = userCmd.split(" ", 2);
				
				
				switch (cmdVal[0]) {
				case "get": //  Get file from server->client
					outputStream.writeObject("get "+cmdVal[1]); 
					int commandID = inputStream.readInt();
					FileOutputStream fileStreamGet = new FileOutputStream("copy".concat(cmdVal[1]));
					int length=inputStream.readInt();
					int offset=0;
					byte bGet[] = new byte[length];
					if(length<1000) {
						inputStream.read(bGet, 0, bGet.length);
					}else {
						for(offset=0; offset<=length; offset+=1000 ) {
							String state = (String)inputStream.readObject();
				             if(state.contains("Terminated")) {
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
					
					
					File file = new File(cmdVal[1]);
					if(file.exists()) {
						FileInputStream fileStreamPut = new FileInputStream(file);
						outputStream.writeObject("put "+cmdVal[1]);
						commandID = (int) inputStream.readObject();
						length = (int)file.length();
						byte bPut[] = new byte[length];
						fileStreamPut.read(bPut, 0, bPut.length);
						outputStream.writeInt(length);
						outputStream.flush();
						
						offset=0;
					if(length<1000) {
						System.out.println("hello");
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

				
				case "get&":
				case "put&": ClientT runnable = new ClientT(serverName,userCmd,nPort);
				Thread thread = new Thread(runnable,"thread");
				thread.start();
				Thread.sleep(200);
				break;
				case "delete": // Delete file
					
					outputStream.writeObject("delete "+cmdVal[1]);
					outputStream.flush();
					break;

				case "ls": // List files
					//String list1 =sc.nextLine();
					outputStream.writeObject("ls");
					//outputStream.flush();
					String[] list = (String[])inputStream.readObject();
					for(String s1:list)
					{
						System.out.println(s1);
					}
					System.out.print("\n");
					break;

				case "cd": // Change directory
					
					outputStream.writeObject("cd "+cmdVal[1]);
					//outputStream.flush();
					break;

				case "mkdir": // Make directory
					
					outputStream.writeObject("mkdir "+cmdVal[1]);
					//outputStream.flush();
					break;
				case "pwd": //PWD
					outputStream.writeObject("pwd ");
					outputStream.flush();
					String path = (String)inputStream.readObject();
					System.out.println(path);
					
					break;
				case "terminate":
					Socket tSocket = new Socket(serverName,tPort);
					ObjectOutputStream outputStreamT = new  ObjectOutputStream(tSocket.getOutputStream());
					outputStreamT.writeObject(cmdVal[1]);
					
					break;
				case "quit": //Exit
					outputStream.writeObject("quit ");
					//outputStream.flush();
					String msg = (String)inputStream.readObject();
					if(msg.equals("quit")) {
					System.out.println("Client Disconnected!\n");
					}
					inputStream.close();
					nsocket.close();
					return;
				

				default:
					System.out.println("Invalid Command");
					break;
				}		
			}
	
		}catch(Exception e) {e.printStackTrace();}
	}
}