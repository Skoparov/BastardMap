package com.example.skoparov.bastardmaps;

import android.location.Location;
import com.google.android.gms.common.ConnectionResult;

public interface BastardMapEventsInterface
{
    //public void onConnectionReady();
    public void onPositionChanged( long time, Location newLocation);
}
