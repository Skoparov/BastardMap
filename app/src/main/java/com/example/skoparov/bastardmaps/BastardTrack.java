package com.example.skoparov.bastardmaps;


import android.location.Location;

import java.util.Iterator;
import java.util.Vector;

public class BastardTrack
{
    private Vector<BastardPosition> mTrackPoints;

    //public methods

    public BastardTrack()
    {
        mTrackPoints = new Vector<>();
    }

    public BastardTrack( Vector<BastardPosition> track )
    {
        mTrackPoints = track;
    }

    public void addPosition( BastardPosition p )
    {
        mTrackPoints.add(p);
    }

    public void clear()
    {
        mTrackPoints.clear();
    }

    public float getTrackLength()
    {
        Iterator<BastardPosition> it = mTrackPoints.iterator();
        float totalDist = 0;

        while(it.hasNext())
        {
            BastardPosition start = it.next();

            if(it.hasNext())
            {
                BastardPosition end = it.next();
                totalDist += distanceBetween(start, end);
            }
            else{
                break;
            }
        }

        return totalDist;
    }

    public float getTrackDuration()
    {
        long duration = 0;

        if(mTrackPoints.size() >= 2)
        {
            BastardPosition start = mTrackPoints.firstElement();
            BastardPosition end = mTrackPoints.lastElement();
            duration = end.time - start.time;
        }

        return duration;
    }

    public float getAverageSpeed()
    {
        return getTrackLength() / getTrackDuration();
    }

    public Vector<BastardPosition> getTrackPoints()
    {
        return mTrackPoints;
    }

    // private methods

    private float distanceBetween(BastardPosition start, BastardPosition end)
    {
        float[] results = new float[1];

        Location.distanceBetween(start.location.getLatitude(), start.location.getLongitude(),
                                end.location.getLatitude(), end.location.getLongitude(),
                                results);

        return results[0];
    }
}
