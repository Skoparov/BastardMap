package com.example.skoparov.bastardmaps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Vector;

public final class BastardLogger
{
    public enum EntryType
    {
        LOG_ENTRY_INFO,
        LOG_ENTRY_WARNING,
        LOG_ENTRY_ERROR
    }


    //represents single log entry
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

    //typedef
    public class LogStorage extends TreeSet< LogEntry > {}

    private LogStorage mLog;
    private String mTimeZone;
    private DateFormat mTimeFormatter;
    private Vector<BastardLogEventsInterface> mSubsciptions = new Vector<>();

    //public methods

    public BastardLogger()
    {
        mLog = new LogStorage();

        mTimeZone = BastardConstants.getTimeZone();
        mTimeFormatter = new SimpleDateFormat("HH:mm:ss");
        mTimeFormatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
    }

    public boolean addLogEventsInterface( BastardLogEventsInterface lofInterface)
    {
        if( lofInterface != null )
        {
            mSubsciptions.add( lofInterface );
            return true;
        }

        return false;
    }

    public void addEntry( EntryType type, String message )
    {
        long timeStamp = System.currentTimeMillis();
        LogEntry newEntry = new LogEntry( message, timeStamp, type );
        mLog.add( newEntry );

        notifyOnNewEntry( newEntry );
    }

    public String getSerializedLog()
    {
        Iterator< LogEntry > iterator = mLog.iterator();
        String result = new String();

        while (iterator.hasNext())
        {
            result += getFormatEntryString( iterator.next() );
        }

        return result;
    }

    //private methods

    private void notifyOnNewEntry( LogEntry newEntry )
    {
        Iterator<BastardLogEventsInterface> it = mSubsciptions.iterator();

        while(it.hasNext())
        {
            BastardLogEventsInterface bIf = it.next();

            if( bIf != null) {
                bIf.onNewLogEntry(getFormatEntryString(newEntry));
            }
            else{
                it.remove();
            }
        }
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
