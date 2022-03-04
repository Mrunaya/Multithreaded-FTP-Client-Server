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
		int nport=Integer.parseInt(args[1]);
		int tport=Integer.parseInt(args[2]);
		
		Socket nsocket = new Socket(serverName,nport);
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

					FileOutputStream fileStreamGet = new FileOutputStream(cmdVal[1]);
					byte bGet[] = new byte[1000];
					inputStream.read(bGet, 0, bGet.length);
					fileStreamGet.write(bGet, 0, bGet.length);
					//outputStream.flush();
					break;

				case "put": // Put file
					
					
					File file= new File(cmdVal[1]);
					if(file.exists()) {
					FileInputStream fileStreamPut = new FileInputStream(file);
					outputStream.writeObject("put "+cmdVal[1]);
					byte bPut[] = new byte[1000];
					fileStreamPut.read(bPut, 0, bPut.length);
					outputStream.write(bPut, 0, bPut.length);
					outputStream.flush();
					}else {
						System.out.println("File at the location do not exists!\n");
					}
					//outputStream.flush();
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
					Socket tSocket = new Socket(serverName,tport);
					ObjectOutputStream outputStreamT = new  ObjectOutputStream(tSocket.getOutputStream());
					ObjectInputStream inputStreamT = new ObjectInputStream(tSocket.getInputStream());
					
					
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
	
		}catch(Exception e) {}
	}
}