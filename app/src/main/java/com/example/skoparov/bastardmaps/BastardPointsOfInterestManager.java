package com.example.skoparov.bastardmaps;

import android.location.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BastardPointsOfInterestManager
        implements
        BastardMapEventsInterface
{

    private List<BastardPointOfInterest> mPendingPoints = new ArrayList<>();
    private List<BastardPointOfInterest> mTakenPoints = new ArrayList<>();
    List< BastardPointsOfInterestEventInterface > mSubscriptions;
    private BastardLogger mLogger;
    private int mTakenDist;
    private int mVisibleDist;

    // public methods

    public BastardPointsOfInterestManager( BastardLogger l, int takenDist, int visibleDist )
    {
        mLogger = l;
        mTakenDist = takenDist;
        mVisibleDist = visibleDist;
    }

    public void addSubscription( BastardPointsOfInterestEventInterface iface )
    {
        mSubscriptions.add(iface);
    }

    @Override
    public void onPositionChanged( Location newLocation)
    {
        checkVisibleAndTaken(newLocation);
    }

    public void clear()
    {
        mPendingPoints.clear();
        mTakenPoints.clear();
    }

    public List<BastardPointOfInterest> getPendingPoints()
    {
        return mPendingPoints;
    }

    public List<BastardPointOfInterest> getTakenPoints()
    {
        return mTakenPoints;
    }

    public void setLogger(BastardLogger l)
    {
        mLogger = l;
    }

    // private methods

    private void checkVisibleAndTaken( Location l )
    {
        List<BastardPointOfInterest> visible = new ArrayList<>();
        BastardPointOfInterest taken = null;
        BastardPointOfInterest next = null;

        for(int  point = 0 ; point < mPendingPoints.size(); ++point )
        {
            BastardPointOfInterest currPoint = mPendingPoints.get(point);

            if(isVisible(l, currPoint))
            {
                visible.add(currPoint);
            }

            if (taken == null && isTaken(l, currPoint))
            {
                taken = currPoint;
                if( point != mPendingPoints.size() - 1 )
                {
                    next = mPendingPoints.get(point + 1);
                }
            }
        }

        for(int  point = 0 ; point < visible.size(); ++point )
        {
            notifyOnPointVisible(visible.get(point));
        }

        if(taken != null)
        {
            mPendingPoints.remove(taken);
            mTakenPoints.add(taken);

            notifyOnPointTaken(taken, next);
        }
    }

    private boolean isTaken( Location l, BastardPointOfInterest p)
    {
        Location locationPoint = new Location("");
        locationPoint.setLatitude(p.pos.latitude);
        locationPoint.setLongitude(p.pos.longitude);

        return l.distanceTo(locationPoint) <= mTakenDist;
    }

    private boolean isVisible( Location l, BastardPointOfInterest p)
    {
        Location locationPoint = new Location("");
        locationPoint.setLatitude(p.pos.latitude);
        locationPoint.setLongitude(p.pos.longitude);

        return l.distanceTo(locationPoint) <= mVisibleDist;
    }

    private void notifyOnPointVisible(BastardPointOfInterest p)
    {
        Iterator<BastardPointsOfInterestEventInterface> it = mSubscriptions.iterator();

        while(it.hasNext())
        {
            BastardPointsOfInterestEventInterface iface = it.next();
            if(it != null)
            {
                iface.onPointVisible(p);
            }
            else
            {
                it.remove();
            }
        }
    }

    private void notifyOnPointTaken(BastardPointOfInterest p, BastardPointOfInterest next)
    {
        Iterator<BastardPointsOfInterestEventInterface> it = mSubscriptions.iterator();

        while(it.hasNext())
        {
            BastardPointsOfInterestEventInterface iface = it.next();
            if(it != null)
            {
                iface.onPointTaken(p, next);
            }
            else
            {
                it.remove();
            }
        }
    }
}