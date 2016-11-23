package edu.slu.parks.healthwatch.model.calendar;

import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.database.Record;

/**
 * Created by okori on 14-Nov-16.
 */

public class GraphTask extends AsyncTask<Void, Void, List<Record>> {
    private final IHealthDb healthDb;
    private TaskListener mListener;
    private DateTime date;

    public GraphTask(IHealthDb healthDb, DateTime dt) {
        this.healthDb = healthDb;
        this.date = dt;
    }


    @Override
    protected List<Record> doInBackground(Void... dateTimes) {
        return healthDb.getRecordByDate(date);
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
