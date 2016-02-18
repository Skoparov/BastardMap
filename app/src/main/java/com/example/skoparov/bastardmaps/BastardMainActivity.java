package com.example.skoparov.bastardmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class BastardMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    GoogleApiClient mApiClient;
    BastardMapLocationSubscriber mLocationHandler;
    BastardMapEventsHandler mEventsHandler;
    BastardMapManager mMapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        {
            // Create menus
            setContentView(R.layout.activity_bastard_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            // Create map stuff
            TextView mTapTextView = (TextView) findViewById(R.id.tap_text);

            if( checkPermissions() )
            {
                BastardMapLogger.getInstance().addEntry(
                        BastardMapLogger.EntryType.LOG_ENTRY_INFO,
                        "Location permissions granted");

                mMapManager = new BastardMapManager();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mMapManager).commit();

                mEventsHandler = new BastardMapEventsHandler( mMapManager );
                mLocationHandler = new BastardMapLocationSubscriber( mEventsHandler, createLocationRequest() );

                mApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks( mEventsHandler )
                        .addConnectionCallbacks( mLocationHandler )
                        .addOnConnectionFailedListener( mEventsHandler )
                        .addApi(LocationServices.API)
                        .build();

                mLocationHandler.setGoogleApiClient( mApiClient );
                mMapManager.setGoogleApiClient( mApiClient );
                mMapManager.setTestTextView(mTapTextView);
                mMapManager.getMapAsync(mMapManager);

                mApiClient.connect();
            }
            else
            {
                BastardMapLogger.getInstance().addEntry(
                        BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                        "Location permissions denied");
            }
        }
    }

    @Override
    public void onStart()
    {
        BastardMapLogger.getInstance().addEntry(
                BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                "Location permissions denied");

        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        mApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bastard_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Log)
        {
            Intent intent = new Intent(this, BastardLogActivity.class);
            startActivity( intent );
            //TextView v = ( TextView )findViewById( R.id.LogView );

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkPermissions()
    {
        return   ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    public LocationRequest createLocationRequest()
    {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return request;
    }
}
