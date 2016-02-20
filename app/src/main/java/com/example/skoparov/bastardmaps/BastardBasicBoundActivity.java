package com.example.skoparov.bastardmaps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

public class BastardBasicBoundActivity extends AppCompatActivity
{
    private class BastardConnection implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service)
        {
            BastardLocationUpdateService.LocalBinder binder =
                    (BastardLocationUpdateService.LocalBinder) service;

            mService = binder.getService();
            mIsBound = true;
            createServiceDependant();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            mIsBound = false;
        }
    }

    protected BastardLocationUpdateService mService;
    protected boolean mIsBound;
    protected BastardConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mConnection = new BastardConnection();

        bindToLocationUpdateService();
    }

    protected void createServiceDependant()
    {
        int i  = 0 ;
    }

    @Override
    public void onDestroy()
    {
        if (mIsBound)
        {
            unbindService(mConnection);
            mIsBound = false;
        }

        super.onDestroy();
    }

    protected void bindToLocationUpdateService()
    {
        Intent intent = new Intent(this, BastardLocationUpdateService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

//        try
//        {
//            mConnection.waitUntilConnected();
//        }
//        catch(InterruptedException e)
//        {
//            //TODO implement handling
//        }
    }
}
