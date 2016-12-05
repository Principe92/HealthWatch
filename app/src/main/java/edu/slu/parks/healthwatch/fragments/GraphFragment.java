package edu.slu.parks.healthwatch.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.DateTime;

import java.util.List;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.ViewType;
import edu.slu.parks.healthwatch.model.calendar.GraphListener;
import edu.slu.parks.healthwatch.model.calendar.GraphType;
import edu.slu.parks.healthwatch.model.calendar.IGraph;
import edu.slu.parks.healthwatch.utils.Constants;


public class GraphFragment extends Fragment implements IGraph {

    private GraphType graphType;
    private GraphView graphView;
    private GraphListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            graphType = GraphType.toEnum(getArguments().getInt(Constants.GRAPH_TYPE, 0));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GraphListener) {
            mListener = (GraphListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GraphListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        graphView = (GraphView) view.findViewById(R.id.graph);
        /*graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setHumanRounding(false);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);
        graphView.getViewport().setMinY(50);

        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mListener.onGraphReady();
    }

    @Override
    public void loadGraph(List<Record> records, ViewType viewType) {
        graphView.removeAllSeries();

        if (records != null && records.size() > 0) {

            int size = records.size();
            Log.d(this.getClass().getName(), "data available with size: " + size);


            DataPoint sys[] = new DataPoint[size];
            DataPoint dias[] = new DataPoint[size];

            for (int i = 0; i < size; i++) {
                Record r = records.get(i);
                double day = 1.0 * getValueByType(r.date, viewType);
                double sysValue = 1.0 * r.systolic;
                double diasValue = 1.0 * r.diastolic;

                sys[i] = new DataPoint(day, sysValue);
                dias[i] = new DataPoint(day, diasValue);


                Log.d(this.getClass().getName(), "d: " + r.diastolic + " s: " + r.systolic + " date: " + r.date.toString());
            }

            addSeriesByType(sys, dias, records.get(0).date, records.get(size - 1).date);
        }
    }

    private int getValueByType(DateTime dateTime, ViewType viewType) {
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

    private void addSeriesByType(DataPoint[] sys, DataPoint[] dias, DateTime min, DateTime max) {
        switch (graphType) {
            case LiNE:
                LineGraphSeries<DataPoint> sysSeries = new LineGraphSeries<>(sys);
                sysSeries.setDrawDataPoints(true);
                sysSeries.setDataPointsRadius(10);
                sysSeries.setTitle("Systolic (mmHg)");
                sysSeries.setColor(Color.RED);

                LineGraphSeries<DataPoint> diaSeries = new LineGraphSeries<>(dias);
                diaSeries.setDrawDataPoints(true);
                diaSeries.setDataPointsRadius(10);
                diaSeries.setTitle("Diastolic (mmHg)");

                graphView.addSeries(sysSeries);
                graphView.addSeries(diaSeries);
                break;

            case BAR:
                BarGraphSeries<DataPoint> barSysSeries = new BarGraphSeries<>(sys);
                barSysSeries.setColor(Color.RED);
                //barSysSeries.setDrawValuesOnTop(true);
                //barSysSeries.setValuesOnTopColor(Color.RED);
                barSysSeries.setSpacing(10);
                barSysSeries.setTitle("Systolic (mmHg)");

                BarGraphSeries<DataPoint> barDiaSeries = new BarGraphSeries<>(dias);
                //barDiaSeries.setDrawValuesOnTop(true);
                //barDiaSeries.setValuesOnTopColor(Color.BLUE);
                barDiaSeries.setSpacing(10);
                barDiaSeries.setTitle("Diastolic (mmHg)");

                graphView.addSeries(barSysSeries);
                graphView.addSeries(barDiaSeries);
                break;

            default:
                break;
        }

        // set date label formatter
       /* graphView.getGridLabelRenderer().setNumHorizontalLabels(dias.length); // only 4 because of the space
        graphView.getViewport().setMinX(min.getMillis());
        graphView.getViewport().setMaxX(max.getMillis());*/
    }
}
