package com.example.skoparov.bastardmaps;

import android.location.Location;

public class BastardPosition
{
    public long time;
    public Location location;

    BastardPosition( long checkTime, Location l )
    {
        time = checkTime;
        location = l;
    }
}
