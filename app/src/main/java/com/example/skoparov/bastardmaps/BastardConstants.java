package com.example.skoparov.bastardmaps;

import java.util.Calendar;
import java.util.TimeZone;

public final class BastardConstants
{
    public interface ACTION
    {
        public static String MAIN_ACTION = "MAIN";
        public static String START_STOP_LOCATION_UPDATE_ACTION = "START_STOP_LOCATION_UPDATE";
        public static String STARTFOREGROUND_ACTION = "START_FOREGROUND";
        public static String STOPFOREGROUND_ACTION = "STOP_FOREGROUND";
    }

    public interface GUI
    {
        public static String BUTTON_SERVICE_IS_DORMANT = "Start location updates";
        public static String BUTTON_SERVICE_IS_RUNNING = "Stop location updates";

        public static String BUTTON_NO_ONGOING_PATH = "Start new path";
        public static String BUTTON_PATH_IS_ONGOING = "Stop path";
        public static String BUTTON_PATH_IS_PAUSED = "Continue path";
        public static String BUTTON_PATH_IS_NOT_PAUSED = "Pause path";
    }

    public interface REQUESTS
    {
        public static int REQUEST_CHECK_SETTINGS = 0x1;
    }

    public interface NOTIFICATION_ID
    {
        public static int FOREGROUND_SERVICE = 101;
    }

    public interface KEYS
    {
        public static String LOG_KEY = "LOG_KEY";
        public static String TRACK_LIST_KEY = "TRACK_LIST_KEY";
    }

    public interface TIME
    {
        public static String SAVE_FILE_NAME_FORMAT = "dd_MM_yyyy_HH_mm_ss";
    }

    public static String getTimeZone()
    {
        String timeZone = new String("GMT");
        Calendar cal = Calendar.getInstance();
        long milliDiff = cal.get(Calendar.ZONE_OFFSET);
        String [] ids = TimeZone.getAvailableIDs();

        for (String id : ids)
        {
            TimeZone tz = TimeZone.getTimeZone(id);
            if (tz.getRawOffset() == milliDiff)
            {
                timeZone = id;
                break;
            }
        }

        return timeZone;
    }
}
