package com.example.skoparov.bastardmaps;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BastardTrackListActivity extends BastardBasicBoundActivity
{
    public static class TrackListFragment extends ListFragment
    {

        private ArrayList<String> mData;
        private ArrayAdapter<String> mAdapter;
        private BastardTracker mTracker;
        private ListView mListView;

        public void setData( ArrayList<String> data)
        {
            this.mData = data;
        }

        public void setTracker( BastardTracker t )
        {
            mTracker = t;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            mListView = getListView();
            mAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    mData);

            setListAdapter(mAdapter);

            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            mListView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    List<String> selection = mTracker.getSwitchedPaths();
                    for (String key : selection) {
                        int index = mData.indexOf(key);
                        if (index != -1)
                        {
                            mListView.setItemChecked(index, true);
                            mListView.getChildAt(index).setBackgroundColor(getSelectionColor());
                        }
                    }
                }
            });
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id)
        {
            if( mTracker != null )
            {
                String name = mData.get(position);
                mTracker.switchPaintPath(name);

                List<String> selection = mTracker.getSwitchedPaths();
                boolean selected = selection.contains(name)?
                        true : false;

                Integer color = selected? getSelectionColor() : Color.TRANSPARENT;
                l.getChildAt(position).setBackgroundColor(color);

                l.setItemChecked(position, selected);
            }
        }

        private Integer getSelectionColor()
        {
            return Color.LTGRAY;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bastard_track_list);
    }

    @Override
    protected void createServiceDependant()
    {
        TrackListFragment fragObject = new TrackListFragment();
        fragObject.setTracker( mService.getTracker() );
        fragObject.setData(getTrackList());

        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container_track_list,
                fragObject).commit();
    }

    private ArrayList<String> getTrackList()
    {
        ArrayList<String> tracks = new ArrayList<>();

        Iterator it = mService.getTracker().getPaths().entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, BastardPath> entry = (Map.Entry)it.next();
            tracks.add(entry.getKey());
        }

        return tracks;
    }
}
