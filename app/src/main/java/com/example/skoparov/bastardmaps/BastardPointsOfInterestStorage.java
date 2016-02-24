package com.example.skoparov.bastardmaps;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class BastardPointsOfInterestStorage
{
    public class UserMarker
    {
        public LatLng pos;
        public String descr;

        public UserMarker(LatLng position, String description)
        {
            pos = position;
            descr = description;
        }
    }

    public class PointOfInterest extends UserMarker
    {
        PointOfInterest(LatLng position, String description)
        {
            super(position, description);
        }
    }

    private List<UserMarker> mUserMarkers = new ArrayList<>();
    private List<PointOfInterest> mPointOfInterest = new ArrayList<>();

    BastardPointsOfInterestStorage( List<PointOfInterest> pointsOfInterest, List<UserMarker> userMarkers )
    {
        mPointOfInterest = pointsOfInterest;
        mUserMarkers = userMarkers;
    }
}
