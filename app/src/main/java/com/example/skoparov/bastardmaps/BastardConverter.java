package com.example.skoparov.bastardmaps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by skoparov on 22.02.16.
 */
public class BastardConverter
{
    public static String timeToStr( long time )
    {
        DateFormat formatter = new SimpleDateFormat(
                BastardConstants.TIME.SAVE_FILE_NAME_FORMAT);

        formatter.setTimeZone(TimeZone.getTimeZone(BastardConstants.getTimeZone()));

        return formatter.format(new Date(time));
    }
}
