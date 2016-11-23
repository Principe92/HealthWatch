package edu.slu.parks.healthwatch;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.slu.parks.healthwatch.database.HealthDb;
import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;
import edu.slu.parks.healthwatch.model.calendar.GraphListener;
import edu.slu.parks.healthwatch.model.calendar.GraphTask;
import edu.slu.parks.healthwatch.model.calendar.ICalendarView;
import edu.slu.parks.healthwatch.model.calendar.IGraph;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.Util;

public class HistoryActivity extends BaseActivity
        implements CompactCalendarView.CompactCalendarViewListener,
        GraphListener, PopupMenu.OnMenuItemClickListener, GraphTask.TaskListener {

    private AppBarLayout mAppBarLayout;
    private IDate date;
    private CompactCalendarView mCompactCalendarView;
    private GraphTask currentTask;
    private IHealthDb healthDb;

    private boolean isExpanded = false;
    private ViewPager mViewPager;
    private DateTime selectedDate;
    private List<ICalendarView> calendarViews;
    private ICalendarView selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendarViews = Util.buildCalendarViews();

        if (savedInstanceState != null) {
            selectedDate = new DateTime(savedInstanceState.getString(Constants.SELECTED_DATE));
            selectedView = getCalendarView(savedInstanceState.getInt(Constants.SELECTED_VIEW, R.id.cal_day));
        } else {
            selectedDate = DateTime.now();
            selectedView = getCalendarView(R.id.cal_day);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        date = new JodaDate(this);
        healthDb = new HealthDb(this);

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
        setCurrentDate(selectedDate.toDate());
    }

    public ICalendarView getCalendarView(int id) {

        for (ICalendarView view : calendarViews) {
            if (view.isView(id)) return view;
        }

        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(Constants.SELECTED_DATE, selectedDate.toString());
        savedInstanceState.putInt(Constants.SELECTED_VIEW, selectedView.getId());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
    public int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public void onDayClick(Date dateClicked) {
        String format = DateTime.now().year().get() < new DateTime(dateClicked).year().get()
                ? Constants.HISTORY_DATE_FORMAT : Constants.NORMAL_DATE_FORMAT;

        setSubtitle(date.toString(format, new DateTime(dateClicked)));
        loadGraph(dateClicked);
        selectedDate = new DateTime(dateClicked);
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        String format = DateTime.now().year().get() < new DateTime(firstDayOfNewMonth).year().get()
                ? Constants.HISTORY_DATE_FORMAT : Constants.NORMAL_DATE_FORMAT;

        setSubtitle(date.toString(format, new DateTime(firstDayOfNewMonth)));
        loadGraph(firstDayOfNewMonth);
        selectedDate = new DateTime(firstDayOfNewMonth);
    }

    @Override
    public void onGraphReady() {
        onDayClick(selectedDate.toDate());
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        ICalendarView view = getCalendarView(item.getItemId());

        if (view != null) {
            selectedView = view;
            view.toggleCheck();
            item.setChecked(view.getStatus());
        }

        return true;
    }

    public void showCalendarViewMenu(MenuItem item) {
        View view = findViewById(R.id.action_calendar_view);

        if (view != null) {
            PopupMenu popup = new PopupMenu(this, view);

            // This activity implements OnMenuItemClickListener
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.calendar);
            popup.getMenu().findItem(selectedView.getId()).setChecked(true);
            popup.show();
        }
    }

    public void loadGraph(Date date) {
        DateTime now = DateTime.now();
        DateTime dt = new DateTime(date);
        dt = dt.withTime(now.toLocalTime());

        if (cancelPotentialDownload(dt)) {
            currentTask = new GraphTask(healthDb, dt);
            currentTask.setmListener(this);
            currentTask.execute();
        }
    }

    private GraphTask getBitmapDownloaderTask(DateTime imageView) {
        if (currentTask != null) {

            if (currentTask.getDate().equals(imageView)) {
                return currentTask;
            } else {
                currentTask.cancel(true);
            }
        }
        return null;
    }

    private boolean cancelPotentialDownload(DateTime dateTime) {
        GraphTask bitmapDownloaderTask = getBitmapDownloaderTask(dateTime);

        return bitmapDownloaderTask == null;
    }

    @Override
    public void onTaskFinished(List<Record> records) {
        int size = mViewPager.getAdapter().getCount();

        for (int i = 0; i < size; i++) {
            String name = Util.makeFragmentName(mViewPager.getId(), i);
            IGraph fragment = (IGraph) getSupportFragmentManager().findFragmentByTag(name);

            if (fragment != null && ((Fragment) fragment).isResumed()) {
                fragment.loadGraph(records, selectedView.getViewType());
            }
        }
    }
}
