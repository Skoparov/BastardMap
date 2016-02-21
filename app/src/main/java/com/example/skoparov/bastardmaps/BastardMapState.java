package com.example.skoparov.bastardmaps;

import com.google.android.gms.maps.model.CameraPosition;

public class BastardMapState
{
    public CameraPosition cameraPosition;

    public BastardMapState( CameraPosition cameraPos )
    {
        cameraPosition = cameraPos;
    }
}