package com.example.skoparov.bastardmaps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by skoparov on 25.02.16.
 */
public class BastardUserMarker
{
    public LatLng pos;
    public String name;
    public String descr;

    public BastardUserMarker(LatLng position,  String name, String description)
    {
        pos = position;
        descr = description;
        this.name = name;
    }
}
