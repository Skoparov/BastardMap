package com.example.skoparov.bastardmaps;

import android.util.Pair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by skoparov on 17.02.16.
 */


public class BastardMapLogger
{
    public enum EntryType
    {
        LOG_ENTRY_INFO,
        LOG_ENTRY_ERROR
    }

    private class LogEntry
    {
        public String message;
        public Long time;
        public EntryType type;

        public LogEntry( String entryMessage, Long entryTime, EntryType entryType )
        {
            message = entryMessage;
            time = entryTime;
            type = entryType;
        }
    }

    public class LogStorage extends HashSet< LogEntry > {}
    private LogStorage mLog;
    private BastardMapLogEventsInterface mLogEventsInterface;

    private static BastardMapLogger ourInstance = new BastardMapLogger();

    private BastardMapLogger()
    {
        mLog = new LogStorage();
    }

    public static BastardMapLogger getInstance()
    {
        return ourInstance;
    }

    public void setLogEventsInterface( BastardMapLogEventsInterface lofInterface)
    {
        mLogEventsInterface = lofInterface;
    }

    public void addEntry( EntryType type, String message )
    {
        Long timeStamp = System.currentTimeMillis()/1000;
        LogEntry newEntry = new LogEntry( message, timeStamp, type );


        mLog.add( newEntry );

        if( mLogEventsInterface != null )
        {
            mLogEventsInterface.onNewLogEntry( getFormatEntryString( newEntry ) );
        }
    }

    public String getSerializedLog()
    {
        return serialize();
    }

    private String serialize( )
    {
        Iterator< LogEntry > iterator = mLog.iterator();
        String result = new String();

        while (iterator.hasNext())
        {
            result += getFormatEntryString( iterator.next() );
        }

        return result;
    }

    private  String getFormatEntryString( LogEntry entry )
    {
        Date time = new Date(entry.time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String timeStr = formatter.format( time );

        String resultStr = "[ " + timeStr + " ] " + entry.message + "\n\n";

        if( entry.type == EntryType.LOG_ENTRY_ERROR )
        {
            resultStr += "[ !ERROR! ]";
        }

        return resultStr;
    }

}
