package com.example.skoparov.bastardmaps;

import android.app.Activity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

public final class BastardFactory
{
    public static LocationRequest getLocationRequest( Integer interval )
    {
        LocationRequest request = new LocationRequest();
        request.setInterval(interval);
        request.setFastestInterval(interval);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return request;
    }

    public static BastardLogger getLogger()
    {
        return new BastardLogger();
    }

    public static BastardTrackPainter getPainter( GoogleMap map, BastardTrackPainter.PainterSettings settings )
    {
        return new BastardTrackPainter(map, settings);
    }

    public static BastardMapManager getMapManager( BastardMapManager.MapManagerPackage p )
    {
        BastardMapManager manager = new BastardMapManager();
        manager.setMapMapagerPackage(p);
        return manager;
    }

    public static BastardTracker getBastardTracker(Activity parentActivity,
                                                   Integer interval,
                                                   BastardLogger logger)
    {
        BastardMapEventsHandler eventsHandler = new BastardMapEventsHandler();
        BastardLocationCollector storage = new BastardLocationCollector();

        GoogleApiClient apiClient = new GoogleApiClient.Builder(parentActivity)
                .addConnectionCallbacks( eventsHandler )
                .addOnConnectionFailedListener( eventsHandler)
                .addApi(LocationServices.API)
                .build();

        BastardLocationSubscriber locationSubscriber
                = new BastardLocationSubscriber(
                eventsHandler,
                apiClient,
                BastardFactory.getLocationRequest(interval),
                parentActivity,
                logger );

        apiClient.registerConnectionCallbacks(locationSubscriber);
        eventsHandler.addCallbackInterface(storage);

        return new BastardTracker(
                new BastardTracker.MapPackage(
                apiClient,
                eventsHandler,
                locationSubscriber,
                storage,
                logger));
    }
}
