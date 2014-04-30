package com.utopik;

import java.awt.*;
import java.awt.event.InputEvent;
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
                System.out.println("Command received:" + cmd);

                if(cmd.equals("CloseConnection")) {
                    bufferedReader.close();
                    socket.close();
                }
                else
                    execute_cmd(cmd);
            }
        } catch (IOException e) {
            if (socket.isClosed()) {
                System.out.println("Connection closed");
                System.out.println("Waiting for connection");
                return;
            }
            e.printStackTrace();
        }
    }

    private void execute_cmd(String cmd) {
        String delims = "[,]";
        // Splits the command's args
        String[] args = cmd.split(delims);
        MoveMouse m = new MoveMouse();

        if (args[0].equals("MouseMove")) {
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            m.move(x, y);
        }

        if (args[0].equals("MouseClickLeft")) {
            m.clickLeft();
        }

        if (args[0].equals("MouseClickRight")) {
            m.clickRight();
        }

        if (args[0].equals("MouseWheelDown")) {
            m.wheelDown();
        }

        if (args[0].equals("MouseWheelUp")) {
            m.wheelUp();
        }
    }
}

class MoveMouse {
    public MoveMouse() {
    }

    public void move(int x, int y) {
        /* Get actual mouse position */
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        int posX = (int) mousePos.getX();
        int posY = (int) mousePos.getY();

        try {
            Robot robot = new Robot();
            robot.mouseMove(posX + x, posY + y);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void clickLeft() {
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void clickRight() {
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void wheelDown() {
        try {
            Robot robot = new Robot();
            robot.mouseWheel(1);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void wheelUp() {
        try {
            Robot robot = new Robot();
            robot.mouseWheel(-1);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}