// Room object that handles room members and functionality

import java.util.List;
import java.util.LinkedList;
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
public class Room {

 List<Socket> roomMembers;
 String roomName;

 Room(String roomName) {
    this.roomName = roomName;
    roomMembers = new LinkedList<Socket>();
 }

 public boolean match(String name) {
     return name.equals(this.roomName);
 }

 public void addMember(Socket client) {
    roomMembers.add(client);
 }

 public void removeMember(String client) {
     roomMembers.remove(client);
 }

 public void displayRoomName(StringBuilder response)  {
        response.append(this.roomName + " ");
 }

 public void displayRoomMembers(StringBuilder response) {
         for (Socket s: roomMembers) {
            response.append(s.getInetAddress().toString() + ":" + s.getPort() + "  ");
         }
 }

 public void sendMessage(String message) {
            System.out.println(" message " + message);
    // Broadcast to all room members
    try {
        for (Socket s: roomMembers ) {
            System.out.println( s.getInetAddress().toString() + ":" + s.getPort() + "  ");
            DataOutputStream clientout = new DataOutputStream(s.getOutputStream());
            clientout.writeUTF(message);
        }
    } catch (IOException e) {
        e.printStackTrace(); 
    }
 }

}
