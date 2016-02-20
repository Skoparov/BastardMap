package com.example.skoparov.bastardmaps;


import android.location.Location;

import java.util.Iterator;
import java.util.Vector;

public class BastardTrack
{
    Vector<BastardPosition> mTrackPoints;
    private float mTotalDist = 0;

    public BastardTrack( Vector<BastardPosition> track)
    {
        mTrackPoints = track;
    }

    public Vector<BastardPosition> getTrackPoints()
    {
        return mTrackPoints;
    }

    public float getTrackLength()
    {
        return mTotalDist;
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

    private void calculateTotalDist()
    {
        Iterator<BastardPosition> it = mTrackPoints.iterator();

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

    private float distanceBetween(BastardPosition start, BastardPosition end)
    {
        float[] results = new float[1];

        Location.distanceBetween(start.location.getLatitude(), start.location.getLongitude(),
                                end.location.getLatitude(), end.location.getLongitude(),
                                results);

        return results[0];
    }
}
