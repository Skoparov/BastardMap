package com.example.skoparov.bastardmaps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeSet;

public class BastardMapLogger
{
    public enum EntryType
    {
        LOG_ENTRY_INFO,
        LOG_ENTRY_WARNING,
        LOG_ENTRY_ERROR
    }

    public class LogEntry implements Comparable< LogEntry >
    {
        public String message;
        public long time;
        public EntryType type;

        public LogEntry( String entryMessage, Long entryTime, EntryType entryType )
        {
            message = entryMessage;
            time = entryTime;
            type = entryType;
        }

        @Override
        public int compareTo(LogEntry another)
        {
            return (int) (this.time - another.time);
        }
    }

    public class LogStorage extends TreeSet< LogEntry > {}

    private LogStorage mLog;
    private String mTimeZone;
    private DateFormat mTimeFormatter;
    private BastardMapLogEventsInterface mLogEventsInterface;
    private static BastardMapLogger instance = new BastardMapLogger();

    private BastardMapLogger()
    {
        mLog = new LogStorage();

        getTimeZone();
        mTimeFormatter = new SimpleDateFormat("HH:mm:ss");
        mTimeFormatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
    }

    public static BastardMapLogger getInstance()
    {
        return instance;
    }

    public void setLogEventsInterface( BastardMapLogEventsInterface lofInterface)
    {
        mLogEventsInterface = lofInterface;
    }

    public void addEntry( EntryType type, String message )
    {
        long timeStamp = System.currentTimeMillis();
        LogEntry newEntry = new LogEntry( message, timeStamp, type );
        mLog.add( newEntry );

        // send to log view
        if( mLogEventsInterface != null ) {
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
        String timeStamp = mTimeFormatter.format(new Date(entry.time));
        String typeStr = entry.type != EntryType.LOG_ENTRY_INFO?
                String.format("[ !%s! ] ", entryTypeToString( entry.type )) : "";

        return String.format("[ %s ] %s%s\n",
                timeStamp,
                typeStr,
                entry.message );
    }

    private void getTimeZone()
    {
        mTimeZone = new String("GMT");
        Calendar cal = Calendar.getInstance();
        long milliDiff = cal.get(Calendar.ZONE_OFFSET);
        String [] ids = TimeZone.getAvailableIDs();

        for (String id : ids)
        {
            TimeZone tz = TimeZone.getTimeZone(id);
            if (tz.getRawOffset() == milliDiff)
            {
                mTimeZone = id;
                break;
            }
        }
    }

    private String entryTypeToString( EntryType type )
    {
        String result = new String();

        switch ( type )
        {
            case LOG_ENTRY_WARNING:
                result = "WARNING";
                break;
            case LOG_ENTRY_ERROR:
                result = "ERROR";
                break;
        }

        return result;
    }

}
