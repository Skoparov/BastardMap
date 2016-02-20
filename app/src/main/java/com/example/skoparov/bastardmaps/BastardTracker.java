package com.example.skoparov.bastardmaps;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Environment;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Vector;

public class BastardTracker extends Activity
{
    public static class MapPackage
    {
        public GoogleApiClient apiClient;
        public BastardMapEventsHandler eventsHandler;
        public BastardMapLocationSubscriber subscriber;
        public BastardMapLogger logger;
        public BastardLocationStorage storage;

        public MapPackage(GoogleApiClient apiClient,
                          BastardMapEventsHandler eventsHandler,
                          BastardMapLocationSubscriber subscriber,
                          BastardLocationStorage storage,
                          BastardMapLogger logger)
        {
            this.apiClient = apiClient;
            this.eventsHandler = eventsHandler;
            this.subscriber = subscriber;
            this.storage = storage;
            this.logger = logger;
        }

        public boolean isValid()
        {
            return  apiClient != null &&
                    eventsHandler != null &&
                    subscriber != null &&
                    logger != null &&
                    storage != null;
        }
    }

    private MapPackage mP;
    private boolean mIsRecording = false;
    private BastardMapState mPrevMapState;
    private Vector< BastardTrack > mTracks = new Vector<>();

    public BastardTracker( MapPackage pack )
    {
        mP = pack;
    }

    public void startTrack()
    {
        mP.storage.setLogger(mP.logger);
        if(mIsRecording){
            stopTrack();
        }

        mP.storage.clearPositions();
        mP.logger.addEntry(BastardMapLogger.EntryType.LOG_ENTRY_INFO, "Tracker : path started");
        setRecording(true);
    }

    public void stopTrack()
    {
        setRecording(false);
        mP.logger.addEntry(BastardMapLogger.EntryType.LOG_ENTRY_INFO, "Tracker : path stopped");

        BastardTrack newTrack = new BastardTrack(mP.storage.getPositions());


        mTracks.add(newTrack);

        //saveTrack();
    }

//    public void saveTrack()
//    {
//        try
//        {
//            DataOutputStream out = new DataOutputStream(new FileOutputStream("test.txt"));
//            Iterator<BastardPosition> it = mTracks.lastElement().getTrackPoints().iterator();
//
//            while( it.hasNext() )
//            {
//                BastardPosition pos = it.next();
//                out.writeLong(pos.time);
//                out.writeDouble(pos.location.getLatitude());
//                out.writeDouble(pos.location.getLongitude());
//            }
//
//            Vector<BastardPosition> i = new Vector<>();
//            BastardTrack mTracks1 = new BastardTrack(i);
//
//            DataInputStream in = new DataInputStream(new FileInputStream("test.txt"));
//            while(in.available() > 0)
//            {
//                long time = in.readLong();
//                double lat = in.readDouble();
//                double lon = in.readDouble();
//
//                Location targetLocation = new Location("");//provider name is unecessary
//                targetLocation.setLatitude(lat);//your coords of course
//                targetLocation.setLongitude(lon);
//
//                mTracks1.getTrackPoints().add(
//                        new BastardPosition(time, targetLocation));
//            }
//
//            int  asdi = 0;
//        }
//        catch(Exception e)
//        {
//
//        }
//    }

    public Vector< BastardTrack > getTracks()
    {
        return mTracks;
    }

    public boolean isRecording()
    {
        return mIsRecording;
    }

    void setMapPackage(MapPackage p)
    {
        mP = p;
    }

    MapPackage getMapPackage()
    {
        return mP;
    }

    public void saveMapState( BastardMapState state )
    {
        mPrevMapState= state;
    }

    public BastardMapState getPrevMapState()
    {
        return mPrevMapState;
    }

    private void setRecording( boolean recording )
    {
        mIsRecording = recording;
        mP.eventsHandler.setBlockEvents( !mIsRecording );
    }
}