package edu.slu.parks.healthwatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import edu.slu.parks.healthwatch.model.Record;
import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.views.AlertDialogFragment;

public class RecordActivity extends AppCompatActivity implements AlertDialogFragment.Listener {

    private static final String RECORD_DIALOG = "measure_dialog";
    private EditText commentView;
    private TextView systolicView;
    private TextView diastolicView;
    private boolean includeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        commentView = (EditText) findViewById(R.id.txt_comment);
        systolicView = (TextView) findViewById(R.id.layout_reading_systolic).findViewById(R.id.txt_record_pressure);
        diastolicView = (TextView) findViewById(R.id.layout_reading_diastolic).findViewById(R.id.txt_record_pressure);
        ((TextView) findViewById(R.id.layout_reading_diastolic).findViewById(R.id.txt_reading)).setText("Diastolic (mmHg)");


        Button saveBtn = (Button) findViewById(R.id.btn_save);
        Button repeatBtn = (Button) findViewById(R.id.btn_repeat);
        CheckBox location = (CheckBox) findViewById(R.id.box_location);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
                goHome();
            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeat();
            }
        });

        location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                includeLocation = isChecked;
            }
        });
    }

    private void saveToDatabase() {
        Record record = new Record();
        record.systolic = Integer.parseInt(String.valueOf(systolicView.getText()));
        record.diastolic = Integer.parseInt(String.valueOf(diastolicView.getText()));
        record.comment = String.valueOf(commentView.getText());

        if (includeLocation) {

            // save location
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            record.latitude = sharedPref.getLong(getString(R.string.gps_latitude), 0);
            record.longitude = sharedPref.getLong(getString(R.string.gps_longitude), 0);
        }
    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void repeat() {
        Intent intent = new Intent(this, WaitingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Bundle arg = new Bundle();
        arg.putInt(Constants.TITLE, R.string.cancel_measure);
        arg.putInt(Constants.MESSAGE, R.string.cancel_record_msg);
        arg.putInt(Constants.CANCEL, R.string.no);
        arg.putInt(Constants.OK, R.string.cancel);

        AlertDialogFragment alert = new AlertDialogFragment();
        alert.setArguments(arg);
        alert.show(getSupportFragmentManager(), RECORD_DIALOG);
    }

    @Override
    public void onOkayButtonClick() {
        super.onBackPressed();
    }
}
