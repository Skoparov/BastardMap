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

public final class BastardLocationUpdateService extends Service
{
    // Bins action to service
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

    // public methods

    public void setTracker( BastardTracker tracker )
    {
        mTracker = tracker;
    }

    public void  setActivity( BastardMainActivity a )
    {
        mActivity = a;
        mTracker.setActivity(a);
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
        String action = intent.getAction();

        if(action.equals(BastardConstants.ACTION.STARTFOREGROUND_ACTION))
        {
            showNotification();
            IS_RUNNING = true;
            notifyActivity();
            Toast.makeText(this, "Location Update Service Started", Toast.LENGTH_SHORT).show();
        }
        else if (action.equals(BastardConstants.ACTION.STOPFOREGROUND_ACTION))
        {
            Toast.makeText(this, "Location Update Service Stopped", Toast.LENGTH_SHORT).show();
            stopForeground(true);
            IS_RUNNING = false;
            notifyActivity();
            stopSelf();
        }
        else if(action.equals( BastardConstants.ACTION.STOPFOREGROUND_ACTION ) )
        {
            // TODO: Implement
        }

        return START_STICKY;
    }

    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    // private methods

    private void showNotification()
    {
        Intent notifIntent = new Intent(this, BastardMainActivity.class);
        notifIntent.setAction(BastardConstants.ACTION.MAIN_ACTION);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

//        Intent stopStartLocUpdateIntent = new Intent(this, BastardLocationUpdateService.class);
//        stopStartLocUpdateIntent.setAction(BastardConstants.ACTION.PREV_ACTION);
//        PendingIntent pStartStopIntent = PendingIntent.getService(this, 0, previousIntent, 0);

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
                .build();

        startForeground(BastardConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    private void notifyActivity( )
    {
        if(mActivity != null)
        {
            mActivity.onServiceStatusChanged( IS_RUNNING );
        }
    }
}
