package com.example.skoparov.bastardmaps;

import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BastardMapPainter
{
    public static class PainterSettings
    {
        private int pathWidth;
        private int pathColor;

        PainterSettings( int pathWidth, int pathColor )
        {
            this.pathWidth = pathWidth;
            this.pathColor = pathColor;
        }
    }

    private GoogleMap mMap;
    private PainterSettings mSettings;
    Vector<Polyline> mPath = new Vector<>();
    private LatLng mPrev;

    public BastardMapPainter( GoogleMap map, PainterSettings settings)
    {
        mMap = map;
        mSettings = settings;
    }


    public void startNewPath(Location currLoc)
    {
        Iterator<Polyline> it = mPath.iterator();

        while(it.hasNext())
        {
            it.next().remove();
        }

        mPath.clear();

        LatLng start = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());

        mPath.add(
                mMap.addPolyline((new PolylineOptions())
                        .add(start, start)
                        .width(mSettings.pathWidth)
                        .color(Color.BLUE)
                        .geodesic(true)
                        .visible(true)));


        mPrev = start;
    }

    //must contain at least one start point
    public void loadPath( List<LatLng> points )
    {
//        if(!points.isEmpty())
//        {
//            mCurrPath = mMap.addPolyline(new PolylineOptions()
//                    .add(points.get(0))
//                    .width(mSettings.pathWidth)
//                    .color(mSettings.pathColor)
//                    .geodesic(true));
//        }
    }

    public boolean setSettings( PainterSettings settings )
    {
        if( settings != null)
        {
            mSettings = settings;
            return true;
        }

        return  false;
    }

    public void newPos( Location newLocation)
    {
        if(mMap != null && mPrev != null && mPath != null )
        {
            LatLng newPoint = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

            mPath.add(
            mMap.addPolyline((new PolylineOptions())
                    .add(mPrev, newPoint)
                    .width(15)
                    .color(Color.BLUE)
                    .geodesic(true)
                    .visible(true))
            );

            mPrev = newPoint;
        }
    }


}
