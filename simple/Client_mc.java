/*************************************************************************
This code is developed for IRC project 
By - Harriet Adkins and Yasodha Suriyakumar
Class - CS 494/594 Winter 2020 
One class with two threads to lister for Client, receive messages from 
    Server
************************************************************************/
import java.io.*; 
import java.net.*; 
import java.util.Scanner; 

public class Client_mc 
{ 
    static volatile boolean exit = false; 
	public static void main(String[] args) throws IOException 
	{ 
		Scanner scn = new Scanner(System.in); 
		InetAddress ip = InetAddress.getByName("localhost"); 
        Socket s    = null;
        try{
			 s = new Socket(ip, 5056); 
        } catch(SocketException e) {
			System.out.println("Server sleeping ");
        }
        if(s==null){
            System.exit(0);
        }
		DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
        DataInputStream dis = new DataInputStream(s.getInputStream()); 
        Thread sendMessage = new Thread(new Runnable()  
        { 
        @Override
        public void run() { 
		//while (true) 
		while (!exit) 
		{ 
            try
            {
				String tosend = scn.nextLine(); 
				dos.writeUTF(tosend);
				// If client sends exit break from the while loop 
				if(tosend.equals("Exit")){ 
					System.out.println("Connection closing.."); 
                    exit = true;
					break; 
				}
            } catch(IOException e) {
			        System.out.println("Server crashed in send"); 
                    e.printStackTrace(); 
            }
        }
        }} );
        // readMessage thread 
        Thread readMessage = new Thread(new Runnable()  
        { 
        @Override
        public void run() {  
		//while (true)
		while (!exit) 
		{ 
			try {
                    String received = dis.readUTF(); 
			        System.out.println(received);
                    if(received.equals("Bye")) {
                        exit=true;
                        System.exit(0);
                        break;
                    }
                } catch(IOException e) {
			        System.out.println("Server crashed in receieve" );
                    System.exit(0);
                    e.printStackTrace(); 
                }
		}
        } } );
            
        sendMessage.start(); 
        readMessage.start();  
			
		// closing resource - figure out how to do it right 
        /*
        try{
            s.close();
			scn.close(); 
			dis.close(); 
			dos.close(); 
        } catch(SocketException e) {
			System.out.println("Server sleeping ");
        } catch(IOException e) {
			System.out.println("Server crashed in close connection"); 
		}catch(Exception e){ 
			e.printStackTrace(); 
		} */
    }
} 

