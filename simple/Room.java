// Room object that handles room members and functionality

import java.util.List;
import java.util.LinkedList;
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
public class Room {

 List<String> roomMembers;
 String roomName;

 Room(String roomName) {
    this.roomName = roomName;
    roomMembers = new LinkedList<String>();
 }

 public boolean match(String name) {
     return name.equals(this.roomName);
 }

 public void addMember(String client) {
    roomMembers.add(client);
 }

 public void removeMember(String client) {
     roomMembers.remove(client);
 }

 public void displayRoomName(StringBuilder response)  {
        response.append(this.roomName + " ");
 }

 public void displayRoomMembers(StringBuilder response) {
         for (String m: roomMembers) {
            response.append(m + " ");
         }

 }

 public void sendMessage(String message) {
    // Broadcast to all room members

 }

}
