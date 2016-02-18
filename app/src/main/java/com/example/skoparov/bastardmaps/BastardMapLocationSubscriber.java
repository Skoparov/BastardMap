package com.example.skoparov.bastardmaps;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.common.api.ResultCallback;

public class BastardMapLocationSubscriber implements
        GoogleApiClient.ConnectionCallbacks,
        ResultCallback<LocationSettingsResult>
{
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private BastardMapEventsHandler mEventsHandler;
    private boolean mLocationRequestAdded;
    private Activity mParentActivity;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public BastardMapLocationSubscriber(BastardMapEventsHandler eventsHandler,
                                        LocationRequest request,
                                        Activity parent)
    {
        mEventsHandler = eventsHandler;
        mLocationRequestAdded = false;
        mLocationRequest = request;
        mParentActivity = parent;
    }

    public void setGoogleApiClient( GoogleApiClient googleApiClient )
    {
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        addLocationRequertSettings();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        mLocationRequestAdded = false;
        // TODO
    }

    public boolean addLocationRequertSettings()
    {
        if ( mGoogleApiClient != null &&
             mLocationRequest != null &&
             !mLocationRequestAdded )
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
                type = BastardMapLogger.EntryType.LOG_ENTRY_WARNING;
                logEntry = "Location settings additon: RESOLUTION_REQUIRED";

                try
                {
                    status.startResolutionForResult(
                            mParentActivity,
                            REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e)
                {
                    BastardMapLogger.getInstance().addEntry(
                            BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                            "startResolutionForResult has thrown an exception" );
                }

                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                logEntry = "Location settings additon: SETTINGS_CHANGE_UNAVAILABLE";
                break;
        }

        BastardMapLogger.getInstance().addEntry(type, logEntry );
    }

}
