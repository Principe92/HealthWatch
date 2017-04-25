package edu.slu.parks.healthwatch.history;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.slu.parks.healthwatch.NavigationActivity;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.adapter.GraphPagerAdapter;
import edu.slu.parks.healthwatch.async.RecordListTask;
import edu.slu.parks.healthwatch.database.HealthDb;
import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.history.graph.RecordFragment;
import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;
import edu.slu.parks.healthwatch.model.calendar.GraphListener;
import edu.slu.parks.healthwatch.model.calendar.ICalendarView;
import edu.slu.parks.healthwatch.model.calendar.IGraph;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.utils.DepthPageTransformer;
import edu.slu.parks.healthwatch.utils.Util;

public class HistoryActivity extends NavigationActivity
        implements CompactCalendarView.CompactCalendarViewListener,
        GraphListener, PopupMenu.OnMenuItemClickListener, RecordListTask.TaskListener {

    private AppBarLayout mAppBarLayout;
    private IDate date;
    private List<IGraph> graphs;
    private CompactCalendarView mCompactCalendarView;
    private RecordListTask currentTask;
    private IHealthDb healthDb;

    private boolean isExpanded = false;
    private ViewPager mViewPager;
    private DateTime selectedDate;
    private List<ICalendarView> calendarViews;
    private ICalendarView selectedView;
    private List<Record> records;
    private TextView calendarStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendarViews = Util.buildCalendarViews();
        graphs = Util.buildGraphs();

        if (savedInstanceState != null) {
            selectedDate = new DateTime(savedInstanceState.getString(Constants.SELECTED_DATE));
            selectedView = getCalendarView(savedInstanceState.getInt(Constants.SELECTED_VIEW, R.id.cal_day));
        } else {
            selectedDate = DateTime.now();
            selectedView = getCalendarView(R.id.cal_day);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        date = new JodaDate(this);
        healthDb = new HealthDb(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        ((DrawerLayout) findViewById(R.id.drawer_layout)).addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        calendarStatus = (TextView) findViewById(R.id.calendar_view);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        final ImageView arrow = (ImageView) findViewById(R.id.date_picker_arrow);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        mCompactCalendarView.setLocale(java.util.TimeZone.getDefault(), Locale.ENGLISH);
        mCompactCalendarView.setShouldDrawDaysHeader(true);
        mCompactCalendarView.setListener(this);
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if ((Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) && !isExpanded) {
//                    isExpanded = false;
//                    ViewCompat.animate(arrow).rotation(0).start();
                    //   Toast.makeText(getApplicationContext(), "Collapsed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new GraphPagerAdapter(getSupportFragmentManager(), graphs));
        mViewPager.setPageTransformer(true, new DepthPageTransformer());


        findViewById(R.id.date_picker_button).setOnClickListener(new View.OnClickListener() {
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
        setCalendarStatus(selectedView.getName());
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

    private void setCalendarStatus(String title) {
        calendarStatus.setText(title);
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
        setSubtitle(date.toString(Constants.HISTORY_DATE_FORMAT, new DateTime(dateClicked)));
        loadGraph(dateClicked, selectedView);
        selectedDate = new DateTime(dateClicked);
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        setSubtitle(date.toString(Constants.HISTORY_DATE_FORMAT, new DateTime(firstDayOfNewMonth)));
        loadGraph(firstDayOfNewMonth, selectedView);
        selectedDate = new DateTime(firstDayOfNewMonth);
    }

    @Override
    public void onGraphReady() {
        onDayClick(selectedDate.toDate());
    }

    @Override
    public void showRecordDetails(int index) {
        Log.d(getClass().getName(), "record id: " + index);
        Record record = this.records.get(index);

        if (record != null) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogFragment newFragment = RecordFragment.newInstance(record);
            newFragment.show(ft, "dialog");
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        ICalendarView view = getCalendarView(item.getItemId());

        if (view != null) {
            ICalendarView last = selectedView;
            selectedView = view;

            if (last != selectedView) onDayClick(selectedDate.toDate());

            view.toggleCheck();
            item.setChecked(view.getStatus());
        }

        setCalendarStatus(selectedView.getName());
        return true;
    }

    public void showCalendarViewMenu(MenuItem item) {
        View view = findViewById(R.id.action_calendar_view);

        if (view != null) {
            PopupMenu popup = new PopupMenu(this, view);

            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.calendar);
            popup.getMenu().findItem(selectedView.getId()).setChecked(true);
            popup.show();
        }
    }

    public void loadGraph(Date date, ICalendarView view) {
        DateTime now = DateTime.now();
        DateTime dt = new DateTime(date);
        dt = dt.withTime(now.toLocalTime());

        if (cancelPotentialDownload(dt)) {
            currentTask = new RecordListTask(healthDb, dt);
            currentTask.setmListener(this);
            currentTask.execute(view);
        }
    }

    private RecordListTask getGraphTask(DateTime date) {
        if (currentTask != null) {

            if (currentTask.getDate().equals(date)) {
                return currentTask;
            } else {
                currentTask.cancel(true);
            }
        }
        return null;
    }

    private boolean cancelPotentialDownload(DateTime dateTime) {
        RecordListTask recordListTask = getGraphTask(dateTime);

        return recordListTask == null;
    }

    @Override
    public void onTaskFinished(List<Record> records) {
        int size = mViewPager.getAdapter().getCount();
        this.records = records == null ? new ArrayList<Record>() : records;

        for (int i = 0; i < size; i++) {
            String name = Util.makeFragmentName(mViewPager.getId(), i);
            IGraph fragment = (IGraph) getSupportFragmentManager().findFragmentByTag(name);

            if (fragment != null && ((Fragment) fragment).isResumed()) {
                fragment.loadGraph(records, selectedView);
            }
        }
    }
}
