package com.example.skoparov.bastardmaps;

import android.location.Location;

public class BastardLocationCollector
        implements
        BastardMapEventsInterface
{

    private BastardPath mPath = new BastardPath();
    private long mStartIdleTime = 0;
    private BastardLogger mLogger;

    // public methods

    @Override
    public void onPositionChanged( Location newLocation)
    {
        mPath.addPosition(newLocation);
    }

    public void setPaused( boolean isPaused )
    {
        if(  isPaused == true) {
            mStartIdleTime = System.currentTimeMillis();
        }
        else {
            mPath.addIdleTime( System.currentTimeMillis() - mStartIdleTime );
        }
    }

    public void clear()
    {
        mPath = new BastardPath();
    }

    public BastardPath getPath()
    {
        return mPath;
    }

    public void setmLogger(BastardLogger l)
    {
        mLogger = l;
    }
}
