package com.example.skoparov.bastardmaps;


import android.location.Location;
import android.os.Parcel;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BastardPath
{
    private Vector<Location> mPathPoints;
    private long mIdleTime = 0;

    //public methods

    public BastardPath()
    {
        mPathPoints = new Vector<>();
    }

    public BastardPath(Vector<Location> track)
    {
        mPathPoints = track;
    }

    public void addPosition( Location p )
    {
        mPathPoints.add(p);
    }

    public void addIdleTime(  long time )
    {
        mIdleTime = time;
    }

    public void clear()
    {
        mPathPoints.clear();
    }

    // in meters
    public float getDistance()
    {
        Iterator<Location> it = mPathPoints.iterator();
        float totalDist = 0;

        for( int locationPos = 0; locationPos < mPathPoints.size() - 1; ++locationPos)
        {
            Location start = mPathPoints.get(locationPos);
            Location end = mPathPoints.get(locationPos + 1);
            totalDist += distanceBetween(start, end);
        }

        return totalDist;
    }

    // in ms
    public float getDuration()
    {
        long duration = 0;

        if(mPathPoints.size() >= 2)
        {
            Location start = mPathPoints.firstElement();
            Location end = mPathPoints.lastElement();
            duration = end.getTime() - start.getTime();
        }

        duration -= mIdleTime;

        return duration;
    }

    // in meters per second
    public float getAverageSpeed()
    {
        float aveSpeed = 0;
        float duration = getDuration();

        if( duration != 0 )
        {
            aveSpeed = getDistance() / ( duration / 1000 );
        }

        return aveSpeed;
    }

    public long getIdleTime()
    {
        return mIdleTime;
    }

    public String getName()
    {
        String name = new String();
        if(!mPathPoints.isEmpty())
        {
            name = BastardConverter.timeToStr(
                    mPathPoints.firstElement().getTime());
        }

        return name;
    }

    public Vector<Location> getPoints()
    {
        return mPathPoints;
    }

    public List<LatLng> getPointsAsList()
    {
        List<LatLng> points = new ArrayList<>();

        Iterator< Location > it = mPathPoints.iterator();
        while(it.hasNext())
        {
            Location l = it.next();
            points.add( new LatLng( l.getLatitude(), l.getLongitude() ) );
        }

        return points;
    }

    public long size()
    {
        return mPathPoints.size();
    }


    // private methods

    private float distanceBetween(Location start, Location end)
    {
        return start.distanceTo(end);
    }
}


