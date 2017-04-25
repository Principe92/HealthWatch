package edu.slu.parks.healthwatch.history.graph;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.model.calendar.GraphListener;
import edu.slu.parks.healthwatch.model.calendar.GraphType;
import edu.slu.parks.healthwatch.utils.Constants;
import lecho.lib.hellocharts.model.Axis;

/**
 * Created by okori on 12-Jan-17.
 */

public class BaseGraphFragment extends Fragment {

    protected GraphType graphType;
    protected Axis xAxis;
    protected Axis yDiaAxis;
    protected Axis ySysAxis;
    protected GraphListener mListener;
    protected View defaultView;
    protected View graph;


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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            graphType = GraphType.toEnum(getArguments().getInt(Constants.GRAPH_TYPE, 0));
        }

        xAxis = new Axis();

        yDiaAxis = Axis.generateAxisFromRange(0, 300, 50);
        yDiaAxis.setName(getString(R.string.diastolic_text));

        ySysAxis = Axis.generateAxisFromRange(0, 300, 50);
        ySysAxis.setName(getString(R.string.systolic_text));
    }

    @Override
    public void onResume() {
        super.onResume();

        mListener.onGraphReady();
    }
}
