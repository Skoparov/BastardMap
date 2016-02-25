package com.example.skoparov.bastardmaps;

import com.google.android.gms.maps.model.LatLng;

public class BastardPointOfInterest extends BastardUserMarker
{
    public int numberOfVisits = 0;

    public BastardPointOfInterest(LatLng position, String name, String description)
    {
        super(position,name, description);
    }
}
