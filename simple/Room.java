/*************************************************************************
This code is developed for IRC project 
By - Harriet Adkins and Yasodha Suriyakumar
Class - CS 494/594 Winter 2020 
Room object that handles room members and functionality
************************************************************************/
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

 public void removeMember(Socket client) {
     roomMembers.remove(client);
 }

 public void displayRoomName(StringBuilder response)  {
    response.append(this.roomName + ",");
 }

 public void displayRoomMembers(StringBuilder response) {
    if(roomMembers.size() ==0 ){
        response.append(" None");
     } else {
         for (Socket s: roomMembers) {
            response.append(" "+s.getInetAddress().getHostAddress() + ":" + s.getPort() + " ,");
         }
    }
 }

 public void sendMessage(String message) {
    //System.out.println(" message " + message);
    // Broadcast to all room members
    try {
        for (Socket s: roomMembers ) {
            //System.out.println( s.getInetAddress().toString() + ":" + s.getPort() + "  ");
            DataOutputStream clientout = new DataOutputStream(s.getOutputStream());
            clientout.writeUTF(message);
            //System.out.println(s.getInetAddress().toString() +" message " + message);
        }
    } catch (SocketException e) {
        e.printStackTrace(); 
    } catch (IOException e) {
        e.printStackTrace(); 
    }
 }

}
