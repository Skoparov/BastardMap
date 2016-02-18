package com.example.skoparov.bastardmaps;

import android.Manifest;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class BastardMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    GoogleApiClient mApiClient;
    BastardMapLocationSubscriber mLocationSubscriber;
    BastardMapEventsHandler mEventsHandler;
    BastardMapManager mMapManager;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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

        BastardMapLogger.getInstance().addEntry(
                BastardMapLogger.EntryType.LOG_ENTRY_INFO,
                "Location permissions granted");

        mMapManager = new BastardMapManager();
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container,
                mMapManager).commit();

        mEventsHandler = new BastardMapEventsHandler( mMapManager );
        mLocationSubscriber = new BastardMapLocationSubscriber(
                mEventsHandler,
                createLocationRequest(),
                this );

        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks( mEventsHandler )
                .addConnectionCallbacks(mLocationSubscriber)
                .addOnConnectionFailedListener( mEventsHandler )
                .addApi(LocationServices.API)
                .build();

        mLocationSubscriber.setGoogleApiClient(mApiClient);
        mMapManager.setGoogleApiClient(mApiClient);
        mMapManager.setTestTextView(mTapTextView);
        mMapManager.getMapAsync(mMapManager);

        if( savedInstanceState != null )
        {
            restoreMapState( savedInstanceState );
        }

        if( checkPermissions() )
        {
            mApiClient.connect();
        }
        else
        {
            BastardMapLogger.getInstance().addEntry(
                    BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                    "Location permissions denied");
        }
    }

    @Override
    public void onStart()
    {
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
        BastardMapLogger.getInstance().addEntry(
                BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                "on destroy");
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
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkPermissions()
    {
        return   ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CHECK_SETTINGS)
        {
            if (resultCode == RESULT_OK )
            {
                mLocationSubscriber.addLocationRequertSettings();
            }
            else
            {
                BastardMapLogger.getInstance().addEntry(
                        BastardMapLogger.EntryType.LOG_ENTRY_ERROR,
                        "RESOLUTION_REQUIRED asking failed");
            }
        }
    }

    public LocationRequest createLocationRequest()
    {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return request;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        saveMapState(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void restoreMapState( Bundle savedInstanceState )
    {
        double latitude = savedInstanceState.getDouble("latitude");
        double longitude = savedInstanceState.getDouble("longitude");
        float bearing = savedInstanceState.getFloat("bearing");
        float tilt = savedInstanceState.getFloat("tilt");
        float zoom = savedInstanceState.getFloat("zoom");


        CameraPosition camPos = new CameraPosition(
                new LatLng(latitude, longitude),
                zoom,
                tilt,
                bearing);

        if( mMapManager != null)
        {
            mMapManager.setMapState( camPos );
        }
    }

    private void saveMapState(Bundle savedInstanceState)
    {
        if( mMapManager != null )
        {
            CameraPosition camPos = mMapManager.getMapState();

            if( camPos != null )
            {
                double latitude = camPos.target.latitude;
                double longitude = camPos.target.longitude;
                float bearing = camPos.bearing;
                float tilt = camPos.tilt;
                float zoom = camPos.zoom;

                savedInstanceState.putDouble("longitude", longitude);
                savedInstanceState.putDouble("latitude", latitude);
                savedInstanceState.putFloat("bearing", bearing);
                savedInstanceState.putFloat("tilt", tilt);
                savedInstanceState.putFloat("zoom", zoom);
            }
        }
    }

}
