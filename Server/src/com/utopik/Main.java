package com.utopik;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PORT = 8080;
    private static ServerSocket serverSocket = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Listening on port " + serverSocket.getLocalPort());
            Thread t_acceptClients = new Thread(new Accept_clients(serverSocket));
            t_acceptClients.start();
        } catch (IOException e) {
            System.err.println("Failed to listen on port " + serverSocket.getLocalPort());
        }
    }
}

class Accept_clients implements Runnable {

    private static Socket clientSocket;
    private static ServerSocket serverSocket = null;
    private Thread t_cmd;

    public Accept_clients(ServerSocket s) {
        serverSocket = s;
    }

    public void run() {
        try {
            while (true) {
                clientSocket = serverSocket.accept(); //accept the client connection
                System.out.println("Connection established");
                // Start a new thread that handles commands
                t_cmd = new Thread(new Receive_cmd(clientSocket));
                t_cmd.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Receive_cmd implements Runnable {

    private static Socket socket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String cmd;

    public Receive_cmd(Socket s) {
        socket = s;
    }

    public void run() {
        try {
            while (true) {
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                cmd = bufferedReader.readLine();
                execute_cmd(cmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void execute_cmd(String cmd) {
        System.out.println("Message received:" + cmd);
        MoveMouse m = new MoveMouse();

        //TODO: Implement all commands
        if (cmd.equals("mouse_right")) {
            m.right();
        }
    }
}

class MoveMouse {
    public MoveMouse() {
    }

    public void right() {
        try {
            Robot robot = new Robot();
            robot.mouseMove(300, 400);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}