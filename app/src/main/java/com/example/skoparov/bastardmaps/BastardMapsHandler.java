package com.example.skoparov.bastardmaps;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import android.view.View;
import android.widget.TextView;

public class BastardMapsHandler extends SupportMapFragment
        implements
        OnMapReadyCallback,
        OnMapLongClickListener,
        OnMapClickListener
{
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        LatLng moscow = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(moscow).title("Marker in MOscow"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(moscow));

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
    }

    public void onMapClick(LatLng point)
    {
        int i = 0;
        //mTapTextView.setText("long pressed, point=" + point);
    }

    public void onMapLongClick(LatLng point)
    {
        int i = 0;
        //mTapTextView.setText("long pressed, point=" + point);
    }
}
