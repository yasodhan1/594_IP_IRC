/*************************************************************************
This code is developed for IRC project 
By - Harriet Adkins and Yasodha Surkyakumar
Class - CS 494/594 Winter 2020 
Two classes in here - one for Server and other to handle clients
************************************************************************/
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 

public class Server_mc 
{ 
    public static IRC chatRooms = new IRC();
    static volatile boolean serverExit = false; 
    static List<Socket> activeClients;
	public static void main(String[] args) throws IOException
	{ 
        activeClients = new LinkedList<Socket>();
		// server is listening on port 5056 
		ServerSocket ss = new ServerSocket(5056); 
		Socket s = null; 
        Scanner scn = new Scanner(System.in); 
		System.out.println("Enter Exit to disconnect Server from all clients");
        
        Thread disconnectServer = new Thread(new Runnable()
        {
        @Override
        public void run() {
            String userInput = null; 
        while (true) 
        {
            try
            {
            userInput = scn.nextLine();
            // If user sends exit, inform clients, and close this connection 
            if(userInput.equals("Exit"))
            {
                System.out.println("Server connection closing..");
                serverExit = true;
                for (Socket s: activeClients ) {
                    if(!s.isClosed())
                    {
                        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                        dos.writeUTF("Server is disconnecting");
                        dos.writeUTF("Bye");
                        s.close();
                    }
                }
                ss.close();
                System.exit(0);
            } else {
                System.out.println("Ignoring invalid input");
            }
            } catch(Exception e) {
                System.out.println("Server crashed in send");
                e.printStackTrace();
            }
            }
            }} );
        disconnectServer.start();

		while (!serverExit) 
		{ 
			try
			{ 
				// socket object to receive incoming client requests 
                if(!(ss.isClosed()))
                {
				    s = ss.accept(); 
                    activeClients.add(s);
				    System.out.println("A new client is connected : " + s); 
				    DataInputStream dis = new DataInputStream(s.getInputStream()); 
				    DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
				    System.out.println("Assigning new thread for this client"); 
				    Thread t = new ClientHandler(s, chatRooms, dis, dos ); 

				    // Invoking the start() method 
				    t.start(); 
                }
			} catch (SocketException e) {
                if(s.isClosed() && ss.isClosed()) {
                    System.out.println("No client and Server connections open");
                }
                else
                {
                    System.out.println("Socket exception in Server main");
				    e.printStackTrace();
                } 
			} catch (Exception e){ 
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
	public ClientHandler(Socket s, IRC chatRoom, DataInputStream dis, DataOutputStream dos)
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
        this.chatRoom = chatRoom;
        //this.name = "Client_" +client_id;
        this.name = s.getInetAddress().getHostAddress() + ":" + s.getPort(); 
	} 

	@Override
	public void run() 
	{ 
		String received; 
		String toreturn; 
        boolean bl_conn=true;
		Date date = new Date(); 
		while (bl_conn && (!(this.s.isClosed()))) 
		{ 
			try { 
                if((this.s.isClosed()))
                {
                    bl_conn=false;
                    System.out.println(" socket closed ");
                    break;
                }

                    System.out.println(" after socket closed ");
				// Ask user what he wants 
				dos.writeUTF( this.name + " > What do you want? ( Date | Time | Create [roomName] | DisplayRooms | \n"+ 
                              "DisplayRoomMembers [roomName] | Join [roomName] | Leave [roomName] | SendMessage [roomName] [message] .. ) \n" +
							"Type Exit to terminate connection."); 
				
				// receive the answer from client 
				received = dis.readUTF(); 
				String[] command = null; 
				command = received.trim().split("[ ,\\t]+");
				switch (command[0]) { 
                    case "Exit" :
					    System.out.println("Client " + this.name + " sends exit..."); 
                        chatRoom.removeMemberFromRooms(this.s);
					    System.out.println("Closing this connection."); 
                        dos.writeUTF("You are disconnected");
					    this.s.close(); 
					    System.out.println("Connection closed " + this.name); 
					    break; 
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
                        }
                        break;
                    case "Leave" :
                        if (command.length == 2) {
                            chatRoom.removeRoomMember(s, command[1]);
                            dos.writeUTF("Leaving chat room: " + command[1]);
                        }
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
					System.out.println(" Client "+ this.name +" crashed - Connection closed"); 
                    System.out.println(" Client closed status " + this.name + this.s.isClosed());
                    break;
			} catch (SocketException e) { 
                bl_conn=false;
                if(this.s.isClosed())
                {
                    bl_conn=false;
                    System.out.println(" Client " + this.name + "socket closed by Server");
                    break;
                }
				System.out.println(" Client crashed "+ this.name +" - Connection closed"); 
				e.printStackTrace(); 
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

