package com.example.skoparov.bastardmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class BastardMainActivity
        extends
        BastardBasicBoundActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback
{
    private BastardLogger mLogger;
    private BastardMapManager mMapManager;
    private boolean mPermissionsGranted;

    @Override
    protected void createServiceDependant()
    {
        super.createServiceDependant();

        createMenus();

        setButtonVisible(false, R.id.path_option);
        setButtonVisible(false, R.id.path_pause_continue_option);
        setButtonVisible(false, R.id.service_option);
        setButtonVisible(false, R.id.track_list_option);

        restoreLogger();

        // Create map stuff
        mPermissionsGranted = checkLocationAccessPermissions();
        if( mPermissionsGranted )
        {
            mLogger.addEntry(BastardLogger.EntryType.LOG_ENTRY_INFO,
                    "Location permissions granted");
        }
        else
        {
            mLogger.addEntry(BastardLogger.EntryType.LOG_ENTRY_ERROR,
                    "Location permissions denied");
        }

        // create map package for service if it's not running
        if( !BastardLocationUpdateService.IS_RUNNING )
        {
            BastardTracker t =
                    BastardFactory.getBastardTracker(this, 1000, mLogger);

            t.connect();
            t.loadPathsBrief();

            mService.setTracker(t);
            mService.setActivity(this);

            t.getMapPackage().apiClient.connect();
            switchServiceState(false);
        }

        updateServiceButtonTitle(true, false);

        BastardTracker t = mService.getTracker();
        BastardTracker.MapPackage trackerPackage = t.getMapPackage();

        if( trackerPackage.isValid() )
        {
            updatePathStatusButtonTitle(t.isRecording());

            //create map
            mMapManager = BastardFactory.getMapManager(
                    new BastardMapManager.MapManagerPackage(
                            mLogger,
                            trackerPackage.apiClient,
                            trackerPackage.collector));

            TextView debugView = (TextView) findViewById(R.id.tap_text);
            debugView.setBackgroundColor(Color.WHITE);

            mMapManager.setDebugView(debugView); // DEBUG, remove later
            mMapManager.getMapAsync(this);

            trackerPackage.eventsHandler.addCallbackInterface(mMapManager);
            t.subscribePaintInterface(mMapManager);
            restoreMapState();

            // add map to view
            getSupportFragmentManager().beginTransaction().add(
                    R.id.fragment_container,
                    mMapManager).commit();

            //show buttons
            setButtonVisible(true, R.id.service_option);
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
        if (id == R.id.track_list_option)
        {
            startTrackListActivity();
        }
        else if(id == R.id.service_option)
        {
            boolean currentState = BastardLocationUpdateService.IS_RUNNING;
            switchServiceState(currentState);
        }
        else if(id == R.id.path_option)
        {
            boolean newMode = !mService.getTracker().isRecording();
            if(newMode)
            {
                mMapManager.startNewPath();
                mService.getTracker().startPath();
            } else
            {
                mService.getTracker().stopPath();
            }

            updatePathStatusButtonTitle(newMode);
            updatePathPauseButtonTitle(mService.getTracker().isPaused());

            setButtonVisible(newMode, R.id.path_pause_continue_option);
        }
        else if(id == R.id.path_pause_continue_option)
        {
            BastardTracker t = mService.getTracker();
            boolean newMode = !t.isPaused();
            t.setPaused(newMode);

            updatePathPauseButtonTitle( newMode );
        }
        else if(id == R.id.exit_option)
        {
            if(BastardLocationUpdateService.IS_RUNNING)
            {
                switchServiceState(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        saveMapState(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMapManager.onMapReady(googleMap);

        if( mPermissionsGranted )
        {
            setButtonVisible(true, R.id.path_option);
            setButtonVisible(true, R.id.track_list_option);

            if (mService.getTracker().isRecording()) {
                setButtonVisible(true, R.id.path_pause_continue_option);

                updatePathPauseButtonTitle(mService.getTracker().isPaused());
            }
        }
    }

    private void createMenus()
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

    private void restoreLogger()
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
        startActivity(intent);
    }

    private void startTrackListActivity()
    {
        Intent intent = new Intent(this, BastardPathListActivity.class);
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

    private boolean checkLocationAccessPermissions()
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

    private void setButtonTitle( String title, int id )
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(id).setTitle(title);
    }

    private void setButtonEnabled( boolean isEnabled, int id )
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(id).setEnabled(isEnabled);
    }

    private void setButtonVisible( boolean isVisible, int id )
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(id).setVisible(isVisible);
    }

    //updaters
    private void updatePathStatusButtonTitle( boolean isRunning )
    {
        String serviceButtonTitle;

        serviceButtonTitle = isRunning?
                BastardConstants.GUI.BUTTON_PATH_IS_ONGOING :
                BastardConstants.GUI.BUTTON_NO_ONGOING_PATH;

        setButtonTitle(serviceButtonTitle, R.id.path_option);
    }

    //updaters
    private void updatePathPauseButtonTitle( boolean isPaused )
    {
        String serviceButtonTitle;

        serviceButtonTitle = isPaused?
                BastardConstants.GUI.BUTTON_PATH_IS_PAUSED :
                BastardConstants.GUI.BUTTON_PATH_IS_NOT_PAUSED;

        setButtonTitle(serviceButtonTitle, R.id.path_pause_continue_option);
    }

    private void updateServiceButtonTitle(boolean checkService, boolean isRunning )
    {

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

        setButtonTitle(serviceButtonTitle, R.id.service_option);
    }
}
