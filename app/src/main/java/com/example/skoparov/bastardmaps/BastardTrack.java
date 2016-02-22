package com.example.skoparov.bastardmaps;


import android.location.Location;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

public class BastardTrack
{
    private Vector<Location> mTrackPoints;

    //public methods

    public BastardTrack()
    {
        mTrackPoints = new Vector<>();
    }

    public BastardTrack( Vector<Location> track )
    {
        mTrackPoints = track;
    }

    public void addPosition( Location p )
    {
        mTrackPoints.add(p);
    }

    public void clear()
    {
        mTrackPoints.clear();
    }

    public float getTrackLength()
    {
        Iterator<Location> it = mTrackPoints.iterator();
        float totalDist = 0;

        while(it.hasNext())
        {
            Location start = it.next();

            if(it.hasNext())
            {
                Location end = it.next();
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
            Location start = mTrackPoints.firstElement();
            Location end = mTrackPoints.lastElement();
            duration = end.getTime() - start.getTime();
        }

        return duration;
    }

    public float getAverageSpeed()
    {
        return getTrackLength() / getTrackDuration();
    }

    public Vector<Location> getTrackPoints()
    {
        return mTrackPoints;
    }

    public long size()
    {
        return mTrackPoints.size();
    }

    public void save(FileOutputStream out) throws IOException {
        Iterator<Location> it = mTrackPoints.iterator();

        while(it.hasNext())
        {
            Location l = it.next();
            out.write(Long.toString(l.getTime()).getBytes());
            out.write(Double.toString(l.getLatitude()).getBytes());
            out.write(Double.toString(l.getLongitude()).getBytes());
        }
    }

    // private methods

    private float distanceBetween(Location start, Location end)
    {
        float[] results = new float[1];

        Location.distanceBetween(start.getLatitude(), start.getLongitude(),
                                end.getLatitude(), end.getLongitude(),
                                results);

        return results[0];
    }
}


