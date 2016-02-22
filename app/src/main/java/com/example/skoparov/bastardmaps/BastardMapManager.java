package com.example.skoparov.bastardmaps;

import android.graphics.Color;
import android.location.Location;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BastardMapManager extends SupportMapFragment
             implements
             BastardMapEventsInterface,
             OnMapReadyCallback,
             GoogleMap.OnMapLongClickListener,
             GoogleMap.OnMapClickListener,
             GoogleMap.OnMarkerClickListener

{
    public static class MapManagerPackage
    {
        public BastardLogger logger;
        public GoogleApiClient apiClient;
        public BastardLocationCollector collector;

        MapManagerPackage( BastardLogger logger,
                           GoogleApiClient apiClient,
                           BastardLocationCollector collector)
        {
            this.logger = logger;
            this.apiClient = apiClient;
            this.collector = collector;
        }
    }

    private GoogleMap mMap;
    private MapManagerPackage mP;
    private CameraPosition mCamPos;
    private BastardTrackPainter mPainter;
    private TextView mDebugView; // TODO: Remove later

    // public methods

    public void setMapMapagerPackage( MapManagerPackage p )
    {
        mP = p;
    }

    public MapManagerPackage getMapManagerPackage( )
    {
        return mP;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        //create painter
        mPainter = BastardFactory.getPainter(
                mMap,
                new BastardTrackPainter.PainterSettings( 5, Color.RED));

        tuneMap();
        restoreMap();
        mMap.setOnMapClickListener(this);

        addLogEntry(BastardLogger.EntryType.LOG_ENTRY_INFO,
                "Map ready");
    }

    @Override
    public void onPositionChanged(Location newLocation)
    {
        if(mPainter != null)
        {
            mPainter.addPoint(newLocation);
        }

        //TODO: remove the following debug info
        LatLng userCurrPos = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        addLogEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Pos: " + userCurrPos);
        mDebugView.setText("Pos: " + userCurrPos);
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
//        Location targetLocation = new Location("");//provider name is unecessary
//        targetLocation.setLatitude(latLng.latitude);//your coords of course
//        targetLocation.setLongitude(latLng.longitude);
//
//        if(mPainter != null)
//        {
//            mPainter.addPoint(targetLocation);
//        }

        //TODO smth cool
    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {
        if( mMap != null  )
        {
            mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
        }

        //TODO  smth cool
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        //TODO  smth cool
        return false;
    }

    public void startNewPath()
    {
        if(mPainter != null)
        {
            mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "MapManager : path started");
            mPainter.startNewPath(getCurrentLocation());
        }
    }

    public Location getCurrentLocation()
    {
        try
        {
            if( mP.apiClient != null )
            {
                return LocationServices.FusedLocationApi.getLastLocation(mP.apiClient);
            }
        }
        catch( SecurityException e )
        {
            addLogEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                    "Failed to get user location" );
        }

        return null;
    }

    public void setDebugView(TextView view)
    {
        mDebugView = view;
    }

    public void setMapState( BastardMapState state )
    {
        mCamPos = state.cameraPosition;
    }

    public BastardMapState getMapState()
    {
        if( mMap != null )
        {
            return new BastardMapState( mMap.getCameraPosition() );
        }

        return null;
    }

    //private methods

    private void addLogEntry( BastardLogger.EntryType type, String text )
    {
        if(mP.logger != null )
        {
            mP.logger.addEntry(type, text);
        }
    }

    private void tuneMap()
    {
        //set ui parts
        try
        {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
        }
        catch ( SecurityException e )
        {
            addLogEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                    "Failed to set up some ui");
        }
    }

    private void restoreMap()
    {
        //resstore path
        BastardTrack lastTrack = mP.collector.getTrack();
        List<LatLng> points = new ArrayList<>();

        Iterator< Location > it = lastTrack.getTrackPoints().iterator();
        while(it.hasNext())
        {
            Location l = it.next();
            points.add( new LatLng( l.getLatitude(), l.getLongitude() ) );
        }

        mPainter.loadPath(points);

        //restore cam position
        if( mCamPos != null )
        {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCamPos));
        }
    }
}