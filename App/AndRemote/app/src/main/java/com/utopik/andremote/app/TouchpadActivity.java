package com.utopik.andremote.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class TouchpadActivity extends Activity {

    private Thread thread;
    private String host;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_touchpad);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        host = sharedPref.getString("prefHost", "");
        port = Integer.parseInt(sharedPref.getString("prefPort", ""));
        thread = new Thread(new ConnectionThread(host, port));
        thread.start();
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
        return super.onOptionsItemSelected(item);
    }

    public class ConnectionThread implements Runnable {
        private Socket socket;
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
}
