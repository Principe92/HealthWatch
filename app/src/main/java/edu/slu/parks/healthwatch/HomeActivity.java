package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import edu.slu.parks.healthwatch.settings.SettingsActivity;
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
        HistoryFragment.OnFragmentInteractionListener {

    private int currentFragmentId;
    private List<ISection> sections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState != null) {
            return;
        }

        sections = new ArrayList<>();
        sections.add(new HealthSection());
        sections.add(new HelpSection());
        sections.add(new HistorySection());
        sections.add(new MeasureSection());
        sections.add(new HealthSection());

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            currentFragmentId = id;
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
    public void onMeasureButtonClick() {
        Intent intent = new Intent(this, WaitingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHistoryButtonClick() {
        ISection section = getSection(R.id.nav_history);

        currentFragmentId = R.id.nav_history;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        assert section != null;
        transaction.replace(R.id.content_home, section.getFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
