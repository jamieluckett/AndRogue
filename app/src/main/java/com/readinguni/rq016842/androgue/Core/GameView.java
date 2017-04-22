package com.readinguni.rq016842.androgue.Core;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private volatile GameThread thread;

    //private SensorEventListener sensorAccelerometer;

    //Handle communication from the GameThread to the View/Activity Thread
    private Handler mHandler;
    //Pointers to the views
    private TextView mScoreView;
    private TextView mHPView;
    private TextView mLVLView;
    private TextView mEXPView;


    private TextView mStatusView;

    Sensor accelerometer;
    Sensor magnetometer;

    public GameView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        //Get the holder of the screen and register interest
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        //Set up a handler for messages from GameThread
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message m) {
                if (m.getData().getBoolean("score")) {
                    mScoreView.setText(m.getData().getString("text"));
                } else if (m.getData().getBoolean("hp")) {
                    mHPView.setText(m.getData().getString("text"));
                } else if (m.getData().getBoolean("lvl")) {
                    mLVLView.setText(m.getData().getString("text"));
                } else if (m.getData().getBoolean("exp")) {
                    mEXPView.setText(m.getData().getString("text"));
                } else if (m.getData().getBoolean("end")) {
                    cleanup();
                    Intent i = context.getPackageManager()
                            .getLaunchIntentForPackage(context.getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } else {
                    //So it is a status
                    int i = m.getData().getInt("viz");
                    switch (i) {
                        case View.VISIBLE:
                            mStatusView.setVisibility(View.VISIBLE);
                            break;
                        case View.INVISIBLE:
                            mStatusView.setVisibility(View.INVISIBLE);
                            break;
                        case View.GONE:
                            mStatusView.setVisibility(View.GONE);
                            break;
                    }
                    mStatusView.setText(m.getData().getString("text"));
                }
            }
        };
    }

    /**
     * Releases any used resources
     */
    public void cleanup() {
        this.thread.setRunning(false);
        this.thread.cleanup();
        this.removeCallbacks(thread);
        thread = null;
        this.setOnTouchListener(null);
        SurfaceHolder holder = getHolder();
        holder.removeCallback(this);
    }
    /*
	 * Setters and Getters
	 */

    /**
     * @param newThread
     */
    public void setThread(GameThread newThread) {
        thread = newThread;
        setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return thread != null && thread.onTouch(event);
            }
        });
        setClickable(true);
        setFocusable(true);
    }

    public GameThread getThread() {
        return thread;
    }

    public TextView getStatusView() {
        return mStatusView;
    }

    public void setStatusView(TextView mStatusView) {
        this.mStatusView = mStatusView;
    }

    public TextView getScoreView() {
        return mScoreView;
    }

    public void setScoreView(TextView mScoreView) {
        this.mScoreView = mScoreView;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

	/*
	 * Screen functions
	 */

    //ensure that we go into pause state if we go out of focus
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (thread != null) {
            if (!hasWindowFocus)
                thread.pause();
        }
    }

    /**
     * Checks if thread is null and makes a new one, or starts the existing one
     */
    public void surfaceCreated(SurfaceHolder holder) {
        if (thread != null) {
            thread.setRunning(true);

            if (thread.getState() == Thread.State.NEW) {
                //Just start the new thread
                thread.start();
            } else {
                if (thread.getState() == Thread.State.TERMINATED) {
                    //Start a new thread
                    //Should be this to update screen with old game: new GameThread(this, thread);
                    //The method should set all fields in new thread to the value of old thread's fields
                    thread = new TheGame(this);
                    thread.setRunning(true);
                    thread.start();
                }
            }
        }
    }

    //Always called once after surfaceCreated. Tell the GameThread the actual size
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (thread != null) {
            thread.setSurfaceSize(width, height);
        }
    }

    /*
     * Need to stop the GameThread if the surface is destroyed
     * Remember this doesn't need to happen when app is paused on even stopped.
     */
    public void surfaceDestroyed(SurfaceHolder arg0) {
        boolean retry = true;
        if (thread != null) {
            thread.setRunning(false);
        }

        //join the thread with this thread
        while (retry) {
            try {
                if (thread != null) {
                    thread.join();
                }
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void setHPView(TextView HPView) {
        this.mHPView = HPView;
    }

    public void setEXPView(TextView EXPView) {
        this.mEXPView = EXPView;
    }

    public void setLVLView(TextView LVLView) {
        this.mLVLView = LVLView;
    }
}
