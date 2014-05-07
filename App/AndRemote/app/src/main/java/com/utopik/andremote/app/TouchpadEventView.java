package com.utopik.andremote.app;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by geoffrey on 4/9/14.
 */
public class TouchpadEventView extends View implements OnGestureListener {
    private Paint paint = new Paint();
    private Path path = new Path();
    private float previousX = 0;
    private float previousY = 0;
    private String cmd;
    private GestureDetector gestureScanner;
    private int sensitivity = 1;
    private boolean moveable = true;

    public TouchpadEventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        gestureScanner = new GestureDetector(this);

        // Todo: Use an int array

        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        //sensitivity = Integer.parseInt(sharedPref.getString("prefSensitivity", "1"));
    }

    @Override
    public boolean onDown(MotionEvent event) {
        path.moveTo(event.getX(), event.getY());
        previousX = event.getX();
        previousY = event.getY();
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        /* Build the command
        *  Perform a right click */
        cmd = "MouseClickRight,0,0";
        Command.sendCmd(cmd);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        /* One finger */
        if (e2.getPointerCount() == 1 && moveable == true) {
            /* Build the command */
            cmd = "MouseMove," + Math.round(e2.getX() - previousX) * sensitivity + "," + Math.round(e2.getY() - previousY) * sensitivity;
            Command.sendCmd(cmd);
            path.lineTo(e2.getX(), e2.getY());
        }
        /* Two fingers, then scroll mouseWheel*/
        else if (e2.getPointerCount() == 2) {
            moveable = false;
            if (e2.getY(0) - previousY < 0) {
                cmd = "MouseWheelDown,0,0";
                Command.sendCmd(cmd);
            } else if (e2.getY(0) - previousY > 0) {
                cmd = "MouseWheelUp,0,0";
                Command.sendCmd(cmd);
            }
        }
        previousX = e2.getX();
        previousY = e2.getY();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        /* Build the command
        *  Perform a left click */
        cmd = "MouseClickLeft,0,0";
        Command.sendCmd(cmd);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            moveable = true;

        return gestureScanner.onTouchEvent(event);
    }

}

