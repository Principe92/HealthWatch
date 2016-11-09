package edu.slu.parks.healthwatch.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.slu.parks.healthwatch.AddressResultReceiver;
import edu.slu.parks.healthwatch.FetchAddressIntentService;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.database.IWatchDb;
import edu.slu.parks.healthwatch.database.Record;
import edu.slu.parks.healthwatch.database.WatchDb;
import edu.slu.parks.healthwatch.model.IAddressReceiver;
import edu.slu.parks.healthwatch.utils.Constants;


public class MeasureFragment extends Fragment implements IAddressReceiver {

    Record record;
    private OnFragmentInteractionListener mListener;
    private AddressResultReceiver addressResultReceiver;
    private IWatchDb database;
    private TextView locationView;

    public MeasureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addressResultReceiver = new AddressResultReceiver(new Handler());
        database = new WatchDb(getActivity());
        record = database.getLatestReading();

        getAddressFromLocation(record.latitude, record.longitude);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measure, container, false);

        Button button = (Button) view.findViewById(R.id.btn_measure);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMeasureButtonClick();
            }
        });

        Button historyBtn = (Button) view.findViewById(R.id.btn_history);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onHistoryButtonClick();
            }
        });

        TextView diastolicView = (TextView) (view.findViewById(R.id.layout_diastolic)).findViewById(R.id.txt_pressure);
        TextView systolicView = (TextView) (view.findViewById(R.id.layout_systolic)).findViewById(R.id.txt_pressure);
        locationView = (TextView) view.findViewById(R.id.txt_location);
        TextView dateView = (TextView) view.findViewById(R.id.txt_date);

        ((TextView) view.findViewById(R.id.layout_systolic).findViewById(R.id.txt_pressure_metric)).setText("Systolic (mmHg)");

        systolicView.setText(String.valueOf(record.systolic));
        diastolicView.setText(String.valueOf(record.diastolic));

        dateView.setText(record.date.toString());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Log.d(this.getClass().getName(), "longitude: " + longitude);
        Log.d(this.getClass().getName(), "latitude: " + latitude);

        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);
        getActivity().startService(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAddressReceived(int resultCode, Bundle resultData) {
        String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

        if (resultCode == Constants.SUCCESS_RESULT) {
            locationView.setText(mAddressOutput);
            locationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (addressResultReceiver != null)
            addressResultReceiver.setReceiver(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (addressResultReceiver != null) {
            addressResultReceiver.setReceiver(this);
        }
    }

    public interface OnFragmentInteractionListener {
        void onMeasureButtonClick();

        void onHistoryButtonClick();
    }
}
