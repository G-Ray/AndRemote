package com.utopik;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/*****************************************
 * This client allow you to send commands to the server running on localhost
 * Interpreted commands are:
 * -CloseConnection
 * -MouseMove,x,y
 */
public class Main {

    private static final int PORT = 8080;
    private static Socket socket = null;
    private static Thread t;

    public static void main(String[] args) {

        try {
            System.out.println("Connecting...");
            socket = new Socket(InetAddress.getLocalHost(), PORT);
            System.out.println("Connection established");

            t = new Thread(new SendCommand(socket));
            t.start();
        }catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SendCommand implements Runnable {
    private Socket socket = null;

    public SendCommand(Socket s) {
        socket = s;
    }

    public void run() {
        try {
            while (true) {
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                Scanner sc = new Scanner(System.in);
                System.out.println("Votre message :");
                String message = sc.nextLine();
                if (message.equals("CloseConnection")) {
                    out.println(message);
                    out.close();
                    socket.close();
                }
                else {
                    out.println(message);
                    out.flush();
                }
            }
        }catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (IOException e) {
            if (socket.isClosed()) {
                System.out.println("Connection closed");
                return;
            }
            else
                e.printStackTrace();
        }
    }
}