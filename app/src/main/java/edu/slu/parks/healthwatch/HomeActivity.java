package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import edu.slu.parks.healthwatch.model.ILocation;
import edu.slu.parks.healthwatch.model.MyLocation;
import edu.slu.parks.healthwatch.settings.SettingsActivity;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.views.HealthSection;
import edu.slu.parks.healthwatch.views.HelpSection;
import edu.slu.parks.healthwatch.views.HistorySection;
import edu.slu.parks.healthwatch.views.ISection;
import edu.slu.parks.healthwatch.views.MeasureSection;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MeasureFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        HealthFragment.OnFragmentInteractionListener,
        HistoryFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String ACTIVE_SECTION = "active_section";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String LOCATION_KEY = "location_key";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "allow";
    private int currentFragmentId;
    private List<ISection> sections;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates;
    private AddressResultReceiver mResultReceiver;
    private ILocation gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(this.getLocalClassName(), "onCreate");

        sections = new ArrayList<>();
        sections.add(new HealthSection());
        sections.add(new HelpSection());
        sections.add(new HistorySection());
        sections.add(new MeasureSection());
        sections.add(new HealthSection());

        gps = new MyLocation(this);

        if (retoreValues(savedInstanceState)) return;

        ISection current = getSection(R.id.nav_measure);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_home, current.getFragment()).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(current.getTitle());
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private boolean retoreValues(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            currentFragmentId = savedInstanceState.getInt(ACTIVE_SECTION, R.id.nav_measure);
            ISection current = getSection(currentFragmentId);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (current != null && toolbar != null)
                toolbar.setTitle(current.getTitle());

            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                gps.update((Location) savedInstanceState.getParcelable(LOCATION_KEY));
            }

            return true;
        }

        return false;
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All gps settings are satisfied. The client can
                        // initialize gps requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    HomeActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });

        return mLocationRequest;
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, gps.getLocation());
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(ACTIVE_SECTION, currentFragmentId);
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, gps.getLocation());
        //savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            ISection section = getSection(id);

            if (section != null && !section.isSection(currentFragmentId)) {
                currentFragmentId = id;

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.content_home, section.getFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(item.getTitle());

        return true;
    }

    private ISection getSection(int id) {
        int size = sections.size();

        for (int i = 0; i < size; i++) {
            if (sections.get(i).isSection(id)) return sections.get(i);
        }

        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setActionbarTitle(currentFragmentId);
    }

    private void setActionbarTitle(int id) {
        ISection current = getSection(id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null && current != null)
            toolbar.setTitle(current.getTitle());
    }

    @Override
    public void onMeasureButtonClick() {
        Intent intent = new Intent(this, WaitingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHistoryButtonClick() {
        ISection section = getSection(R.id.nav_history);

        if (section != null) {
            currentFragmentId = R.id.nav_history;
            setActionbarTitle(currentFragmentId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.content_home, section.getFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(Constants.TAG, "mGoogleApi Connected");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        gps.update(LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient));

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        if (gps.isValid()) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                return;
            }

            startIntentService();
        }

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(Constants.TAG, "Connection suspended");
        Toast.makeText(this, "Unable to acquire gps gps", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to acquire gps", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        gps.update(location);
        gps.save();
    }
}
