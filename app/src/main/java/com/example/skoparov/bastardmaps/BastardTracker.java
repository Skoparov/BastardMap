package com.example.skoparov.bastardmaps;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileOutputStream;
import java.util.Vector;

public class BastardTracker extends Activity
{
    public static class MapPackage
    {
        public GoogleApiClient apiClient;
        public BastardMapEventsHandler eventsHandler;
        public BastardLocationSubscriber subscriber;
        public BastardLogger logger;
        public BastardLocationCollector collector;

        public MapPackage(GoogleApiClient apiClient,
                          BastardMapEventsHandler eventsHandler,
                          BastardLocationSubscriber subscriber,
                          BastardLocationCollector collector,
                          BastardLogger logger)
        {
            this.apiClient = apiClient;
            this.eventsHandler = eventsHandler;
            this.subscriber = subscriber;
            this.collector = collector;
            this.logger = logger;
        }

        public boolean isValid()
        {
            return  apiClient != null &&
                    eventsHandler != null &&
                    subscriber != null &&
                    logger != null &&
                    collector != null;
        }
    }

    private MapPackage mP;
    private boolean mIsRecording = false;
    private boolean mIsPaused = false;
    private BastardMapState mPrevMapState;
    private Vector< BastardTrack > mTracks = new Vector<>();

    public BastardTracker( MapPackage pack )
    {
        mP = pack;
    }

    public void connect()
    {
        mP.apiClient.connect();
    }

    public void startTrack()
    {
        mP.collector.setLogger(mP.logger);
        if(mIsRecording){
            stopTrack();
        }

        mP.collector.clear();
        mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Tracker : path started");
        setRecording(true);
    }

    public void stopTrack()
    {
        setRecording(false);
        mIsPaused = false;
        mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Tracker : path stopped");
        BastardTrack newTrack = mP.collector.getTrack();

        if( newTrack.size() != 0 )
        {
            mTracks.addElement(mP.collector.getTrack());
        }

        mP.collector.clear();

        //TODO: saveTrack();
    }

    public void setPaused( boolean paused )
    {
        mIsPaused = paused;
        mP.eventsHandler.setBlockEvents(paused);
    }

    public void saveTrackToFile( BastardTrack t )
    {
        String filename = BastardConverter.timeToStr(
                t.getTrackPoints().firstElement().getTime());

        try
        {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            t.save(outputStream);
            outputStream.close();
        }
        catch (Exception e)
        {
            mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                   "Error saving track#" + filename );
        }
    }

    public Vector< BastardTrack > getTracks()
    {
        return mTracks;
    }

    public boolean isRecording()
    {
        return mIsRecording;
    }

    public boolean isPaused()
    {
        return mIsPaused;
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