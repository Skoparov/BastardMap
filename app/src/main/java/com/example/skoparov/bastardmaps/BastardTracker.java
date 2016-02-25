package com.example.skoparov.bastardmaps;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BastardTracker
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
    private Activity mGuiActivity;
    private boolean mIsPaused = false;
    private boolean mIsRecording = false;
    private BastardMapState mPrevMapState;
    private BastardFileManager mFileManager;
    private List< String > mSwitchedPaths = new ArrayList<>();
    private HashMap<String, BastardPathDetails > mPathsList = new HashMap<>();
    private ArrayList< BastardPaintEventsInterface > mPainterSubsriptions = new ArrayList<>();

    //public methods

    public BastardTracker( MapPackage pack )
    {
        mP = pack;
        mFileManager = new BastardFileManager();
    }

    public void setActivity( Activity activity )
    {
        mGuiActivity = activity;
    }

    public void connect()
    {
        mP.apiClient.connect();
    }

    public void loadPathsBrief()
    {
        try
        {
            mPathsList = mFileManager.loadPathsList();
            mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Paths loaded: OK");
        }
        catch(Exception e)
        {
            mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                    "Failed to load path list");
        }
    }

    public void startPath()
    {
        mP.collector.setmLogger(mP.logger);
        if(mIsRecording)
        {
            stopPath();
        }

        mP.collector.clear();
        mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Tracker : path started");
        setRecording(true);

        sendToast("Path tracking started");
    }

    public void stopPath()
    {
        setRecording(false);
        mIsPaused = false;
        mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO, "Tracker : path stopped");
        BastardPath newPath = mP.collector.getPath();
        String name = newPath.getName();

        if( newPath.size() != 0)
        {
            BastardPathDetails d =
                    new BastardPathDetails(name, BastardPathDetails.getPathDetails(newPath));

            if(savePath(newPath, d))
            {
                mPathsList.put(name, d);
            }
        }

        mP.collector.clear();
    }

    public void setPaused( boolean paused )
    {
        mIsPaused = paused;
        mP.eventsHandler.setBlockEvents(paused);
        mP.collector.setPaused(paused);

        if(paused)
        {
            sendToast("Path tracking paused");
        }
        else
        {
            sendToast("Path tracking resumed");
        }
    }

    public boolean deletePath( String pathName )
    {
        boolean ok = false;
        String message = new String();
        if(mPathsList.containsKey(pathName))
        {
            mPathsList.remove(pathName);
            boolean ok1 = mFileManager.deleteFile(pathName);
            boolean ok2 = mFileManager.deleteDetailsFile(pathName);

            ok = ok1 && ok2;
            message = ok? "Path" + pathName + " deleted" :
                    "Failed to delete " + pathName;
        }
        else
        {
            message = "Could not find path " + pathName;
        }

        sendToast(message);

        return false;
    }

    public boolean switchPaintPath( String name )
    {
        if( mPathsList.containsKey(name) )
        {
            BastardPath p = null;
            try
            {
                p = mFileManager.loadPath(name);
                String message = new String();

                if(mSwitchedPaths.contains(name))
                {
                    mSwitchedPaths.remove(name);
                    message = "Path " + name + " removed from map";
                }
                else
                {
                    mSwitchedPaths.add(name);
                    message = "Path " + name + " added to map";
                }

                notifyPaintSubscriptions(p);
                sendToast(message);
                return true;
            }
            catch(Exception e) {
                mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                        "Failed to load path " + name);

                sendToast("Failed to load path " + name);
            }
        }

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

    public HashMap<String, BastardPathDetails> getPathsList()
    {
        return mPathsList;
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

    private boolean savePath(BastardPath p, BastardPathDetails d)
    {
        boolean isSavedOk = true;

        try {
            mFileManager.savePath(p);
        }
        catch(Exception e)
        {
            isSavedOk = false;
            mFileManager.deleteFile(p.getName());
            mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                    "Could not save path " + p.getName());

            sendToast("Failed to save path " +  p.getName());
        }

        if(isSavedOk)
        {
            try {
                mFileManager.savePathDetails(d);
            }
            catch(Exception e)
            {
                mFileManager.deleteFile(p.getName());
                mFileManager.deleteDetailsFile(p.getName());

                mP.logger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                        "Could not save path details" + p.getName());

                isSavedOk = false;
                sendToast("Failed to save path details for" + p.getName());
            }
        }

        sendToast("Saved path " + p.getName());

        return isSavedOk;
    }

    private void sendToast( String message )
    {
        if( mGuiActivity != null)
        {
            Toast.makeText(mGuiActivity, message, Toast.LENGTH_SHORT).show();
        }
    }
}