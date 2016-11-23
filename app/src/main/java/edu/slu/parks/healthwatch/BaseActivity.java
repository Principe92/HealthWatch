package edu.slu.parks.healthwatch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.Util;
import edu.slu.parks.healthwatch.views.ISection;

/**
 * Created by okorie on 14-Nov-16.
 */

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected int activeSection;
    private List<ISection> sections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        activeSection = getIntent().getIntExtra(Constants.ACTIVE_SECTION, R.id.nav_measure);

        sections = Util.buildNavigationSections();

        restoreValues(savedInstanceState);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        ISection section = getSection(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (section != null && id != activeSection) {
            section.load(this, id);
        }

        return true;
    }

    protected ISection getSection(int id) {
        int size = sections.size();

        for (int i = 0; i < size; i++) {
            if (sections.get(i).isSection(id)) return sections.get(i);
        }

        return null;
    }

    protected void setActionbarTitle(int id) {
        ISection current = getSection(id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null && current != null)
            toolbar.setTitle(current.getTitle());
    }

    private boolean restoreValues(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            activeSection = savedInstanceState.getInt(Constants.ACTIVE_SECTION, R.id.nav_measure);
            ISection current = getSection(activeSection);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (current != null && toolbar != null)
                toolbar.setTitle(current.getTitle());

            return true;
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(Constants.ACTIVE_SECTION, activeSection);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public abstract int getLayoutId();
}
