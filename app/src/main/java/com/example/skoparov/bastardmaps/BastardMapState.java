package com.example.skoparov.bastardmaps;

import com.google.android.gms.maps.model.CameraPosition;


public class BastardMapState
{
    public CameraPosition prevCameraPosition;

    public BastardMapState( CameraPosition prevCamPos )
    {
        prevCameraPosition = prevCamPos;
    }
}