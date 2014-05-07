package com.utopik.andremote.app;

import android.util.Log;

import java.io.PrintWriter;

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
