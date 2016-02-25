package com.example.skoparov.bastardmaps;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BastardPointOfInterestSeqCreator
{
    private int mMaxDistByFoot;
    private int mMaxDistByBicycle;

    public enum MoveType
    {
        TYPE_WALK,
        TYPE_BICYCLE
    }

    private class POIInstance implements Comparable<POIInstance>
    {
        public BastardPointOfInterest point;
        public float distance;

        public POIInstance(BastardPointOfInterest p, float dist)
        {
            point = p;
            distance = dist;
        }

        @Override
        public int compareTo(POIInstance another)
        {
            if( this.point.numberOfVisits != another.point.numberOfVisits )
            {
                return this.point.numberOfVisits < another.point.numberOfVisits? -1 : 1;
            }
            else
            {
                return this.distance < another.distance? -1 : 1;
            }
        }
    }

    public BastardPointOfInterestSeqCreator( int maxDistByFoot, int maxDistByBicycle )
    {
        mMaxDistByFoot = maxDistByFoot;
        mMaxDistByBicycle = maxDistByBicycle;
    }

    public List<BastardPointOfInterest > createSequence( List<BastardPointOfInterest> points, LatLng currPos, int numberOfPoints, MoveType type )
    {
        List<BastardPointOfInterest> track = new ArrayList<>();
        int maxDist = getMaxDist(type);

        for( int startPoint = 0; startPoint <  points.size(); ++startPoint)
        {
            LatLng pos = currPos;

            while( true )
            {
                BastardPointOfInterest p = getNextPoint(pos, maxDist, points, startPoint);
                if( p != null)
                {
                    track.add(p);
                    pos = p.pos;
                }

                if( p == null || track.size() == numberOfPoints )
                {
                    break;
                }
            }

            if(  track.size() == numberOfPoints )
            {
                break;
            }

            track.clear();
        }

        return track;
    }

    BastardPointOfInterest getNextPoint( LatLng pos, int maxDist, List< BastardPointOfInterest > points, int startPos)
    {
        List<POIInstance> appropriate = new ArrayList<>();

        for( int point = startPos; point < points.size(); ++point )
        {
            BastardPointOfInterest p = points.get(point);
            float currDist = getDistance( p.pos, pos );

            if( currDist <= maxDist )
            {
               appropriate.add(new POIInstance(p, currDist));
            }
        }

       if( !appropriate.isEmpty() )
       {
           Collections.sort(appropriate);
           return appropriate.get(0).point;
       }

        return null;
    }

    private int getMaxDist( MoveType type)
    {
        if( type == MoveType.TYPE_BICYCLE )
        {
            return mMaxDistByBicycle;
        }
        else  if( type == MoveType.TYPE_WALK )
        {
            return mMaxDistByFoot;
        }

        return Integer.MAX_VALUE;
    }

    private float getDistance(LatLng one, LatLng two)
    {
        Location locationA = new Location("");
        locationA.setLatitude(one.latitude);
        locationA.setLongitude(one.longitude);

        Location locationB = new Location("");
        locationB.setLatitude(two.latitude);
        locationB.setLongitude(two.longitude);

        return locationA.distanceTo(locationB);
    }
}
