package edu.slu.parks.healthwatch.history.graph;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.async.AddressDownloader;
import edu.slu.parks.healthwatch.database.HealthDb;
import edu.slu.parks.healthwatch.database.IHealthDb;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.model.IDate;
import edu.slu.parks.healthwatch.model.JodaDate;
import edu.slu.parks.healthwatch.model.pressure.PressureType;
import edu.slu.parks.healthwatch.utils.Constants;

public class RecordFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    TextView systolicView;
    TextView diastolicView;
    TextView locationView;
    TextView commentView;
    TextView dateView;
    View layout;
    private IHealthDb database;
    private AddressDownloader addressDownloader;
    private int recordId;
    private IDate joda;
    private TextView statusView;

    public RecordFragment() {
        // Required empty public constructor
    }

    public static RecordFragment newInstance(Record record) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, record.id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            recordId = getArguments().getInt(ARG_PARAM1);
        }

        database = new HealthDb(getContext());
        addressDownloader = new AddressDownloader(getContext());
        joda = new JodaDate(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_record, container, false);
        systolicView = (TextView) layout.findViewById(R.id.txt_systolic);
        diastolicView = (TextView) layout.findViewById(R.id.txt_diastolic);
        locationView = (TextView) layout.findViewById(R.id.txt_location);
        commentView = (TextView) layout.findViewById(R.id.txt_comment);
        dateView = (TextView) layout.findViewById(R.id.txt_date);
        statusView = (TextView) layout.findViewById(R.id.txt_status);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Record record = database.getRecord(recordId);

                if (record != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            systolicView.setText(String.format(Locale.getDefault(), "Systolic: %d mmHg", record.systolic));
                            diastolicView.setText(String.format(Locale.getDefault(), "Diastolic: %d mmHg", record.diastolic));
                            statusView.setText(String.format("Status: %s", PressureType.GetType(record.diastolic).toString()));

                            String cmt = record.comment != null ? record.comment : "";
                            commentView.setText(cmt);
                            dateView.setText(joda.toString(Constants.DATE_FORMAT, record.date));

                            addressDownloader.download(record, locationView);
                            layout.setBackgroundResource(PressureType.GetType(record.diastolic).getColor());
                        }
                    });
                }
            }
        }).start();
    }
}
