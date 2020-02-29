// It contains two classes : Server and ClientHandler 

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 

// Server class 
public class Server_mc 
{ 
    public static IRC chatRooms = new IRC();
    static int client_id =0;

	public static void main(String[] args) throws IOException 
	{ 
		// server is listening on port 5056 
		ServerSocket ss = new ServerSocket(5056); 
		Socket s = null; 

		while (true) 
		{ 
			try
			{ 
				// socket object to receive incoming client requests 
				s = ss.accept(); 
				
				System.out.println("A new client is connected : " + s); 
				
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
				System.out.println("Assigning new thread for this client"); 
				Thread t = new ClientHandler(s, chatRooms, dis, dos, client_id); 

				// Invoking the start() method 
				t.start(); 
                client_id++;
				
			} 
			catch (Exception e){ 
				s.close(); 
				e.printStackTrace(); 
			} 
		} 
	} 
} 

// ClientHandler class 
class ClientHandler extends Thread 
{ 
	DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
	DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
	final DataInputStream dis; 
	final DataOutputStream dos; 
    private String name;
	Socket s; 
    IRC chatRoom;
	

	// Constructor 
	public ClientHandler(Socket s, IRC chatRoom, DataInputStream dis, DataOutputStream dos, int client_id) 
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
        this.chatRoom = chatRoom;
        this.name = "Client " +client_id;
	} 

	@Override
	public void run() 
	{ 
		String received; 
		String toreturn; 
        boolean bl_conn=true;
		while (bl_conn) 
		{ 
			try { 

				// Ask user what he wants 
				dos.writeUTF("What do you want? ( Date | Time | Create [roomName] | DisplayRooms "+ 
                              "Join [roomName] | SendMessage [roomName] [message] .. ) \n" +
							"Type Exit to terminate connection."); 
				
				// receive the answer from client 
				received = dis.readUTF(); 
				String[] command = null; 
				command = received.trim().split("[ ,\\t]+");
				if(command[0].equals("Exit")) 
				{ 
					System.out.println("Client " + this.name + this.s + " sends exit..."); 
					System.out.println("Closing this connection."); 
					this.s.close(); 
					System.out.println("Connection closed"); 
					break; 
				} 
				
				// creating Date object 
				Date date = new Date(); 
			    	
				// write on output stream based on the 
				// answer from the client 
				switch (command[0]) { 
				
					case "Date" : 
						toreturn = fordate.format(date); 
						dos.writeUTF(toreturn); 
						break; 
						
					case "Time" : 
						toreturn = fortime.format(date); 
						dos.writeUTF(toreturn); 
						break; 
                    case "Create" :
                        if (command.length == 2) {
                            chatRoom.createRoom(dos, command[1]);
                            dos.writeUTF("Created chat room: " + command[1]);
                        }
                        break;
                    case "DisplayRooms" :
                        chatRoom.displayRooms(dos);
                        break;
                    case "DisplayRoomMembers" :
                        if (command.length == 2) {
                            chatRoom.displayRoomMembers(dos, command[1]);
                        }
                        break;

                    case "Join" :
                        if (command.length == 2) {
                            chatRoom.joinRoom(s, command[1]);
                            dos.writeUTF("Joined chat room: " + command[1]);
                        }
                        break;
                    case "SendMessage" :
                        if (command.length == 3) {
                            chatRoom.sendMessage(command[1], command[2], this.name,this.s);
                        }
                        break;
					default: 
						dos.writeUTF("Invalid input"); 
						break; 
				} 
			} catch (EOFException e) { 
                    bl_conn=false;
					System.out.println(" No client - Connection closed"); 
			} catch (SocketException e) { 
                    bl_conn=false;
					System.out.println(" Client crashed - Connection closed"); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} 
		} 
		
		try
		{ 
			// closing resources 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 

