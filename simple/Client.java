// A Java program for a Client 
import java.net.*; 
import java.io.*; 

public class Client 
{ 
	// initialize socket and input output streams 
	private Socket socket		 = null; 
	private DataInputStream input = null; 
	private DataOutputStream out	 = null; 
     	private BufferedReader d = null;

	// constructor to put ip address and port 
	public Client(String address, int port) 
	{ 
		// establish a connection 
		try
		{ 
			socket = new Socket(address, port); 
			System.out.println("Connected"); 

			// takes input from terminal 
			//input = new DataInputStream(System.in); 
			
      			d = new BufferedReader(new InputStreamReader(System.in));

			// sends output to the socket 
			out = new DataOutputStream(socket.getOutputStream()); 
		} 
		catch(UnknownHostException u) 
		{ 
			System.out.println(u); 
		} 
		catch(IOException i) 
		{ 
			System.out.println(i); 
		} 

		// string to read message from input 
		String line = ""; 

		// keep reading until "Over" is input 
		while (!line.equals("Over")) 
		{ 
			try
			{ 
				line = d.readLine(); 
				out.writeUTF(line); 
			} 
			catch(IOException i) 
			{ 
				System.out.println(i); 
			} 
		} 

		// close the connection 
		try
		{ 
			input.close(); 
			out.close(); 
			socket.close(); 
		} 
		catch(IOException i) 
		{ 
			System.out.println(i); 
		} 
	} 

	public static void main(String args[]) 
	{ 
		//Client client = new Client("127.0.0.1", 5000); 
      try {
        InetAddress ia = InetAddress.getByName("ada.cs.pdx.edu");
        System.out.println(ia.getHostName());
        System.out.println(ia.getHostAddress());
		//Client client1 = new Client(ia.getHostAddress(), 5000); 
		Client client2 = new Client("131.252.208.103", 5000); 
      } catch (Exception ex) {
        System.err.println(ex);
      }
	} 
} 

