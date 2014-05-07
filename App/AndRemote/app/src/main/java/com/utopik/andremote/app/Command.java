package com.utopik.andremote.app;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;

/**
 * Created by geoffrey on 07/05/14.
 */
public class Command {

    public static PrintWriter out = null;

    public static void sendCmd(String cmd) {
            out.println(cmd);
            Log.i("Command", cmd);
    }
}
