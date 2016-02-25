package com.example.skoparov.bastardmaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.os.StrictMode;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
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

    private final double degreesPerRadian = 180.0 / Math.PI;

    private GoogleMap mMap;
    private PainterSettings mSettings;
    private Polyline mPath;
    private ColorPicker mPicker = new ColorPicker();
    private HashMap<String, Polyline> mOtherPaths = new HashMap<>();
    private HashMap< LatLng, Marker> mShownPointsOfInterest = new HashMap<>();
    private BastardPointOfInterest mNextPoint;
    private Marker mArrow;

    public BastardPathPainter(GoogleMap map, PainterSettings settings)
    {
        mMap = map;
        mSettings = settings;
        mPath = createNewPolyline(false);
    }

    public void addPoint(Location newLocation)
    {
        addNewPointsToPath(Arrays.asList(locationToLatLng(newLocation)), mPath);
    }

    public void addShownPointOfInterest( BastardPointOfInterest p )
    {
        Marker newPoint = mMap.addMarker(new MarkerOptions()
                .position(p.pos)
                .title(p.name)
                .snippet(p.descr)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mShownPointsOfInterest.put(p.pos, newPoint);
    }

    public void pointTaken( LatLng coord, BastardPointOfInterest nextPoint )
    {
        Marker m = mShownPointsOfInterest.get(coord);
        String title = m.getTitle();
        String descr = m.getSnippet();
        mShownPointsOfInterest.remove(coord);

        Marker newPointMarker = mMap.addMarker(new MarkerOptions()
                .position(coord)
                .title(title)
                .snippet(descr)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mShownPointsOfInterest.put(coord, newPointMarker);

        mNextPoint = nextPoint;
    }

    public void startNewPath( Location currLoc, BastardPointOfInterest nextPoint )
    {
        clearCurrentPath();
        addNewPointsToPath(Arrays.asList(locationToLatLng(currLoc)), mPath);
        mNextPoint = nextPoint;
    }

    public boolean loadCurrPath(List<LatLng> points)
    {
        //must contain at least one start point
        if( !points.isEmpty() )
        {
            clearCurrentPath();
            addNewPointsToPath(points, mPath);
            return true;
        }

        return false;
    }

    public void clearCurrentPath()
    {
        List< LatLng> points = mPath.getPoints();
        points.clear();
        mPath.setPoints(points);

        for( int point = 0;  point < mShownPointsOfInterest.size(); ++point)
        {
            mShownPointsOfInterest.get(point).remove();
        }

        mShownPointsOfInterest.clear();
        mNextPoint = null;
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

    private void drawArrow( LatLng from, LatLng to )
    {
//        PolylineOptions polylines = new PolylineOptions();
//
//        LatLng from = new LatLng(f.getLatitude(), f.getLongitude());
//        LatLng to = new LatLng(t.getLatitude(), t.getLongitude());
//        polylines.add(from, to).color(polyColor).width(2);
//
//        mMap.addPolyline(polylines);

        drawArrowHead( from, to);
    }

    private void drawArrowHead( LatLng from, LatLng to)
    {
        // obtain the bearing between the last two points
        double bearing = GetBearing(from, to);

        // round it to a multiple of 3 and cast out 120s
        double adjBearing = Math.round(bearing / 3) * 3;
        while (adjBearing >= 120) {
            adjBearing -= 120;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get the corresponding triangle marker from Google
        URL url;
        Bitmap image = null;

        try {
            url = new URL("http://www.google.com/intl/en_ALL/mapfiles/dir_" + String.valueOf((int)adjBearing) + ".png");
            try {
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (image != null){

            // Anchor is ratio in range [0..1] so value of 0.5 on x and y will center the marker image on the lat/long
            float anchorX = 0.5f;
            float anchorY = 0.5f;

            int offsetX = 0;
            int offsetY = 0;

            // images are 24px x 24px
            // so transformed image will be 48px x 48px

            //315 range -- 22.5 either side of 315
            if (bearing >= 292.5 && bearing < 335.5){
                offsetX = 24;
                offsetY = 24;
            }
            //270 range
            else if (bearing >= 247.5 && bearing < 292.5){
                offsetX = 24;
                offsetY = 12;
            }
            //225 range
            else if (bearing >= 202.5 && bearing < 247.5){
                offsetX = 24;
                offsetY = 0;
            }
            //180 range
            else if (bearing >= 157.5 && bearing < 202.5){
                offsetX = 12;
                offsetY = 0;
            }
            //135 range
            else if (bearing >= 112.5 && bearing < 157.5){
                offsetX = 0;
                offsetY = 0;
            }
            //90 range
            else if (bearing >= 67.5 && bearing < 112.5){
                offsetX = 0;
                offsetY = 12;
            }
            //45 range
            else if (bearing >= 22.5 && bearing < 67.5){
                offsetX = 0;
                offsetY = 24;
            }
            //0 range - 335.5 - 22.5
            else {
                offsetX = 12;
                offsetY = 24;
            }

            Bitmap wideBmp;
            Canvas wideBmpCanvas;
            Rect src, dest;

            // Create larger bitmap 4 times the size of arrow head image
            wideBmp = Bitmap.createBitmap(image.getWidth() * 2, image.getHeight() * 2, image.getConfig());

            wideBmpCanvas = new Canvas(wideBmp);

            src = new Rect(0, 0, image.getWidth(), image.getHeight());
            dest = new Rect(src);
            dest.offset(offsetX, offsetY);

            wideBmpCanvas.drawBitmap(image, src, dest, null);

            mArrow = mMap.addMarker(new MarkerOptions()
                    .position(to)
                    .icon(BitmapDescriptorFactory.fromBitmap(wideBmp))
                    .anchor(anchorX, anchorY));


        }
    }

    private double GetBearing(LatLng from, LatLng to)
    {
        double lat1 = from.latitude * Math.PI / 180.0;
        double lon1 = from.longitude * Math.PI / 180.0;
        double lat2 = to.latitude * Math.PI / 180.0;
        double lon2 = to.longitude * Math.PI / 180.0;

        // Compute the angle.
        double angle = - Math.atan2( Math.sin( lon1 - lon2 ) * Math.cos( lat2 ),
                Math.cos( lat1 ) * Math.sin( lat2 ) - Math.sin( lat1 ) * Math.cos( lat2 ) * Math.cos( lon1 - lon2 ) );

        if (angle < 0.0)
            angle += Math.PI * 2.0;

        // And convert result to degrees.
        angle = angle * degreesPerRadian;

        return angle;
    }
}
