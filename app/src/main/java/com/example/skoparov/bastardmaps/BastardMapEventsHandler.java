package com.example.skoparov.bastardmaps;

import android.os.Bundle;
import android.location.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;

public class BastardMapEventsHandler implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener

{
    private BastardMapEventsInterface mEventsInterface;

    public BastardMapEventsHandler(BastardMapEventsInterface eventsIe )
    {
        mEventsInterface = eventsIe;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        mEventsInterface.onConnectionReady();
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        //TODO
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        //TODO
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mEventsInterface.onPositionChanged( location );
    }
}
