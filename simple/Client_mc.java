
import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
import java.util.NoSuchElementException;

// Client class 
public class Client_mc 
{ 
	public static void main(String[] args) throws IOException 
	{ 
    Runtime.getRuntime().addShutdownHook(new Thread() 
    {
        @Override
        public void run() 
        {
        System.out.println("Shutdown");
        }
    });
    
		try
		{ 
			Scanner scn = new Scanner(System.in); 
			
			// getting localhost ip 
			InetAddress ip = InetAddress.getByName("localhost"); 
	
			// establish the connection with server port 5056 
			Socket s = new Socket(ip, 5056); 
	
			// obtaining input and out streams 
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
	
			// the following loop performs the exchange of 
			// information between client and client handler 
			while (true) 
			{ 
				System.out.println(dis.readUTF()); 
				//try {
                    String tosend = scn.nextLine(); 
                //} catch(NoSuchElementException e) {
				 //   String tosend = "Exit";
                //} 
				dos.writeUTF(tosend); 
				
				// If client sends exit,close this connection 
				// and then break from the while loop 
				if(tosend.equals("Exit")) 
				{ 
					System.out.println("Closing this connection : " + s); 
					s.close(); 
					System.out.println("Connection closed"); 
					break; 
				} 
				
				// printing date or time as requested by client 
				String received = dis.readUTF(); 
				System.out.println(received); 
			} 
			
			// closing resources 
			scn.close(); 
			dis.close(); 
			dos.close(); 
        } catch(SocketException exception) {
			System.out.println("Server sleeping "); 
       // } catch(NoSuchElementException e) {
        //    System.out.println("Connection closed");
   //     } catch(InterruptedException e) {
    //        System.out.println("Connection closed");
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 
} 

