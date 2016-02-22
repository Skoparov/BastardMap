package com.example.skoparov.bastardmaps;


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BastardPath
{
    private Vector<Location> mPathPoints;

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

    public void clear()
    {
        mPathPoints.clear();
    }

    public float getTrackLength()
    {
        Iterator<Location> it = mPathPoints.iterator();
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

    public float getPathDuration()
    {
        long duration = 0;

        if(mPathPoints.size() >= 2)
        {
            Location start = mPathPoints.firstElement();
            Location end = mPathPoints.lastElement();
            duration = end.getTime() - start.getTime();
        }

        return duration;
    }

    public float getAverageSpeed()
    {
        return getTrackLength() / getPathDuration();
    }

    public void save(FileOutputStream out) throws IOException {
        Iterator<Location> it = mPathPoints.iterator();

        while(it.hasNext())
        {
            Location l = it.next();
            out.write(Long.toString(l.getTime()).getBytes());
            out.write(Double.toString(l.getLatitude()).getBytes());
            out.write(Double.toString(l.getLongitude()).getBytes());
        }
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
        float[] results = new float[1];

        Location.distanceBetween(start.getLatitude(), start.getLongitude(),
                                end.getLatitude(), end.getLongitude(),
                                results);

        return results[0];
    }
}


