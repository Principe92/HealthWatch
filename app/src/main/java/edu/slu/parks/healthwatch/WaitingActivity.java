package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Timer;
import java.util.TimerTask;

import edu.slu.parks.healthwatch.utils.Constants;
import edu.slu.parks.healthwatch.views.AlertDialogFragment;

public class WaitingActivity extends AppCompatActivity implements AlertDialogFragment.Listener {

    private static final String MEASURE_DIALOG = "measure_dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.measure);
        setSupportActionBar(toolbar);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(WaitingActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        }, 5000);
    }

    @Override
    public void onBackPressed() {
        Bundle arg = new Bundle();
        arg.putInt(Constants.TITLE, R.string.cancel_measure);
        arg.putInt(Constants.MESSAGE, R.string.cancel_measure_msg);
        arg.putInt(Constants.CANCEL, R.string.no);
        arg.putInt(Constants.OK, R.string.cancel);

        AlertDialogFragment alert = new AlertDialogFragment();
        alert.setArguments(arg);
        alert.show(getSupportFragmentManager(), MEASURE_DIALOG);
    }

    @Override
    public void onOkayButtonClick() {
        super.onBackPressed();
    }
}
