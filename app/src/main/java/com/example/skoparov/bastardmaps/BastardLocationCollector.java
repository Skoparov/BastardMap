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
        printTrack();
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

    // private methods

    // TODO: Remove the following debug method
    private void printTrack(  )
    {
        if(mLogger != null)
        {
            mLogger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO,
                    "Dist = " + mPath.getDistance() + " m \n" +
                            "Time = " + mPath.getDuration() / 1000 + " sec");
        }
    }
}
