package edu.slu.parks.healthwatch.async;

import android.os.AsyncTask;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.calendar.ICalendarView;

/**
 * Created by okori on 14-Nov-16.
 */

public class RecordListTask extends AsyncTask<ICalendarView, Void, List<Record>> {
    private final IHealthDb healthDb;
    private TaskListener mListener;
    private DateTime date;

    public RecordListTask(IHealthDb healthDb, DateTime dt) {
        this.healthDb = healthDb;
        this.date = dt;
    }


    @Override
    protected List<Record> doInBackground(ICalendarView... calendarViews) {
        ICalendarView view = calendarViews[0];
        Log.d(getClass().getName(), String.format(Locale.getDefault(),
                "Getting records on %s of %s", date.toString(), view.toString()));
        return healthDb.getRecordByDate(date, view);
    }

    @Override
    protected void onPostExecute(List<Record> records) {
        if (mListener != null) mListener.onTaskFinished(records);
    }

    public DateTime getDate() {
        return date;
    }

    public void setmListener(TaskListener mListener) {
        this.mListener = mListener;
    }

    public interface TaskListener {
        void onTaskFinished(List<Record> records);
    }
}
