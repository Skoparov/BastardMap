package com.example.skoparov.bastardmaps;

import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BastardPathPainter
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

    private static class ColorPicker
    {
        private List<Integer> mPool;

        public ColorPicker()
        {
            init();
        }

        public Integer getColor( int pos )
        {
            return mPool.get(pos % mPool.size());
        }

        private void init()
        {
            mPool = Arrays.asList(
                    Color.CYAN,
                    Color.DKGRAY,
                    Color.GRAY,
                    Color.GREEN,
                    Color.LTGRAY,
                    Color.MAGENTA,
                    Color.YELLOW);
        }
    }

    private GoogleMap mMap;
    private PainterSettings mSettings;
    private Polyline mPath;
    private ColorPicker mPicker = new ColorPicker();
    private HashMap<String, Polyline> mOtherPaths = new HashMap<>();

    public BastardPathPainter(GoogleMap map, PainterSettings settings)
    {
        mMap = map;
        mSettings = settings;

        mPath = createNewPolyline(false);
    }

    public void addPoint(Location newLocation )
    {
        addNewPointsToPath(Arrays.asList(locationToLatLng(newLocation)), mPath);
    }

    public void startNewPath(Location currLoc)
    {
        clearCurrentTrack();
        addNewPointsToPath(Arrays.asList(locationToLatLng(currLoc)), mPath);
    }

    public boolean loadCurrPath(List<LatLng> points)
    {
        //must contain at least one start point
        if( !points.isEmpty() )
        {
            clearCurrentTrack();
            addNewPointsToPath(points, mPath);
            return true;
        }

        return false;
    }

    public void clearCurrentTrack()
    {
        List< LatLng> points = mPath.getPoints();
        points.clear();
        mPath.setPoints(points);
    }

    public boolean loadOtherPath( List<LatLng> points, String name )
    {
        if( !points.isEmpty() && !containsOtherPath(name) )
        {
            Polyline newPath = createNewPolyline(true);

            addNewPointsToPath(points, newPath);

            mOtherPaths.put(name, newPath);
            return true;
        }

        return false;
    }

    public boolean containsOtherPath( String name )
    {
        return mOtherPaths.containsKey(name);
    }

    public boolean removeOtherPath(String pathName)
    {
        if( mOtherPaths.containsKey(pathName) )
        {
            mOtherPaths.get(pathName).remove();
            mOtherPaths.remove(pathName);

            return true;
        }

        return false;
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
    private void addNewPointsToPath(List<LatLng> points, Polyline path)
    {
        if( !points.isEmpty() )
        {
            List< LatLng > p = path.getPoints();
            p.addAll(points);
            path.setPoints(p);
        }
    }

    private LatLng locationToLatLng( Location l )
    {
        return new LatLng(l.getLatitude(), l.getLongitude());
    }

    private Polyline createNewPolyline( boolean isOther )
    {
        Integer color = isOther?
                mPicker.getColor(mOtherPaths.size()) : mSettings.pathColor;

        return mMap.addPolyline(
                new PolylineOptions()
                        .width(mSettings.pathWidth)
                        .color(color)
                        .geodesic(true)
                        .visible(true));
    }
}
