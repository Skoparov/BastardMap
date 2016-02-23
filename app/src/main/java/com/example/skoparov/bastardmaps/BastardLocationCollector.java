package com.example.skoparov.bastardmaps;

import android.location.Location;

public class BastardLocationCollector
        implements
        BastardMapEventsInterface
{

    private BastardPath mPath = new BastardPath();
    private BastardLogger logger;

    // public methods

    @Override
    public void onPositionChanged( Location newLocation)
    {
        mPath.addPosition(newLocation);
        printTrack();
    }

    public void clear()
    {
        mPath = new BastardPath();
    }

    public BastardPath getPath()
    {
        return mPath;
    }

    public void setLogger( BastardLogger l )
    {
        logger = l;
    }

    // private methods

    // TODO: Remove the following debug method
    private void printTrack(  )
    {
        logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO,
                "Dist = " + mPath.getDistance() + " m \n" +
                "Time = " + mPath.getDuration() / 1000 + " sec");
    }
}
