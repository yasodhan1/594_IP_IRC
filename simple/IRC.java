/*************************************************************************
This code is developed for IRC project 
By - Harriet Adkins and Yasodha Surkyakumar
Class - CS 494/594 Winter 2020 
Manage chat rooms
************************************************************************/

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
        StringBuilder response = null; 
        boolean bl_room = false;
        try {
             response = new StringBuilder(">> All rooms: ");
            for(Room r: chatRooms) {
                r.displayRoomName(response);
                bl_room = true;
            }
            if(bl_room == false)
            {
                response = new StringBuilder(">>  No rooms exist  ");
            } 
            response.setLength( response.length() - 1 );
            dos.writeUTF(response.toString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void displayRoomMembers(DataOutputStream dos, String roomName) {
        StringBuilder response =null;
        boolean bl_room = false;
        try {
            response = new StringBuilder(">> Members of " + roomName + ": ");
            for (Room r: chatRooms) {
                if (r.match(roomName)) {
                    bl_room = true;
                    r.displayRoomMembers(response);
                }
            }
            if(bl_room == false)
            {
                response = new StringBuilder(">> No such room " + roomName + "exists ");
            }
            response.setLength( response.length() - 1 );
            dos.writeUTF(response.toString());
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    }

    public void createRoom(DataOutputStream dos, String roomName) {
    boolean bl_room = true;
    String response = null;
        try {
            for (Room r: chatRooms) {
                if (r.match(roomName)) {
                    response = ">> Room with name "+ roomName +" exists already"; 
                    bl_room = false;
                }
            }
        if(bl_room==true)
        {
            Room newRoom = new Room(roomName);
            chatRooms.add(newRoom);
            response = ">> You created room - "+ roomName; 
        }
        dos.writeUTF(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinRoom(Socket client, String roomName) {
        boolean bl_room = false;
        boolean blNewMember = false;
        String retMessage = null;
        for (Room r: chatRooms) {
            if (r.match(roomName)) {
                bl_room = true;
                if(!(r.roomMembers.contains(client))){
                    blNewMember = true;
                    r.addMember(client);
                    retMessage = ">> You joined room " + roomName;
                } else {
                    retMessage = ">> You are already a member of " + roomName;
                }
                if(blNewMember ==true) {
                for (Socket s: r.roomMembers) {
                    if(s!= client) {
                    try {
                        DataOutputStream clientout = new DataOutputStream(s.getOutputStream());
                        clientout.writeUTF(" >> New client " + client.getInetAddress().getHostAddress() + ":" + client.getPort()+ " in room " +roomName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    }
                }
                }
            }
        }
        if(bl_room == false) {
                retMessage = ">> You cannot join room " + roomName +" - No such room exists";
        }
        try {
            DataOutputStream clientout = new DataOutputStream(client.getOutputStream());
            clientout.writeUTF(retMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeRoomMember(Socket client, String roomName, boolean bl_msg_flag) {
        boolean bl_room = false;
        boolean bl_member = false;
        String retMessage = null;
        for (Room r: chatRooms) {
            if (r.match(roomName)){ 
                bl_room = true;
                if (r.roomMembers.contains(client)){
                    bl_member = true;
                    retMessage = ">> You are leaving room " + roomName;
                    //System.out.println(" room name in leave " +roomName);
                    r.removeMember(client);
                }
                if((bl_room==true) && (bl_member == true))
                {
                    for (Socket s: r.roomMembers) {
                    if(s!=client) {
                    try {
                        DataOutputStream clientout = new DataOutputStream(s.getOutputStream());
                        clientout.writeUTF(" >> Client " + client.getInetAddress().getHostAddress() + ":" + client.getPort()+ " left room " +roomName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    }
                    }
                }
            }
        }
        if(bl_room == false) {
                retMessage = ">> You cannot leave room " + roomName +" - No such room exists";
        }
        try {
            DataOutputStream clientout = new DataOutputStream(client.getOutputStream());
            if(bl_msg_flag) {
                clientout.writeUTF(retMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void removeMemberFromRooms(Socket client) {
        for (Room r: chatRooms) {
            if(r.roomMembers.contains(client)){
                removeRoomMember(client, r.roomName, false);
            }
        }
    }

    public void sendMessage(String roomName, String message, String client_name, Socket client) {
        for (Room r: chatRooms) {
            //System.out.println(" room name " + roomName + "message " + message);
            if (r.match(roomName)) {
                if(r.roomMembers.contains(client)){
                    r.sendMessage(">> "+ client.getInetAddress().getHostAddress() + ":" + client.getPort() + 
                                     " in room " + roomName +" says " +message);
                } else {
                    //System.out.println(" cannot send message to room name " + roomName + "message " + message);
                    String retMessage = ">> You cannot send message to room name " + roomName + " - not a member";
                    try {
                            DataOutputStream clientout = new DataOutputStream(client.getOutputStream());
                            clientout.writeUTF(retMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
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

