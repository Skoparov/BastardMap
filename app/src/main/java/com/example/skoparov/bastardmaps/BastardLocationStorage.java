package com.example.skoparov.bastardmaps;

import android.location.Location;

import java.util.Iterator;
import java.util.Vector;

public class BastardLocationStorage
        implements
        BastardMapEventsInterface
{

    private Vector< BastardPosition > mPositions = new Vector<>();
    private float mTotalDist = 0;
    private BastardMapLogger logger;

    @Override
    public void onConnectionReady() {}

    public void setLogger( BastardMapLogger l )
    {
        logger = l;
    }

    @Override
    public void onPositionChanged(long time, Location newLocation)
    {
        mPositions.add(new BastardPosition(time, newLocation));
        printTrack();

    }
    public Vector<BastardPosition> getPositions()
    {
        return mPositions;
    }

    public void clearPositions()
    {
        mPositions.clear();
    }

    ///////////////////

    public float getTrackLength()
    {
        return mTotalDist;
    }

    public float getTrackDuration()
    {
        long duration = 0;

        if(mPositions.size() >= 2)
        {
            BastardPosition start = mPositions.firstElement();
            BastardPosition end = mPositions.lastElement();
            duration = end.time - start.time;
        }

        return duration;
    }

    public float getAverageSpeed()
    {
        return getTrackLength() / (getTrackDuration() / 1000);
    }

    private void calculateTotalDist()
    {
        Iterator<BastardPosition> it = mPositions.iterator();
        mTotalDist = 0;

        while(it.hasNext())
        {
            BastardPosition start = it.next();

            if(it.hasNext())
            {
                BastardPosition end = it.next();
                mTotalDist += distanceBetween(start, end);
            }
            else{
                break;
            }
        }
    }

    private double distanceBetween(BastardPosition start, BastardPosition end)
    {
        return distFrom(start.location.getLatitude(), start.location.getLongitude(),
                end.location.getLatitude(), end.location.getLongitude());
    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private void printTrack(  )
    {
        logger.addEntry(BastardMapLogger.EntryType.LOG_ENTRY_INFO, "Dist = " + getTrackDuration() + " m");
    }
}
