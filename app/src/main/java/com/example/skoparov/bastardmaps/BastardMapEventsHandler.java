package com.example.skoparov.bastardmaps;

import android.os.Bundle;
import android.location.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Iterator;

public class BastardMapEventsHandler implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener

{
    private ArrayList< BastardMapEventsInterface > mInterfaces = new ArrayList<>();
    private boolean mBlock = true;

    @Override
    public void onConnected(Bundle bundle)
    {

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
        if( !mBlock )
        {
            Iterator<BastardMapEventsInterface> it = mInterfaces.iterator();

            while (it.hasNext()) {
                BastardMapEventsInterface bIf = it.next();
                if (bIf != null) {
                    bIf.onPositionChanged(location);
                } else {
                    it.remove();
                }
            }
        }
    }

    public void setBlockEvents( boolean block )
    {
        mBlock = block;
    }

    public boolean addCallbackInterface( BastardMapEventsInterface bastardIf )
    {
        if( bastardIf != null )
        {
            mInterfaces.add( bastardIf );
            return true;
        }

        return false;
    }
}
