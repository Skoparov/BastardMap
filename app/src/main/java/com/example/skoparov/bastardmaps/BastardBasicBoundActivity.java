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
    protected BastardConnection mConnection;
    protected boolean mIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mConnection = new BastardConnection();

        bindToLocationUpdateService();
    }

    // All instantiation that requires Service
    // to be already instantiated must be implemented here
    protected void createServiceDependant() {}

    @Override
    protected void onDestroy()
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
    }
}
