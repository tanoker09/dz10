package client.threads;

import client.ChatClient;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        Scanner console = new Scanner(System.in);

        String userName = null;

        System.out.println("\nEnter your name: ");
        userName = console.nextLine();

        client.setUserName(userName);
        writer.println(userName);

        String text;

        do {
            System.out.print("[" + userName + "]: ");
            text = console.nextLine();
            writer.println(text);

        } while (!text.equals("quit"));

        try {
            if(!socket.isClosed())
                socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}