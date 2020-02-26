// Manage chat rooms

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class IRC {

    List<Room> chatRooms;

    IRC() {
        chatRooms = new LinkedList<Room>();
    }

    public void displayRooms(DataOutputStream dos) {
        StringBuilder response = new StringBuilder("All rooms: ");
        try {
            for(Room r: chatRooms) {
                r.displayRoomName(response);
            }
            dos.writeUTF(response.toString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void displayRoomMembers(DataOutputStream dos, String roomName) {
        StringBuilder response = new StringBuilder("Members of " + roomName + ": ");
        try {
            for (Room r: chatRooms) {
                if (r.match(roomName)) {
                    r.displayRoomMembers(response);
                }
            }
            dos.writeUTF(response.toString());
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    }

    public void createRoom(DataOutputStream dos, String roomName) {
        Room newRoom = new Room(roomName);
        chatRooms.add(newRoom);
    }

    public void addRoomMember(String client, List<String> roomNames) {
        for (String n: roomNames) {
            for (Room r: chatRooms) {
                if (r.match(n)) {
                    r.addMember(client);
                }
            }
        }
    }

    public void removeRoomMember(String client, String roomName) {
        for (Room r: chatRooms) {
            if (r.match(roomName)) {
                r.removeMember(client);
            }
        }
    }

    public void sendMessage(HashMap<String, String> messages) {

        for (Map.Entry<String, String> message : messages.entrySet()) {
            for (Room r: chatRooms) {
                if (r.match(message.getKey())) {
                    r.sendMessage(message.getValue());
                }
            }
        }
    }
}