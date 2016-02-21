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

public class BastardLocationSubscriber implements
        GoogleApiClient.ConnectionCallbacks,
        ResultCallback<LocationSettingsResult>
{
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private BastardMapEventsHandler mEventsHandler;
    private BastardLogger mLogger;
    private boolean mLocationRequestAdded;
    private Activity mParentActivity;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public BastardLocationSubscriber(BastardMapEventsHandler eventsHandler,
                                     LocationRequest request,
                                     Activity parent,
                                     BastardLogger logger)
    {
        mEventsHandler = eventsHandler;
        mLocationRequestAdded = false;
        mLocationRequest = request;
        mParentActivity = parent;
        mLogger = logger;
    }

    public void setGoogleApiClient( GoogleApiClient googleApiClient )
    {
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        addLocationRequestSettings();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        mLocationRequestAdded = false;
        // TODO
    }

    public boolean addLocationRequestSettings()
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
        BastardLogger.EntryType type = BastardLogger.EntryType.LOG_ENTRY_ERROR;
        String logEntry = new String();

        switch (status.getStatusCode())
        {
            case LocationSettingsStatusCodes.SUCCESS:
            {
                logEntry = "Location settings additon: SUCCESS";
                if (subscribeEventHandler()) {
                    mLocationRequestAdded = true;

                    addLogEntry(BastardLogger.EntryType.LOG_ENTRY_INFO,
                            "Subscription to location updates: SUCCESS");
                } else {
                    addLogEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                            "Request location updates func has thrown an exception");

                }

                break;
            }
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            {
                type = BastardLogger.EntryType.LOG_ENTRY_WARNING;
                logEntry = "Location settings additon: RESOLUTION_REQUIRED";

                try {
                    status.startResolutionForResult(
                            mParentActivity,
                            REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    addLogEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                            "startResolutionForResult has thrown an exception");
                }

                break;
            }
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            {
                logEntry = "Location settings additon: SETTINGS_CHANGE_UNAVAILABLE";
                break;
            }
        }

        addLogEntry(type, logEntry );
    }

    public boolean subscribeEventHandler()
    {
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    mEventsHandler);
        }
        catch( SecurityException e )
        {
            return false;
        }

        return true;
    }

    private void addLogEntry( BastardLogger.EntryType type, String text )
    {
        if(mLogger != null )
        {
            mLogger.addEntry(type, text);
        }
    }
}
