/*************************************************************************
This code is developed for IRC project 
By - Harriet Adkins and Yasodha Suriyakumar
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
            while (true) {
                try {
                    userInput = scn.nextLine();
                    // If user sends exit, inform clients, and close this connection 
                    if(userInput.equals("Exit")) {
                        System.out.println("Server connection closing..");
                        serverExit = true;
                        for (Socket s: activeClients ) {
                            if(!s.isClosed()) {
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

		while (!serverExit) { 
			try { 
				// socket object to receive incoming client requests 
                if(!(ss.isClosed())) {
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
                } else {
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
        boolean bl_print_options=true;
		while (bl_conn && (!(this.s.isClosed()))){ 
			try { 
                /* Dont think this is needed here
                if((this.s.isClosed()))
                {
                    bl_conn=false;
                    System.out.println(" socket closed ");
                    break;
                }
                */
				// Ask user what he wants 
                if(bl_print_options)
                {
				    dos.writeUTF(">> Your name is "+this.name + "\n>> Your options are: ( Date | Time | Create [roomName(,s)] | DisplayRooms | \n"+ 
                              ">> DisplayRoomMembers [roomName(,s)] | Join [roomName(,s)] | Leave [roomName(,s)] | SendMessage [roomName]:[message],[roomName]:[message]) \n" +
							">> Type Options for choices or Exit to terminate connection."); 
                    bl_print_options=false;
				}
				
				// receive the answer from client 
				received = dis.readUTF(); 
				String[] command = null; 
				command = received.trim().split("[ ,\\t]+");
				switch (command[0]) { 
                    case "Exit" :
					    System.out.println("Client " + this.name + " sends exit..."); 
                        chatRoom.removeMemberFromRooms(this.s);
					    System.out.println("Closing connection..."); 
                        dos.writeUTF("You are disconnected");
					    this.s.close(); 
					    System.out.println("Connection closed for " + this.name); 
					    break; 
					case "Date" : 
						toreturn = fordate.format(date); 
						dos.writeUTF(toreturn); 
						break; 
					case "Time" : 
						toreturn = fortime.format(date); 
						dos.writeUTF(toreturn); 
						break; 
                    case "Options":
				        dos.writeUTF(">> Your name is "+this.name + "\n>> Your options are: ( Date | Time | Create [roomName(,s)] | DisplayRooms | \n"+ 
                              ">> DisplayRoomMembers [roomName(,s)] | Join [roomName(,s)] | Leave [roomName(,s)] | SendMessage [roomName]:[message],[roomName]:[message]) \n" +
							">> Type Options for choices or Exit to terminate connection."); 
						break; 
                    case "Create" :
                        if (command.length >= 2) {
                            String roomNames = received.substring(received.indexOf(command[1]));
                            String [] splitRooms = roomNames.split(",");
                            for (int i = 0; i < splitRooms.length; i++) {
                                if(splitRooms[i].indexOf(":") == -1) {
                                    chatRoom.createRoom(dos, splitRooms[i].trim());
                                } else {
                                    dos.writeUTF(">> Invalid roomName " + splitRooms[i]+" ':' not allowed");
                                }
                            }
                        } else {
                            dos.writeUTF(">> Invalid input - Create [roomName], [roomName]");
                        }
                        break;
                    case "DisplayRooms" :
                        chatRoom.displayRooms(dos);
                        break;
                    case "DisplayRoomMembers" :
                        if (command.length >= 2) {
                            String roomNames = received.substring(received.indexOf(command[1]));
                            String [] splitRooms = roomNames.split(",");
                            for (int i = 0; i < splitRooms.length; i++) {
                                if(splitRooms[i].indexOf(":") == -1) {
                                chatRoom.displayRoomMembers(dos, splitRooms[i].trim());
                                } else {
                                    dos.writeUTF(">> Invalid roomName " + splitRooms[i]+" ':' not allowed");
                                }
                            }
                        } else {
                            dos.writeUTF(">> Invalid input - DisplayRoomMembers [roomName], [roomName]");
                        }
                        break;
                    case "Join" :
                        if (command.length >= 2) {
                            String roomNames = received.substring(received.indexOf(command[1]));
                            String [] splitRooms = roomNames.split(",");
                            for (int i = 0; i < splitRooms.length; i++) {
                                if(splitRooms[i].indexOf(":") == -1) {
                                chatRoom.joinRoom(s, splitRooms[i].trim());
                                } else {
                                    dos.writeUTF(">> Invalid roomName " + splitRooms[i]+" ':' not allowed");
                                }
                            }
                        } else {
                            dos.writeUTF(">> Invalid input - Join [roomName], [roomName]");
                        }
                        break;
                    case "Leave" :
                        if (command.length >= 2) {
                            String roomNames = received.substring(received.indexOf(command[1]));
                            String [] splitRooms = roomNames.split(",");
                            for (int i = 0; i < splitRooms.length; i++) {
                                if(splitRooms[i].indexOf(":") == -1) {
                                chatRoom.removeRoomMember(s, splitRooms[i].trim(),true);
                                } else {
                                    dos.writeUTF(">> Invalid roomName " + splitRooms[i]+" ':' not allowed");
                                }
                            }
                        } else {
                            dos.writeUTF(">> Invalid input - Leave [roomName], [roomName]");
                        }
                        break;
                    case "SendMessage" :
                            //String message = received.substring(received.indexOf(command[2]));
                            //chatRoom.sendMessage(command[1], message, this.name,this.s);
                        if ((command.length >= 2) && 
                            (received.indexOf(":") != -1)) {
                            String roomMsg = received.substring(received.indexOf(command[1]));
                            String [] splitRoomMessage = roomMsg.split(",");
                            for (int i = 0; i < splitRoomMessage.length; i++) {
                                 String [] splitMessage = splitRoomMessage[i].split(":");
                                 chatRoom.sendMessage(splitMessage[0].trim(), splitMessage[1], this.name,this.s);
                            }
                        } else {
                            dos.writeUTF(">> Invalid input - SendMessage [roomName]:[message], [roomName]:[message]..");
                        }
                        break;
					default: 
						dos.writeUTF(">> Invalid input"); 
						break; 
				} 
			} catch (EOFException e) { 
                    bl_conn=false;
					System.out.println(" Client "+ this.name +" crashed - Connection closed"); 
                    //System.out.println(" Client closed status " + this.name + this.s.isClosed());
                    break;
			} catch (SocketException e) { 
                bl_conn=false;
                if(this.s.isClosed()) {
                    System.out.println(" Client " + this.name + " socket closed by Server");
                    break;
                }
				System.out.println(" Client crashed "+ this.name +" - Connection closed"); 
				e.printStackTrace(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} 
		} 
		
		// closing resources 
		try
		{ 
			this.dis.close(); 
			this.dos.close(); 
		} catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 

