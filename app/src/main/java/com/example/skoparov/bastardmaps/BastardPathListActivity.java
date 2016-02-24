package com.example.skoparov.bastardmaps;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class BastardPathListActivity extends BastardBasicBoundActivity
{
    private BastardPathListFragment mFragObject;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bastard_track_list);
    }

    @Override
    protected void createServiceDependant()
    {
        mFragObject = new BastardPathListFragment();
        mFragObject.setTools(this, mService.getTracker());
        mFragObject.setData(getPathList());

        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container_track_list,
                mFragObject).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        if(mFragObject != null)
        {
            getSupportFragmentManager().beginTransaction().remove(mFragObject).commit();
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    private ArrayList<BastardPathListFragment.RowItem> getPathList()
    {
        ArrayList<BastardPathListFragment.RowItem> paths = new ArrayList<>();

        Iterator it = mService.getTracker().getPathsList().entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, BastardPathDetails> entry = (Map.Entry)it.next();
            paths.add(new BastardPathListFragment.RowItem(
                    entry.getValue().pathName,
                    entry.getValue().details));
        }

        return paths;
    }
}
