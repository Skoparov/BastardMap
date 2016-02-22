package com.example.skoparov.bastardmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class BastardTrackListActivity extends AppCompatActivity
{
    public static class TrackListFragment extends ListFragment
    {

        String data[];
        public ArrayAdapter<String> mAdapter;

        public TrackListFragment( String[] data )
        {
            this.data = data;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    data);

            setListAdapter(mAdapter);
        }
    }

    private ArrayList<String> mTracks =new ArrayList<String>();
    private  ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bastard_track_list);

        Intent intent = getIntent();
        String tracksList = intent.getStringExtra( BastardConstants.KEYS.TRACK_LIST_KEY );

        TrackListFragment fragObject = new TrackListFragment(tracksList.split(";"));
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container2,
                fragObject).commit();
    }
}
