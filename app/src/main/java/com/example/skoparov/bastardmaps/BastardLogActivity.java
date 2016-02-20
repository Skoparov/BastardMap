package com.example.skoparov.bastardmaps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BastardLogActivity
        extends BastardBasicBoundActivity
        implements BastardMapLogEventsInterface
{
    class ScrollTextView extends TextView
    {

        private int maxY = 0;

        public ScrollTextView(Context context)
        {
            super(context);

        }

        public ScrollTextView(Context context, AttributeSet attrs) {
            super(context, attrs);

        }

        public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);

        }

        protected void onDraw (Canvas canvas)
        {
            super.onDraw(canvas);

        }

        @Override
        protected void onScrollChanged(int x, int y, int oldx, int oldy)
        {
            super.onScrollChanged(x, y, oldx, oldy);

            if(y>maxY)
            {
                maxY = y;
            }
        }
        public void scrollToBottom()
        {
            this.scrollTo(0, maxY);
        }
    }

    private ScrollTextView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bastard_log);

        Intent intent = getIntent();
        String log = intent.getStringExtra( BastardConstants.MISC.LOG_KEY );

        mLogView = new ScrollTextView(this);
        mLogView.setMovementMethod(new ScrollingMovementMethod());
        mLogView.setMaxLines(65536);
        mLogView.setVerticalScrollBarEnabled(true);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_log);
        layout.addView(mLogView);

    }

    @Override
    protected void createServiceDependant()
    {
        super.createServiceDependant();

        BastardTracker.MapPackage p = mService.getTracker().getMapPackage();

        if( p.isValid() )
        {
            mLogView.setText(p.logger.getSerializedLog());
            p.logger.addLogEventsInterface(this);
        }

        //mLogView.scrollToBottom();
    }

    @Override
    public void onNewLogEntry(String entry)
    {
        mLogView.append(entry);
    }
}
