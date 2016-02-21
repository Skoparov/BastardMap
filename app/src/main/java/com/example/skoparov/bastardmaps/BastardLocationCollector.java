package com.example.skoparov.bastardmaps;

import android.location.Location;

import java.util.Iterator;

public class BastardLocationCollector
        implements
        BastardMapEventsInterface
{

    private BastardTrack mTrack = new BastardTrack();
    private BastardLogger logger;

    // public methods

    @Override
    public void onPositionChanged(long time, Location newLocation)
    {
        mTrack.addPosition( new BastardPosition(time, newLocation) );
        printTrack();
    }

    public void clear()
    {
        mTrack.clear();
    }

    public BastardTrack getTrack()
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
                "Time = " + mTrack.getTrackDuration() / 1000 + " sec");
    }
}
