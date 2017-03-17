package edu.slu.parks.healthwatch.fragments.graph;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.adapter.TableListAdapter;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;
import edu.slu.parks.healthwatch.model.calendar.GraphListener;
import edu.slu.parks.healthwatch.model.calendar.GraphType;
import edu.slu.parks.healthwatch.model.calendar.ICalendarView;
import edu.slu.parks.healthwatch.model.calendar.IGraph;
import edu.slu.parks.healthwatch.utils.Constants;


public class TableFragment extends Fragment implements IGraph {

    private GraphType graphType;
    private IDate joda;
    private GraphListener mListener;
    private View defaultView;
    private RecyclerView mRecyclerView;
    private TableListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        joda = new JodaDate(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.table);
        defaultView = view.findViewById(R.id.no_record);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new TableListAdapter(joda, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        mListener.onGraphReady();
    }

    @Override
    public void loadGraph(List<Record> records, ICalendarView calendarView) {
        mAdapter.setRecords(records);

        if (records == null || records.size() == 0) {
            defaultView.setVisibility(View.VISIBLE);
        } else {
            defaultView.setVisibility(View.GONE);
        }
    }

    @Override
    public GraphType getType() {
        return GraphType.TABLE;
    }

    @Override
    public Fragment getNewInstance(Bundle arg) {
        TableFragment frag = new TableFragment();
        frag.setArguments(arg);
        return frag;
    }
}
