package edu.slu.parks.healthwatch;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Locale;

import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;
import edu.slu.parks.healthwatch.model.ViewType;
import edu.slu.parks.healthwatch.utils.Constants;

public class HistoryActivity extends BaseActivity implements CompactCalendarView.CompactCalendarViewListener, GraphFragment.GraphListener {

    private AppBarLayout mAppBarLayout;
    private IDate date;
    private CompactCalendarView mCompactCalendarView;

    private boolean isExpanded = false;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        date = new JodaDate(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        mCompactCalendarView.setLocale(java.util.TimeZone.getDefault(), Locale.ENGLISH);
        mCompactCalendarView.setShouldDrawDaysHeader(true);
        mCompactCalendarView.setListener(this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new GraphPagerAdapter(getSupportFragmentManager()));

        final ImageView arrow = (ImageView) findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = (RelativeLayout) findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    ViewCompat.animate(arrow).rotation(0).start();
                    mAppBarLayout.setExpanded(false, true);
                    isExpanded = false;
                } else {
                    ViewCompat.animate(arrow).rotation(180).start();
                    mAppBarLayout.setExpanded(true, true);
                    isExpanded = true;
                }
            }
        });

        // Set current date to today
        setCurrentDate(new Date());
    }

    private void loadGraph(Date date, ViewType viewType) {
        int size = mViewPager.getAdapter().getCount();

        for (int i = 0; i < size; i++) {
            String name = makeFragmentName(mViewPager.getId(), i);
            GraphFragment fragment = (GraphFragment) getSupportFragmentManager().findFragmentByTag(name);

            if (fragment != null && fragment.isResumed()) {
                fragment.loadGraph(date, viewType);
            }
        }
    }

    private String makeFragmentName(int viewId, int position) {
        return "android:switcher:" + viewId + ":" + position;
    }

    public void setCurrentDate(Date date) {
        setSubtitle(this.date.toString(Constants.NORMAL_DATE_FORMAT, new DateTime(date)));

        if (mCompactCalendarView != null) {
            mCompactCalendarView.setCurrentDate(date);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = (TextView) findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setSubtitle(String subtitle) {
        TextView datePickerTextView = (TextView) findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public void onDayClick(Date dateClicked) {
        String format = DateTime.now().year().get() < new DateTime(dateClicked).year().get()
                ? Constants.HISTORY_DATE_FORMAT : Constants.NORMAL_DATE_FORMAT;

        setSubtitle(date.toString(format, new DateTime(dateClicked)));
        Log.d(getLocalClassName(), "date clicked");
        loadGraph(dateClicked, ViewType.DAY);
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        String format = DateTime.now().year().get() < new DateTime(firstDayOfNewMonth).year().get()
                ? Constants.HISTORY_DATE_FORMAT : Constants.NORMAL_DATE_FORMAT;

        setSubtitle(date.toString(format, new DateTime(firstDayOfNewMonth)));

        loadGraph(firstDayOfNewMonth, ViewType.MONTH);
    }

    @Override
    public void onGraphReady() {
        onDayClick(new Date());
    }
}
