package com.example.skoparov.bastardmaps;

import android.location.Location;

public class BastardLocationCollector
        implements
        BastardMapEventsInterface
{

    private BastardPath mTrack = new BastardPath();
    private BastardLogger logger;

    // public methods

    @Override
    public void onPositionChanged( Location newLocation)
    {
        mTrack.addPosition( newLocation );
        printTrack();
    }

    public void clear()
    {
        mTrack = new BastardPath();
    }

    public BastardPath getTrack()
    {
        return mTrack;
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
                "Dist = " + mTrack.getTrackLength() + " m \n" +
                "Time = " + mTrack.getPathDuration() / 1000 + " sec");
    }
}
