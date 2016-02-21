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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class BastardMainActivity extends BastardBasicBoundActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private BastardLogger mLogger;
    private BastardMapManager mMapManager;

    @Override
    protected void createServiceDependant()
    {
        super.createServiceDependant();

        createMenus();
        restoreLogger();

        // Create map stuff
        if( checkPermissions() )
        {
            mLogger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO,
                    "Location permissions granted");
        }
        else
        {
            mLogger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                    "Location permissions denied");
            return;
        }

        // create map package for service if it's not running
        if( !BastardLocationUpdateService.IS_RUNNING )
        {
            BastardTracker t =
                    BastardFactory.getBastardTracker(this, 1000, mLogger);

            mService.setTracker(t);
            mService.setActivity(this);

            t.getMapPackage().apiClient.connect();
            switchServiceState(false);
        }

        updateServiceButtonTitle(true, false);

        BastardTracker t = mService.getTracker();
        if( t.getMapPackage().isValid() )
        {
            BastardTracker.MapPackage p = t.getMapPackage();
            TextView debugView = (TextView) findViewById(R.id.tap_text); //TODO remove later

            //create map
            mMapManager = BastardFactory.getMapManager(p.apiClient, mLogger);
            mMapManager.setTestTextView(debugView); // DEBUG, remove later
            mMapManager.getMapAsync(mMapManager);

            p.eventsHandler.addCallbackInterface(mMapManager);
            restoreMapState();

            // add map to view
            getSupportFragmentManager().beginTransaction().add(
                    R.id.fragment_container,
                    mMapManager).commit();
        }
    }

    public void onServiceStatusChanged( boolean isRunning )
    {
        updateServiceButtonTitle(false, isRunning);
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
        mLogger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                "on destroy"); //TODO remove later

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

        if (id == R.id.log_option)
        {
            startLogActivity();
        }
        else if(id == R.id.service_option)
        {
            boolean currentState = BastardLocationUpdateService.IS_RUNNING;
            switchServiceState(currentState);
        }
        else if(id == R.id.path_option)
        {
            if(mMapManager.getPainter() != null)
            {
                boolean isRunning = mService.getTracker().isRecording();
                if(!isRunning)
                {
                    mMapManager.startNewPath();
                    mService.getTracker().startTrack();
                }
                else
                {
                    mService.getTracker().stopTrack();
                }

                updatePathStatusButtonTitle(!isRunning);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        saveMapState(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Custom functions

    void createMenus()
    {
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
    }

    void restoreLogger()
    {
        if(BastardLocationUpdateService.IS_RUNNING && mService != null)
        {
            BastardTracker.MapPackage p =
                    mService.getTracker().getMapPackage();

            if( p.isValid() ){
                mLogger = p.logger;
            }
        }
        if( mLogger == null )
        {
            mLogger = BastardFactory.getLogger();
        }
    }

    private void startLogActivity()
    {
        Intent intent = new Intent(this, BastardLogActivity.class);
        intent.putExtra(BastardConstants.MISC.LOG_KEY, mLogger.getSerializedLog());

        startActivity(intent);
    }

    private void switchServiceState( boolean currentState )
    {
        Intent service = new Intent(BastardMainActivity.this, BastardLocationUpdateService.class);

        if ( !currentState)
        {
            service.setAction(BastardConstants.ACTION.STARTFOREGROUND_ACTION);
        }
        else
        {
            service.setAction(BastardConstants.ACTION.STOPFOREGROUND_ACTION);
        }

        startService(service);
    }

    private boolean checkPermissions()
    {
        return ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == BastardConstants.REQUESTS.REQUEST_CHECK_SETTINGS)
        {
            if (resultCode == RESULT_OK )
            {
                mService.getTracker().getMapPackage().subscriber.addLocationRequestSettings();
            }
            else
            {
                mLogger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                        "RESOLUTION_REQUIRED asking failed");
            }
        }
    }

    private void saveMapState(Bundle savedInstanceState)
    {
        if( mMapManager != null )
        {
            mService.getTracker().saveMapState(mMapManager.getMapState());
        }
    }

    private void restoreMapState( )
    {
        if( mMapManager != null)
        {
            BastardMapState state = mService.getTracker().getPrevMapState();

            if( state != null )
            {
                mMapManager.setMapState( state);
            }
        }
    }

    private void updatePathStatusButtonTitle( boolean isRunning )
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        String serviceButtonTitle;

            serviceButtonTitle = isRunning?
                    BastardConstants.GUI.BUTTON_PATH_IS_ONGOING :
                    BastardConstants.GUI.BUTTON_NO_ONGOING_PATH;

        navigationView.getMenu().findItem(R.id.path_option).setTitle(serviceButtonTitle);
    }

    private void updateServiceButtonTitle(boolean checkService, boolean isRunning )
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        String serviceButtonTitle;

        if(checkService)
        {
            serviceButtonTitle = BastardLocationUpdateService.IS_RUNNING?
                    BastardConstants.GUI.BUTTON_SERVICE_IS_RUNNING :
                    BastardConstants.GUI.BUTTON_SERVICE_IS_DORMANT;
        }
        else
        {
            serviceButtonTitle = isRunning?
                    BastardConstants.GUI.BUTTON_SERVICE_IS_RUNNING :
                    BastardConstants.GUI.BUTTON_SERVICE_IS_DORMANT;
        }

        navigationView.getMenu().findItem(R.id.service_option).setTitle(serviceButtonTitle);
    }
}
