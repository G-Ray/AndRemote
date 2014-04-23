package com.utopik.andremote.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;

/**
 * Created by geoffrey on 4/9/14.
 */
public class TouchpadEventView extends View {
    private Paint paint = new Paint();
    private Path path = new Path();
    float previousX = 0;
    float previousY = 0;
    private PrintWriter out;

    public TouchpadEventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(event.getX(), event.getY());
                previousX = event.getX();
                previousY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                /* Build the command */
                String cmd = "MouseMove," + Math.round(event.getX() - previousX) + "," + Math.round(event.getY() - previousY);
                //Todo: Clean that
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(TouchpadActivity.getSocket().getOutputStream())), true);
                    out.println(cmd);
                    Log.i("Command", cmd);
                    previousX = event.getX();
                    previousY = event.getY();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                path.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                path.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
}

