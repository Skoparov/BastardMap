package com.example.skoparov.bastardmaps;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

public class BastardLocationUpdateService extends Service
{
    public class LocalBinder extends Binder
    {
        BastardLocationUpdateService getService()
        {
            return BastardLocationUpdateService.this;
        }
    }

    private BastardTracker mTracker;
    private BastardMainActivity mActivity;
    private final IBinder mBinder = new LocalBinder();
    public static boolean IS_RUNNING = false;

    public void setTracker( BastardTracker tracker )
    {
        mTracker = tracker;
    }

    public void  setActivity( BastardMainActivity a )
    {
        mActivity = a;
    }

    public BastardTracker getTracker()
    {
        return mTracker;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        IS_RUNNING = false;
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent.getAction().equals(BastardConstants.ACTION.STARTFOREGROUND_ACTION))
        {
            showNotification();
            IS_RUNNING = true;
            notifyActivity();
            Toast.makeText(this, "Location Updating Started", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(BastardConstants.ACTION.PREV_ACTION))
        {
            Toast.makeText(this, "Clicked Previous!", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(BastardConstants.ACTION.PLAY_ACTION))
        {
            Toast.makeText(this, "Clicked Play!", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(BastardConstants.ACTION.NEXT_ACTION))
        {
            Toast.makeText(this, "Clicked Next!", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(BastardConstants.ACTION.STOPFOREGROUND_ACTION))
        {
            Toast.makeText(this, "Location Updating Stopped", Toast.LENGTH_SHORT).show();
            stopForeground(true);
            IS_RUNNING = false;
            notifyActivity();
            stopSelf();
        }

        return START_STICKY;
    }

    private void showNotification()
    {
        Intent notifIntent = new Intent(this, BastardMainActivity.class);
        notifIntent.setAction(BastardConstants.ACTION.MAIN_ACTION);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

        Intent previousIntent = new Intent(this, BastardLocationUpdateService.class);
        previousIntent.setAction(BastardConstants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, BastardLocationUpdateService.class);
        playIntent.setAction(BastardConstants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, BastardLocationUpdateService.class);
        nextIntent.setAction(BastardConstants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("BastardMap Location Updater")
                .setTicker("ololo")
                .setContentText("Here be the location")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                .addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent).build();

        startForeground(BastardConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    private void notifyActivity( )
    {
        if(mActivity != null)
        {
            mActivity.onServiceStatusChanged( IS_RUNNING );
        }
    }



    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
}
