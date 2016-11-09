package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import edu.slu.parks.healthwatch.fragments.HealthFragment;
import edu.slu.parks.healthwatch.fragments.HelpFragment;
import edu.slu.parks.healthwatch.fragments.HistoryFragment;
import edu.slu.parks.healthwatch.fragments.MeasureFragment;
import edu.slu.parks.healthwatch.fragments.SettingsFragment;
import edu.slu.parks.healthwatch.views.HealthSection;
import edu.slu.parks.healthwatch.views.HelpSection;
import edu.slu.parks.healthwatch.views.HistorySection;
import edu.slu.parks.healthwatch.views.ISection;
import edu.slu.parks.healthwatch.views.MeasureSection;
import edu.slu.parks.healthwatch.views.SettingsSection;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MeasureFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        HealthFragment.OnFragmentInteractionListener,
        HistoryFragment.OnFragmentInteractionListener {

    private static final String ACTIVE_SECTION = "active_section";
    private static final String LOCATION_KEY = "location_key";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "allow";
    private int activeSection;
    private List<ISection> sections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sections = new ArrayList<>();
        sections.add(new HealthSection());
        sections.add(new HelpSection());
        sections.add(new HistorySection());
        sections.add(new MeasureSection());
        sections.add(new HealthSection());
        sections.add(new SettingsSection());

        if (restoreValues(savedInstanceState)) return;

        initViews();
    }

    private void initViews() {
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
    }

    private boolean restoreValues(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            activeSection = savedInstanceState.getInt(ACTIVE_SECTION, R.id.nav_measure);
            ISection current = getSection(activeSection);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (current != null && toolbar != null)
                toolbar.setTitle(current.getTitle());

            return true;
        }

        return false;
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
    protected void onRestart() {
        super.onRestart();

        setActionbarTitle(activeSection);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(ACTIVE_SECTION, activeSection);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        ISection section = getSection(id);

        if (section != null && !section.isSection(activeSection)) {
            activeSection = id;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.content_home, section.getFragment(), getString(section.getTitle()));
            transaction.addToBackStack(null);
            transaction.commit();
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

    private void setActionbarTitle(int id) {
        ISection current = getSection(id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null && current != null)
            toolbar.setTitle(current.getTitle());
    }

    @Override
    public void onMeasureButtonClick() {
        Intent intent = new Intent(this, WaitingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onHistoryButtonClick() {
        ISection section = getSection(R.id.nav_history);

        if (section != null) {
            activeSection = R.id.nav_history;
            setActionbarTitle(activeSection);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.content_home, section.getFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        SettingsFragment m = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.settings));
        if (m != null) m.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
