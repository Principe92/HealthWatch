package edu.slu.parks.healthwatch.help;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import edu.slu.parks.healthwatch.NavigationActivity;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.listener.IHelpListAdapterListener;
import edu.slu.parks.healthwatch.utils.Util;

public class HelpActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        Toolbar toolbar = setUpToolbar(R.string.title_activity_help);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_help);

        RecyclerView mainView = (RecyclerView) findViewById(R.id.list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mainView.setLayoutManager(mLayoutManager);

        final HelpListAdapter mAdapter = new HelpListAdapter(new IHelpListAdapterListener() {
            @Override
            public void onClick(IHelp help) {
                help.onClick(HelpActivity.this.getApplicationContext());
            }
        });
        mainView.setAdapter(mAdapter);

        mAdapter.addAll(Util.getHelpList());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_help;
    }

}
