package com.example.skoparov.bastardmaps;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    private HashMap<String, BastardPath> mPaths = new HashMap<>();
    private List< String > mSwitchedPaths = new ArrayList<>();
    private ArrayList< BastardPaintEventsInterface > mPainterSubsriptions = new ArrayList<>();

    public BastardTracker( MapPackage pack )
    {
        mP = pack;
    }

    public void connect()
    {
        mP.apiClient.connect();
    }

    public void startPath()
    {
        mP.collector.setLogger(mP.logger);
        if(mIsRecording){
            stopPath();
        }

        mP.collector.clear();
        mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Tracker : path started");
        setRecording(true);
    }

    public void stopPath()
    {
        setRecording(false);
        mIsPaused = false;
        mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Tracker : path stopped");
        BastardPath newPath = mP.collector.getTrack();

        if( newPath.size() != 0 )
        {
            mPaths.put(newPath.getName(), mP.collector.getTrack());

            //TODO: savePath();
        }

        mP.collector.clear();
    }

    public void setPaused( boolean paused )
    {
        mIsPaused = paused;
        mP.eventsHandler.setBlockEvents(paused);
    }

    public void savePathToFile( BastardPath t )
    {
        String filename = BastardConverter.timeToStr(
                t.getPoints().firstElement().getTime());

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

    public boolean switchPaintPath( String name )
    {
        if( mPaths.containsKey(name) )
        {
            BastardPath p = mPaths.get(name);

            if(mSwitchedPaths.contains(name)){
                mSwitchedPaths.remove(name);
            }
            else
            {
                mSwitchedPaths.add(name);
            }

            notifyPaintSubscriptions(p);
            return true;
        }


        mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                "Could not find path " + name);

        return false;
    }

    public void subscribePaintInterface( BastardPaintEventsInterface paintIf )
    {
        mPainterSubsriptions.add(paintIf);
    }

    public List<String> getSwitchedPaths()
    {
        return mSwitchedPaths;
    }

    public HashMap<String, BastardPath> getPaths()
    {
        return mPaths;
    }

    public boolean isRecording()
    {
        return mIsRecording;
    }

    public boolean isPaused()
    {
        return mIsPaused;
    }

    public void setMapPackage(MapPackage p)
    {
        mP = p;
    }

    public MapPackage getMapPackage()
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

    // private methods

    private void setRecording( boolean recording )
    {
        mIsRecording = recording;
        mP.eventsHandler.setBlockEvents( !mIsRecording );
    }

    private void notifyPaintSubscriptions(BastardPath p)
    {
        Iterator< BastardPaintEventsInterface > it = mPainterSubsriptions.iterator();

        while( it.hasNext() )
        {
            BastardPaintEventsInterface curr = it.next();

            if( curr != null)
            {
                curr.switchPaintPath(p);
            }
            else
            {
                it.remove();
            }
        }
    }
}