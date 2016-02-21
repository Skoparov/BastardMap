package com.example.skoparov.bastardmaps;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;

public class BastardTrackPainter
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
    private Polyline mTrack;

    public BastardTrackPainter(GoogleMap map, PainterSettings settings)
    {
        mMap = map;
        mSettings = settings;

        mTrack = mMap.addPolyline(
                new PolylineOptions()
                        .width(mSettings.pathWidth)
                        .color(mSettings.pathColor)
                        .geodesic(true)
                        .visible(true));
    }

    public void startNewPath(Location currLoc)
    {
        clearCurrentTrack();
        addNewPoints( Arrays.asList( locationToLatLng( currLoc ) ) );
    }

    public boolean loadPath( List<LatLng> points )
    {
        //must contain at least one start point

        if( !points.isEmpty() )
        {
            clearCurrentTrack();
            addNewPoints(points);
            return true;
        }

        return false;
    }

    public void addPoint(Location newLocation)
    {
        addNewPoints(Arrays.asList(locationToLatLng(newLocation)));
    }

    public void clearCurrentTrack()
    {
        List< LatLng> points = mTrack.getPoints();
        points.clear();
        mTrack.setPoints( points );
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

    // private methods
    private void addNewPoints( List<LatLng> points )
    {
        if( !points.isEmpty() )
        {
            List< LatLng > p = mTrack.getPoints();
            p.addAll(points);
            mTrack.setPoints(p);
        }
    }

    private LatLng locationToLatLng( Location l )
    {
        return new LatLng(l.getLatitude(), l.getLongitude());
    }
}
