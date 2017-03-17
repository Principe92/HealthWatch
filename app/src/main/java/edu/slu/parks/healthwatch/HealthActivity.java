package edu.slu.parks.healthwatch;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import edu.slu.parks.healthwatch.adapter.HealthPagerAdapter;
import edu.slu.parks.healthwatch.utils.DepthPageTransformer;

public class HealthActivity extends NavigationActivity {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.health);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_health);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new HealthPagerAdapter(getSupportFragmentManager()));
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_health;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (navigationView != null)
            navigationView.setCheckedItem(activeSection);
    }

}
