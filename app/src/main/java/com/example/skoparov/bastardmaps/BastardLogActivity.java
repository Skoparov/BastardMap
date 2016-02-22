package com.example.skoparov.bastardmaps;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public final class BastardLogActivity
        extends BastardBasicBoundActivity
        implements BastardLogEventsInterface
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bastard_log);

        ScrollView scroll = (ScrollView)findViewById(R.id.log_scroll);
        scroll.setVerticalScrollBarEnabled(true);
    }

    @Override
    protected void createServiceDependant()
    {
        super.createServiceDependant();

        BastardTracker.MapPackage p = mService.getTracker().getMapPackage();

        if( p.isValid() )
        {
            TextView logView = (TextView)findViewById(R.id.log_text);
            logView.setText(p.logger.getSerializedLog());
            p.logger.addLogEventsInterface(this);
        }
    }

    @Override
    public void onNewLogEntry(String entry)
    {
        TextView logView = (TextView)findViewById(R.id.log_text);
        logView.append(entry);
    }
}
