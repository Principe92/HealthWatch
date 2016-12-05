package edu.slu.parks.healthwatch.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;
import edu.slu.parks.healthwatch.model.ViewType;
import edu.slu.parks.healthwatch.model.calendar.GraphListener;
import edu.slu.parks.healthwatch.model.calendar.GraphType;
import edu.slu.parks.healthwatch.model.calendar.IGraph;
import edu.slu.parks.healthwatch.utils.Constants;


public class TableFragment extends Fragment implements IGraph {

    private GraphType graphType;
    private IDate joda;
    private GraphListener mListener;
    private TableLayout table;

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

        table = (TableLayout) view.findViewById(R.id.table);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mListener.onGraphReady();
    }

    @Override
    public void loadGraph(List<Record> records, ViewType viewType) {

        if (records != null) {
            table.removeAllViewsInLayout();

            addHeader();

            for (Record record :
                    records) {
                addRow(record);
            }

        }
    }

    private void addRow(Record record) {
        TableRow row = new TableRow(getContext());

        TextView date = buildTextView();
        date.setText(joda.toString(Constants.HISTORY_DATE_FORMAT, record.date));
        row.addView(date);

        TextView sys = buildTextView();
        sys.setText(String.valueOf(record.systolic));
        row.addView(sys);

        TextView dia = buildTextView();
        dia.setText(String.valueOf(record.diastolic));
        row.addView(dia);

        table.addView(row);
    }

    private TextView buildTextView() {
        TextView text = new TextView(getContext());
        text.setGravity(Gravity.FILL);
        text.setPadding(0, 8, 0, 8);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        return text;
    }

    private void addHeader() {
        LayoutInflater.from(getContext()).inflate(R.layout.table_header, table, true);
    }
}
