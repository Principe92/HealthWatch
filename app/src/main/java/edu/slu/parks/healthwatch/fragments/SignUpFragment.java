package edu.slu.parks.healthwatch.fragments;


import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import edu.slu.parks.healthwatch.model.IPinManager;
import edu.slu.parks.healthwatch.model.PinManager;
import edu.slu.parks.healthwatch.utils.Constants;


public class SignUpFragment extends Fragment implements PinLockListener, PinManager.PinManagerListener {

    private PinLockView mPinLockView;
    private boolean firstIntent = true;
    private TextView statusView;
    private String lastPin;
    private FingerprintManager mFingerprintManager;
    private boolean useFingerPrint;
    private SignUpListener mListener;
    private IPinManager pinManager;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = getActivity().getSystemService(FingerprintManager.class);
        }

        pinManager = new PinManager(getContext(), this);
        pinManager.createKeys();
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

        if (isFingerprintAuthAvailable()) {
            (view.findViewById(R.id.fingerprint)).setVisibility(View.VISIBLE);
            CheckBox usePrint = (CheckBox) view.findViewById(R.id.checkBox);
            usePrint.setVisibility(View.VISIBLE);
            usePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    useFingerPrint = b;
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
        if (fingerPrintIsSetUp()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        }
    }

    private boolean fingerPrintIsSetUp() {
        if (!useFingerPrint) return true;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = getActivity().getSystemService(KeyguardManager.class);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.USE_FINGERPRINT},
                        Constants.REQUEST_FINGERPRINT);

                return false;
            }

            if (keyguardManager.isKeyguardSecure() && mFingerprintManager.hasEnrolledFingerprints()) {
                return true;

            } else {
                Snackbar.make(mPinLockView, "Fingerprint is not setup", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Go to settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), 0);
                            }
                        })
                        .show();
                return false;
            }
        }

        return true;
    }

    public boolean isFingerprintAuthAvailable() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // noinspection ResourceType
            return mFingerprintManager != null && mFingerprintManager.isHardwareDetected();
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        next();
    }

    public interface SignUpListener {

    }
}
