package edu.slu.parks.healthwatch;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;

import org.joda.time.DateTime;

import java.util.Date;

import edu.slu.parks.healthwatch.database.HealthDb;
import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.model.GraphTask;
import edu.slu.parks.healthwatch.model.GraphType;
import edu.slu.parks.healthwatch.model.ViewType;
import edu.slu.parks.healthwatch.utils.Constants;


public class GraphFragment extends Fragment {

    private GraphType graphType;
    private IHealthDb healthDb;
    private GraphView graphView;
    private GraphListener mListener;
    private GraphTask currentTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            graphType = GraphType.toEnum(getArguments().getInt(Constants.GRAPH_TYPE, 0));
        }

        healthDb = new HealthDb(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GraphFragment.GraphListener) {
            mListener = (GraphFragment.GraphListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        graphView = (GraphView) view.findViewById(R.id.graph);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mListener.onGraphReady();
    }

    public void loadGraph(Date date, ViewType viewType) {
        DateTime now = DateTime.now();
        DateTime dt = new DateTime(date);

        Log.d(getClass().getName(), dt.toString());
        dt = dt.withTime(now.toLocalTime());
        Log.d(getClass().getName(), dt.toString());

        if (cancelPotentialDownload(dt)) {
            currentTask = new GraphTask(healthDb, graphView, dt, graphType);
            currentTask.execute(viewType);
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

    public interface GraphListener {

        void onGraphReady();
    }
}
