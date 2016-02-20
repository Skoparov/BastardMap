package com.example.skoparov.bastardmaps;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

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

    public static BastardMapLogger getLogger()
    {
        return new BastardMapLogger();
    }

    public static BastardMapManager getMapManager( GoogleApiClient client, BastardMapLogger logger )
    {
        BastardMapManager manager = new BastardMapManager();
        manager.setGoogleApiClient(client);
        manager.setLogger(logger);

        return manager;
    }

    public static BastardMapPainter getPainter( GoogleMap map,
                                                int pathWidth,
                                                int pathColor)
    {
        return new BastardMapPainter( map,
                new BastardMapPainter.PainterSettings(pathWidth, pathColor));
    }

    public static BastardTracker getBastardTracker(Activity parentActivity,
                                                   Integer interval,
                                                   BastardMapLogger logger)
    {
        BastardMapEventsHandler eventsHandler = new BastardMapEventsHandler();
        BastardLocationStorage storage = new BastardLocationStorage();

        BastardMapLocationSubscriber locationSubscriber
                = new BastardMapLocationSubscriber(
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
