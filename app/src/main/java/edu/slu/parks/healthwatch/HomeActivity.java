package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.slu.parks.healthwatch.database.HealthDb;
import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.fragments.HealthFragment;
import edu.slu.parks.healthwatch.fragments.HelpFragment;
import edu.slu.parks.healthwatch.fragments.HistoryFragment;
import edu.slu.parks.healthwatch.fragments.MeasureFragment;
import edu.slu.parks.healthwatch.model.IAddressReceiver;
import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;
import edu.slu.parks.healthwatch.utils.AddressResultReceiver;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.FetchAddressIntentService;
import edu.slu.parks.healthwatch.views.ISection;


public class HomeActivity extends NavigationActivity
        implements
        MeasureFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        HealthFragment.OnFragmentInteractionListener,
        HistoryFragment.OnFragmentInteractionListener,
        IAddressReceiver {

    private Record record;
    private AddressResultReceiver addressResultReceiver;
    private IHealthDb database;
    private IDate date;
    private TextView locationView;
    private TextView diastolicView;
    private TextView systolicView;
    private TextView dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addressResultReceiver = new AddressResultReceiver(new Handler());
        database = new HealthDb(this);
        date = new JodaDate(this);

        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.measure);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_measure);

        Button button = (Button) findViewById(R.id.btn_measure);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, WaitingActivity.class);
                startActivity(intent);
            }
        });

        Button historyBtn = (Button) findViewById(R.id.btn_history);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHistoryButtonClick();
            }
        });

        diastolicView = (TextView) (findViewById(R.id.layout_diastolic)).findViewById(R.id.txt_pressure);
        systolicView = (TextView) (findViewById(R.id.layout_systolic)).findViewById(R.id.txt_pressure);
        locationView = (TextView) findViewById(R.id.txt_location);
        dateView = (TextView) findViewById(R.id.txt_date);

        ((TextView) findViewById(R.id.layout_systolic).findViewById(R.id.txt_pressure_metric)).setText(R.string.systolic_text);
    }

    private void getLatestReading() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                record = database.getLatestReading();

                HomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (record != null) {
                            systolicView.setText(String.valueOf(record.systolic));
                            diastolicView.setText(String.valueOf(record.diastolic));
                            dateView.setText(date.toString(Constants.DATE_FORMAT, record.date));

                            locationView.setText(R.string.loading_location);
                            getAddressFromLocation(record.latitude, record.longitude);
                        } else {
                            Toast.makeText(HomeActivity.this, R.string.no_records_available, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);
        this.startService(intent);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (addressResultReceiver != null)
            addressResultReceiver.setReceiver(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (addressResultReceiver != null) {
            addressResultReceiver.setReceiver(this);
        }

        getLatestReading();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setActionbarTitle(activeSection);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHistoryButtonClick() {
        ISection section = getSection(R.id.nav_history);

        if (section != null)
            section.load(this, R.id.nav_history);
    }

    @Override
    public void onAddressReceived(int resultCode, Bundle resultData) {
        String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

        if (resultCode == Constants.SUCCESS_RESULT) {
            locationView.setText(mAddressOutput);
            locationView.setVisibility(View.VISIBLE);
        } else {
            locationView.setText(R.string.unknown_location);
        }
    }
}
