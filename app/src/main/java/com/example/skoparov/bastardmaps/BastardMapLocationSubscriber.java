package com.example.skoparov.bastardmaps;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by skoparov on 16.02.16.
 */
public class BastardMapLocationSubscriber implements
        GoogleApiClient.ConnectionCallbacks,
        ResultCallback<LocationSettingsResult>
{
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private BastardMapEventsHandler mEventsHandler;
    private boolean mLocationRequestAdded;

    public BastardMapLocationSubscriber(BastardMapEventsHandler eventsHandler,
                                        LocationRequest request)
    {
        mEventsHandler = eventsHandler;
        mLocationRequestAdded = false;
        mLocationRequest = request;
    }

    public void setGoogleApiClient( GoogleApiClient googleApiClient )
    {
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        if ( mGoogleApiClient != null ){
            addLocationRequertSettings();
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        mLocationRequestAdded = false;
        // TODO
    }

    protected boolean addLocationRequertSettings()
    {
        if ( mLocationRequest != null && !mLocationRequestAdded )
        {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            mGoogleApiClient,
                            builder.build());

            result.setResultCallback( this );
        }

        return false;
    }

    @Override
    public void onResult(LocationSettingsResult result )
    {
        Status status = result.getStatus();
        BastardMapLogger.EntryType type = BastardMapLogger.EntryType.LOG_ENTRY_ERROR;
        String logEntry = new String();

        switch (status.getStatusCode())
        {
            case LocationSettingsStatusCodes.SUCCESS:

                logEntry = "Location settings additon: SUCCESS";
                boolean noExept = true;

                try
                {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, mEventsHandler);
                }
                catch( SecurityException e )
                {
                    noExept = false;

                    BastardMapLogger.getInstance().addEntry(
                            BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                            "Request location updates has thrown an exception" );
                }

                if( noExept )
                {
                    type = BastardMapLogger.EntryType.LOG_ENTRY_INFO;
                    mLocationRequestAdded = true;
                }

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                logEntry = "Location settings additon: RESOLUTION_REQUIRED";

                //TODO: ask for permission

                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                logEntry = "Location settings additon: SETTINGS_CHANGE_UNAVAILABLE";
                break;
        }

        BastardMapLogger.getInstance().addEntry(type, logEntry );
    }

}
