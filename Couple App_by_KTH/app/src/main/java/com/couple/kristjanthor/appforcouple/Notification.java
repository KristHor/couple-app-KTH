package com.couple.kristjanthor.appforcouple;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.app.IntentService;
import android.widget.Toast;


//Created by kristjanthorhedinsson on 10/05/16.

public class Notification extends IntentService {

    private static final String TAG = "com.couple.kristjanthor.appforcouple";
    NotificationCompat.Builder notification;
    private static final int uniqueID = 45455;


    public Notification() {

        super("Notification");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        messageNotify();
        CheckBatteryLevel();

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        Log.i(TAG, "The Notification Service is started");

    }


    public void build(String input) {

        String in = input.toString();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notification.setSmallIcon(R.drawable.logo);
        notification.setSound(uri);
        notification.setTicker("New message");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("New Message from CoupleApp");
        notification.setContentText(in);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            BatteryLevel();
        }
    };

    public void CheckBatteryLevel(){

        Runnable r = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 10000;
                while (System.currentTimeMillis() < futureTime){
                    synchronized (this){
                        try {
                            wait(futureTime-System.currentTimeMillis());
                        }catch (Exception e){}
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };
Thread batteryThread = new Thread(r);
        batteryThread.start();

    }

    public void BatteryLevel() {

        registerReceiver(this.batteryInformationReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    BroadcastReceiver batteryInformationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            if (level  < 50) {
                build("Your battery level is: " + Integer.toString(level));
            }
        }
    };


    public void messageNotify() {

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("event"));

    }

    // handler for received Intents. This will be called whenever an Intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String Input = intent.getStringExtra("InputMessage");

            try {
            if(Input != null){
                build(Input);

          } }
                catch (Exception e)

                {
                Toast.makeText(getApplicationContext(), "There is error in notification class",Toast.LENGTH_LONG).show();
                }
return;
        }

    };

}

