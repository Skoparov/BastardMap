package com.example.skoparov.bastardmaps;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class BastardPathListActivity extends BastardBasicBoundActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bastard_track_list);
    }

    @Override
    protected void createServiceDependant()
    {
        BastardPathListFragment fragObject = new BastardPathListFragment();
        fragObject.setTools(this, mService.getTracker() );
        fragObject.setData(getPathList());

        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container_track_list,
                fragObject).commit();
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
