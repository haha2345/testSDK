package com.example.cbnosdk.utiles;

import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/8/17.
 */
public class Timeutils {
    private static String  TAG = "<<<";
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = null;
    private static long count = 0;
    private boolean isPause = false;
    private static int delay = 1000;  //1s
    private static int period = 1000;  //1s
    private static final int UPDATE_TEXTVIEW = 0;
    TextView mTextView;
    public Timeutils(TextView mTextView){
        this.mTextView=mTextView;
            mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXTVIEW:
                    updateTextView();
                    break;
                default:
                    break;
            }
        }
    };
    }
public void puseTimer(){
    isPause = !isPause;
}

    private void updateTextView(){
        int i = 1000;
        long time= count * i;
        CharSequence sysTimeStr = DateFormat.format("mm:ss", time);
       mTextView.setText(String.valueOf(sysTimeStr));
    }
    public void startTimer(){
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "count: "+String.valueOf(count));
                    sendMessage(UPDATE_TEXTVIEW);

                    do {
                        try {
                            Log.i(TAG, "sleep(1000)...");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } while (isPause);

                    count ++;
                }
            };
        }

        if(mTimer != null && mTimerTask != null )
            mTimer.schedule(mTimerTask, delay, period);

    }

    public void stopTimer(){

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        count = 0;

    }
    public void sendMessage(int id){
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }
}