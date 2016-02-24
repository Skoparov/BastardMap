package com.example.skoparov.bastardmaps;

import android.location.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Vector;

import static junit.framework.TestCase.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class BastardLocationCollectorUnitTest
{
    BastardLocationCollector collector;
    BastardPath testPath;

    Location createLocation( double lat, double lon, long time )
    {
        Location l = new Location("");
        l.setLatitude(lat);
        l.setLongitude(lon);
        l.setTime(time);

        return l;
    }

    @Before
    public void setUp() throws Exception
    {
        collector = new BastardLocationCollector();
    }

    @Test
    public void onPositionChangedCheck() throws Exception
    {
        collector.onPositionChanged( createLocation(55.70939, 37.732673,  0) );
        long asd = collector.getPath().size();
        assertEquals(collector.getPath().size(), 1);
    }

    @Test
    public void clearCheck() throws Exception
    {
        collector.onPositionChanged( createLocation(55.70939, 37.732673,  0) );
        collector.clear();
        assertEquals(collector.getPath().size(), 0);
    }

    @Test
    public void setPausedCheck() throws Exception
    {
        collector.setPaused(true);
        Thread.sleep(500);
        collector.setPaused(false);
        assertEquals(collector.getPath().getIdleTime(), 500, 100);
    }
}