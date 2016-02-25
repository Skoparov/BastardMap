package com.example.skoparov.bastardmaps;

/**
 * Created by skoparov on 25.02.16.
 */
public interface BastardPointsOfInterestEventInterface
{
    public void onPointVisible( BastardPointOfInterest p );
    public void onPointTaken( BastardPointOfInterest p, BastardPointOfInterest next );
}
