package com.example.skoparov.bastardmaps;

import java.util.ArrayList;
import java.util.List;

public class BastardPointsOfInterestStorage
{
    private List<BastardUserMarker> mUserMarkers = new ArrayList<>();
    private List<BastardPointOfInterest> mPointOfInterest = new ArrayList<>();

    BastardPointsOfInterestStorage(  )
    {

    }

    public List<BastardPointOfInterest> getPointsOfInterest()
    {
        return mPointOfInterest;
    }

    public List<BastardUserMarker> getUserMarkers()
    {
        return mUserMarkers;
    }
}
