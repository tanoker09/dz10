package server.threads;

import server.ChatServer;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();

            String userName = reader.readLine();
            server.addUser(userName, this);

            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                if(clientMessage.getBytes()[0] == '@'){
                    Pattern pattern = Pattern.compile("@\\w+\\s");
                    Matcher matcher = pattern.matcher(clientMessage);
                    String recieverName = null;
                    while (matcher.find()) {
                        recieverName = clientMessage.substring(matcher.start()+1, matcher.end()-1);
                    }
                    serverMessage = "[" + userName + "]: " + clientMessage;
                    server.unicast(serverMessage, recieverName);
                } else{
                    serverMessage = "[" + userName + "]: " + clientMessage;
                    server.broadcast(serverMessage, this);
                }

            } while (!clientMessage.equals("quit"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Error in server.threads.UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}