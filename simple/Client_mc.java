
import java.io.*; 
import java.net.*; 
import java.util.Scanner; 

// Client class 
public class Client_mc 
{ 
	public static void main(String[] args) throws IOException 
	{ 
        Socket s= null;
        boolean bl_conn=true;
		try
		{ 
			Scanner scn = new Scanner(System.in); 
			
			// getting localhost ip 
			InetAddress ip = InetAddress.getByName("localhost"); 
	
			// establish the connection with server port 5056 
			s = new Socket(ip, 5056); 
	
			// obtaining input and out streams 
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
	
			// the following loop performs the exchange of 
			// information between client and client handler 
			while (bl_conn) 
			{ 
				System.out.println(dis.readUTF()); 
				String tosend = scn.nextLine(); 
                if(tosend == null)
                tosend = "Exit";
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
                if(received == null)
                {
			        System.out.println("Server crashed "); 
                    bl_conn=false;
                }
                
			} 
			
			// closing resources 
			scn.close(); 
			dis.close(); 
			dos.close(); 
        } catch(EOFException e) {
			System.out.println("Server crashed "); 
            bl_conn=false;
        } catch(SocketException e) {
			System.out.println("Server sleeping "); 
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
    }
} 

