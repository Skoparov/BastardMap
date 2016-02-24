package com.example.skoparov.bastardmaps;

import android.location.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Vector;

import static junit.framework.TestCase.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class BastardPathUnitTest
{
    BastardPath path;

    Location createLocation( double lat, double lon, long time )
    {
        Location l = new Location("");
        l.setLatitude(lat);
        l.setLongitude(lon);
        l.setTime(time);

        return l;
    }

    Vector<Location> createPathPoints()
    {
        Vector<Location> v = new Vector<>();


        v.add(createLocation(55.70939, 37.732673,  0));
        v.add(createLocation(55.70939, 37.736804,  1000));
        v.add(createLocation(55.71198, 37.73551,   2000));
        v.add(createLocation(55.71227, 37.737473,  3000));
        v.add(createLocation(55.712319, 37.73816,  4000));
        v.add(createLocation(55.712283, 37.738439, 5000));
        v.add(createLocation(55.712531, 37.738857, 6000));
        v.add(createLocation(55.713312, 37.739158, 7000));
        v.add(createLocation(55.713936, 37.73845, 8000));

        return v;
    }

    @Before
    public void setUp() throws Exception
    {
        path = new BastardPath(createPathPoints());
        path.addIdleTime(1000);
    }

    @Test
    public void testDist() throws Exception
    {
        assertEquals(path.getDistance(), 958, 1);
    }

    @Test
    public void testTime() throws Exception
    {
        assertEquals(path.getDuration(), 7000, 0);
    }

    @Test
    public void testAvgSpeed() throws Exception
    {
        float f = path.getAverageSpeed();
        assertEquals(path.getAverageSpeed(), 136.8, 0.2);
    }
}