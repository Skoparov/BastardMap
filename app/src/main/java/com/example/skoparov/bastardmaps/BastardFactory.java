package com.example.skoparov.bastardmaps;

import android.app.Activity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

public class BastardFactory
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

    public static BastardMapManager getMapManager( GoogleApiClient client, BastardLogger logger )
    {
        BastardMapManager manager = new BastardMapManager();
        manager.setGoogleApiClient(client);
        manager.setLogger(logger);

        return manager;
    }

    public static BastardTrackPainter getPainter( GoogleMap map,
                                                int pathWidth,
                                                int pathColor)
    {
        return new BastardTrackPainter( map,
               new BastardTrackPainter.PainterSettings(pathWidth, pathColor));
    }

    public static BastardTracker getBastardTracker(Activity parentActivity,
                                                   Integer interval,
                                                   BastardLogger logger)
    {
        BastardMapEventsHandler eventsHandler = new BastardMapEventsHandler();
        BastardLocationCollector storage = new BastardLocationCollector();

        BastardLocationSubscriber locationSubscriber
                = new BastardLocationSubscriber(
                eventsHandler,
                BastardFactory.getLocationRequest(interval),
                parentActivity,
                logger );

        GoogleApiClient apiClient = new GoogleApiClient.Builder(parentActivity)
                .addConnectionCallbacks( eventsHandler )
                .addConnectionCallbacks( locationSubscriber )
                .addOnConnectionFailedListener( eventsHandler)
                .addApi(LocationServices.API)
                .build();

        eventsHandler.addCallbackInterface(storage);

        locationSubscriber.setGoogleApiClient(apiClient);
        apiClient.connect();

        return new BastardTracker(
                new BastardTracker.MapPackage(
                apiClient,
                eventsHandler,
                locationSubscriber,
                storage,
                logger));
    }
}
