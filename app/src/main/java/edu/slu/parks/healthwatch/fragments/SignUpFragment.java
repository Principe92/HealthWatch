package edu.slu.parks.healthwatch.fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import edu.slu.parks.healthwatch.HomeActivity;
import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.security.FingerPrint;
import edu.slu.parks.healthwatch.security.IFingerPrint;
import edu.slu.parks.healthwatch.security.IPinManager;
import edu.slu.parks.healthwatch.security.PinManager;


public class SignUpFragment extends Fragment implements PinLockListener, PinManager.PinManagerListener {

    private PinLockView mPinLockView;
    private boolean firstIntent = true;
    private TextView statusView;
    private String lastPin;
    private SignUpListener mListener;
    private IPinManager pinManager;
    private IFingerPrint fingerPrint;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pinManager = new PinManager(getContext(), this);
        pinManager.createKeys();

        fingerPrint = new FingerPrint(getContext(), new FingerPrint.FingerPrintListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void requestFingerPrintPermission() {
                fingerPrint.requestFingerPrintPermission(getActivity());
            }

            @Override
            public void addFingerPrints() {
                fingerPrint.addFingerPrints(mPinLockView, getActivity());
            }

            @Override
            public void onAuthenticated() {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpFragment.SignUpListener) {
            mListener = (SignUpFragment.SignUpListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EmailListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        statusView = (TextView) view.findViewById(R.id.status);

        mPinLockView = (PinLockView) view.findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = (IndicatorDots) view.findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(Integer.parseInt(getString(R.string.pinLength)));
        mPinLockView.setPinLockListener(this);

        if (fingerPrint.isFingerprintAuthAvailable()) {
            (view.findViewById(R.id.fingerprint)).setVisibility(View.VISIBLE);
            CheckBox usePrint = (CheckBox) view.findViewById(R.id.checkBox);
            usePrint.setVisibility(View.VISIBLE);
            usePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    fingerPrint.setUseFingerPrint(b);
                }
            });
        }

        return view;
    }

    @Override
    public void onComplete(String pin) {

        if (firstIntent) {
            firstIntent = false;
            statusView.setText(R.string.enter_pin_again);
            lastPin = pin;
            mPinLockView.resetPinLockView();

        } else {

            if (pin.equalsIgnoreCase(lastPin)) {
                showMessage("Pin successfully registered");
                pinManager.savePin(pin);
                next();
            } else {
                firstIntent = true;
                showMessage(getString(R.string.incorrect_pin));
                mPinLockView.resetPinLockView();
                statusView.setText(R.string.enter_your_new_pin);
            }
        }
    }

    @Override
    public void onEmpty() {
    }

    @Override
    public void onPinChange(int pinLength, String intermediatePin) {
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void next() {
        if (!fingerPrint.canUseFingerPrint() || fingerPrint.IsFingerPrintSetUp()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        next();
    }

    public interface SignUpListener {

    }
}
