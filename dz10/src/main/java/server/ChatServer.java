package server;
import server.threads.UserThread;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private int port;
    private Map<String, UserThread> userThreads = new HashMap<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                newUser.start();

            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }

        int port = Integer.parseInt(args[0]);

        ChatServer server = new ChatServer(port);
        server.execute();
    }

    public void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads.values()) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    public void unicast(String message, String includeUser) {
        userThreads.get(includeUser).sendMessage(message);
    }

    public void addUser(String userName, UserThread userThread) {
        userThreads.put(userName, userThread);
    }

    public void removeUser(String userName, UserThread aUser) {
        userThreads.remove(userName);
        System.out.println("The user " + userName + " quitted");
    }

    public Set<String> getUserNames() {
        return this.userThreads.keySet();
    }

    public boolean hasUsers() {
        return !this.userThreads.isEmpty();
    }
}