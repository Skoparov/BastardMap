package com.example.skoparov.bastardmaps;

public class BastardConstants
{
    public interface ACTION
    {
        public static String MAIN_ACTION = "com.marothiatechs.foregroundservice.action.main";
        public static String INIT_ACTION = "com.marothiatechs.foregroundservice.action.init";
        public static String PREV_ACTION = "com.marothiatechs.foregroundservice.action.prev";
        public static String PLAY_ACTION = "com.marothiatechs.foregroundservice.action.play";
        public static String NEXT_ACTION = "com.marothiatechs.foregroundservice.action.next";
        public static String STARTFOREGROUND_ACTION = "com.marothiatechs.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.marothiatechs.foregroundservice.action.stopforeground";
    }

    public interface GUI
    {
        public static String BUTTON_SERVICE_IS_DORMANT = "Start location updates";
        public static String BUTTON_SERVICE_IS_RUNNING = "Stop location updates";

        public static String BUTTON_NO_ONGOING_PATH = "Start new path";
        public static String BUTTON_PATH_IS_ONGOING = "Stop path";
    }

    public interface REQUESTS
    {
        public static int REQUEST_CHECK_SETTINGS = 0x1;
    }

    public interface NOTIFICATION_ID
    {
        public static int FOREGROUND_SERVICE = 101;
    }

    public interface MISC
    {
        public static String LOG_KEY = "LOG_KEY";
    }
}
