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

    public void joinRoom(Socket client, String roomName) {
        //for (String n: roomNames) {
            for (Room r: chatRooms) {
                if (r.match(roomName)) {
                    r.addMember(client);
                }
            }
       // }
    }

    public void removeRoomMember(Socket client, String roomName) {
        for (Room r: chatRooms) {
            if (r.match(roomName)) {
                r.removeMember(client);
            }
        }
    }

    public void sendMessage(String roomName, String message, String client_name, Socket client) {
            for (Room r: chatRooms) {
                System.out.println(" room name " + roomName + "message " + message);
                if (r.match(roomName)) {
                    if(r.roomMembers.contains(client))
                    {
                        r.sendMessage(client_name + " in " + roomName +" says " +message);
                    }
                    else
                    {
                        System.out.println(" cannot send message to room name " + roomName + "message " + message);
                        String retMessage = " You cannot send message to room name " + roomName + " - not a member";
                        try {
                        DataOutputStream clientout = new DataOutputStream(client.getOutputStream());
                        clientout.writeUTF(retMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
          //  dos.writeUTF(response.toString());
        /*
        //for (Map.Entry<String, String> message : messages.entrySet()) {
            for (Room r: chatRooms) {
                //if (r.match(message.getKey())) {
                if (r.match(message)) {
                    r.sendMessage(message);
                }
            }
       // }
       */
    }
}

