package edu.slu.parks.healthwatch.model;

import android.os.AsyncTask;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.util.List;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.database.Record;

/**
 * Created by okori on 14-Nov-16.
 */

public class GraphTask extends AsyncTask<ViewType, Void, List<Record>> {
    private final IHealthDb healthDb;
    private final DateTime date;
    private final GraphType graphType;
    private WeakReference<GraphView> graphViewWeakReference;
    private ViewType viewType;

    public GraphTask(IHealthDb healthDb, GraphView graphView, DateTime date, GraphType graphType) {
        this.healthDb = healthDb;
        this.graphViewWeakReference = new WeakReference<>(graphView);
        this.date = date;
        this.graphType = graphType;
    }


    @Override
    protected List<Record> doInBackground(ViewType... views) {
        viewType = views[0];
        return healthDb.getRecordByDate(date);
    }

    @Override
    protected void onPostExecute(List<Record> records) {
        GraphView view = graphViewWeakReference.get();
        if (view != null) view.removeAllSeries();

        if (view != null && records != null && records.size() > 0) {

            int size = records.size();
            Log.d(this.getClass().getName(), "data available with size: " + size);


            DataPoint sys[] = new DataPoint[size];
            DataPoint dias[] = new DataPoint[size];

            for (int i = 0; i < size; i++) {
                Record r = records.get(i);
                double day = 1.0 * getValueByType(r.date);
                double sysValue = 1.0 * r.systolic;
                double diasValue = 1.0 * r.diastolic;

                sys[i] = new DataPoint(day, sysValue);
                dias[i] = new DataPoint(day, diasValue);


                Log.d(this.getClass().getName(), "d: " + r.diastolic + " s: " + r.systolic + " date: " + r.date.toString());
            }

            addSeriesByType(sys, dias, view);
        }
    }

    public DateTime getDate() {
        return date;
    }

    private int getValueByType(DateTime dateTime) {
        switch (viewType) {
            case DAY:
                return dateTime.getHourOfDay();

            case WEEK:
                return dateTime.getDayOfWeek();

            case MONTH:
                return dateTime.getWeekyear();

            case YEAR:
                return dateTime.getMonthOfYear();

            default:
                return dateTime.getHourOfDay();
        }
    }

    private void addSeriesByType(DataPoint[] sys, DataPoint[] dias, GraphView view) {
        switch (graphType) {
            case LiNE:
                LineGraphSeries<DataPoint> sysSeries = new LineGraphSeries<>(sys);
                sysSeries.setColor(R.color.wallet_secondary_text_holo_dark);

                LineGraphSeries<DataPoint> diaSeries = new LineGraphSeries<>(dias);

                view.addSeries(sysSeries);
                view.addSeries(diaSeries);
                break;

            case BAR:
                BarGraphSeries<DataPoint> barSysSeries = new BarGraphSeries<>(sys);
                barSysSeries.setColor(R.color.wallet_secondary_text_holo_dark);

                BarGraphSeries<DataPoint> barDiaSeries = new BarGraphSeries<>(dias);

                view.addSeries(barSysSeries);
                view.addSeries(barDiaSeries);
                break;

            default:
                break;
        }
    }
}
