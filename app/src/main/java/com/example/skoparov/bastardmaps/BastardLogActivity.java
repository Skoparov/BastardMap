package com.example.skoparov.bastardmaps;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BastardLogActivity extends AppCompatActivity
                                implements
                                BastardMapLogEventsInterface
{
    TextView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bastard_log);

        BastardMapLogger logger = BastardMapLogger.getInstance();

        mLogView = new TextView(this);
        mLogView.setMovementMethod(new ScrollingMovementMethod());
        mLogView.setMaxLines(65536);
        mLogView.setVerticalScrollBarEnabled(true);
        mLogView.setText(logger.getSerializedLog());
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_log);

        logger.setLogEventsInterface( this );

        layout.addView(mLogView);
    }

    @Override
    public void onNewLogEntry(String entry)
    {
        mLogView.append(entry);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
