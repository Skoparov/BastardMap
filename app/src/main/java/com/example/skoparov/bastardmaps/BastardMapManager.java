package com.example.skoparov.bastardmaps;

import android.location.Location;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by skoparov on 15.02.16.
 */
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

    public void setGoogleApiClient( GoogleApiClient client )
    {
        mApiClient = client;
    }

    public void setTestTextView( TextView view )
    {
        mTextView = view;
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
            BastardMapLogger.getInstance().addEntry(
                    BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                    "Failed to get user location" );
        }

        return mCurrLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        BastardMapLogger.getInstance().addEntry(
                BastardMapLogger.EntryType.LOG_ENTRY_INFO,
                "Map ready" );

        try
        {
            mMap.setMyLocationEnabled(true);
        }
        catch ( SecurityException e )
        {
            BastardMapLogger.getInstance().addEntry(
                    BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                    "Failed to set up user location layer" );
        }
    }

    @Override
    public void onPositionChanged(Location newLocation)
    {
        if( mCurrLocation != null ){
            mCurrLocation = newLocation;
        }
        else{
            mCurrLocation = new Location( newLocation );
        }

        LatLng userCurrPos = new LatLng(mCurrLocation.getLatitude(), mCurrLocation.getLongitude());
        BastardMapLogger.getInstance().addEntry(
                BastardMapLogger.EntryType.LOG_ENTRY_INFO,
                "Coord update: " + userCurrPos);

        mTextView.setText("Pos upd: " + userCurrPos);

        if( mMap != null  )
        {
            //mMap.addMarker(new MarkerOptions().position(me).title("You are here, you bastard!"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
        }
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
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

    @Override
    public void onConnectionReady()
    {
        //TODO  smth cool
    }
}