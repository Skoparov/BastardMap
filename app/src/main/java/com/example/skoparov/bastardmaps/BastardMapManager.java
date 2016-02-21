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

public class BastardMapManager extends SupportMapFragment
             implements
             BastardMapEventsInterface,
             OnMapReadyCallback,
             GoogleMap.OnMapLongClickListener,
             GoogleMap.OnMapClickListener,
             GoogleMap.OnMarkerClickListener

{
    private GoogleMap mMap;
    private Location mCurrLocation;
    private GoogleApiClient mApiClient;
    private TextView mTextView;
    private CameraPosition mCamPos;
    private BastardLogger mLogger;
    private BastardTrackPainter mPainter;

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        addLogEntry(BastardLogger.EntryType.LOG_ENTRY_INFO,
                "Map ready");

        if( mPainter == null)
        {
            mPainter = BastardFactory.getPainter( mMap, 6, Color.BLUE );
        }

        if (mCamPos == null) {
            mCamPos = mMap.getCameraPosition();
        }


        setMapState( new BastardMapState( mCamPos ) );
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        try
        {
            mMap.setMyLocationEnabled(true);
        }
        catch ( SecurityException e )
        {
            addLogEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                    "Failed to set up user location layer");
        }
    }

    @Override
    public void onPositionChanged(long time, Location newLocation)
    {
        mCurrLocation = mCurrLocation != null?
                newLocation : new Location( newLocation );

        if(mPainter != null)
        {
            mPainter.addPoint(newLocation);
        }

        //TODO: remove the following debug info
        LatLng userCurrPos = new LatLng(mCurrLocation.getLatitude(), mCurrLocation.getLongitude());
        addLogEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Pos: " + userCurrPos);
        mTextView.setText("Pos: " + userCurrPos);

//        if( mMap != null  )
//        {
//            mMap.addMarker(new MarkerOptions().position(me).title("You are here, you bastard!"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
//        }
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
            mLogger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "MapManager : path started");
            mPainter.startNewPath(getCurrentLocation());
        }
    }

    public void setGoogleApiClient( GoogleApiClient client )
    {
        mApiClient = client;
    }

    public BastardTrackPainter getPainter()
    {
        return mPainter;
    }

    public Location getCurrentLocation()
    {
        try
        {
            if( mCurrLocation == null && mApiClient != null )
            {
                mCurrLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            }
        }
        catch( SecurityException e )
        {
            addLogEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                    "Failed to get user location" );
        }

        return mCurrLocation;
    }

    public void setTestTextView( TextView view )
    {
        mTextView = view;
    }

    public void setLogger( BastardLogger logger )
    {
        mLogger = logger;
    }

    public void setMapPainter( BastardTrackPainter painter )
    {
        mPainter = painter;
    }

    public void setMapState( BastardMapState state )
    {
        if( mMap != null )
        {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(state.cameraPosition));
        }
        else
        {
            mCamPos = state.cameraPosition;
        }
    }

    public BastardMapState getMapState()
    {
        if( mMap != null )
        {

            mCamPos = mMap.getCameraPosition();
            return new BastardMapState( mCamPos );
        }

        return null;
    }

    private void addLogEntry( BastardLogger.EntryType type, String text )
    {
        if(mLogger != null )
        {
            mLogger.addEntry(type, text);
        }
    }
}