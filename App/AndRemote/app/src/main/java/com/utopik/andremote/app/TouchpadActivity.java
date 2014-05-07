package com.utopik.andremote.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class TouchpadActivity extends Activity {

    private Thread thread;
    private String host;
    private int port;
    private static Socket socket;
    private String curMode = "touchpad"; // Used to know in which mode we are

    public static Socket getSocket() {
        return socket;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_touchpad);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        host = sharedPref.getString("prefHost", "");
        port = Integer.parseInt(sharedPref.getString("prefPort", ""));
        thread = new Thread(new ConnectionThread(host, port));
        thread.start();

        Button buttonClick = (Button) findViewById(R.id.buttonClick);
        buttonClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event ) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    clickPressed();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    clickReleased();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.touchpad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_reconnect) {
            thread.interrupt();
            thread = new Thread(new ConnectionThread(host, port));
            thread.start();
            return true;
        }

        if (id == R.id.action_accel && curMode.equals("accel")) {
            findViewById(R.id.activeSurface).setBackgroundColor(Color.parseColor("#33B5E5"));
            curMode = "touchpad";
            item.setTitle(R.string.action_accel);
            return true;
        }
        else if (id == R.id.action_accel && curMode.equals("touchpad")) {
            findViewById(R.id.activeSurface).setBackgroundColor(Color.parseColor("#AA66CC"));
            curMode = "accel";
            item.setTitle("Touchpad Mode");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ConnectionThread implements Runnable {
        private String HOST;
        private int PORT;

        ConnectionThread(String host, int port) {
            this.HOST = host;
            this.PORT = port;
        }

        @Override
        public void run() {
            try {
                final TextView textConnection = (TextView) findViewById(R.id.textConnection);
                InetAddress serverAddress = InetAddress.getByName(HOST);
                socket = new Socket(serverAddress, PORT);

                Command.out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(TouchpadActivity.getSocket().getOutputStream())), true);

                /* Update connection state*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (socket.isConnected())
                            textConnection.setText("Connected to host");
                        else textConnection.setText("Disconnected");
                    }
                });
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void altTab(View view) {
        /*  Alt+Tab */
        String cmd = "AltTab,0,0";
        Command.sendCmd(cmd);
    }

    public void clickPressed() {
        /*  Alt+Tab */
        String cmd = "MouseClickLong,0,0";
        Command.sendCmd(cmd);
    }

    public void clickReleased() {
        /*  Alt+Tab */
        String cmd = "MouseClickLongRelease,0,0";
        Command.sendCmd(cmd);
    }
}
