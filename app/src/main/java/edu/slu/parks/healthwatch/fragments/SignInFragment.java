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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.model.EmailMessage;
import edu.slu.parks.healthwatch.model.IMail;
import edu.slu.parks.healthwatch.model.Mail;
import edu.slu.parks.healthwatch.security.IPreference;
import edu.slu.parks.healthwatch.security.Preference;
import edu.slu.parks.healthwatch.utils.Constants;

public class SignInFragment extends Fragment implements PinLockListener {

    private PinLockView mPinLockView;
    private TextView statusView;
    private FingerprintManager mFingerprintManager;
    private SignInListener mListener;
    private IMail mailSender;
    private IPreference preference;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = getActivity().getSystemService(FingerprintManager.class);
        }

        mailSender = new Mail(getActivity());
        preference = new Preference(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignInFragment.SignInListener) {
            mListener = (SignInFragment.SignInListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignInFragment.SignInListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        statusView = (TextView) view.findViewById(R.id.status);

        mPinLockView = (PinLockView) view.findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = (IndicatorDots) view.findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(Integer.parseInt(getString(R.string.pinLength)));
        mPinLockView.setPinLockListener(this);

        if (isFingerprintAuthAvailable()) {
            (view.findViewById(R.id.fingerprint)).setVisibility(View.VISIBLE);
            TextView usePrint = (TextView) view.findViewById(R.id.checkBox);
            usePrint.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onComplete(String pin) {
        if (mListener != null) {
            if (mListener.isPinValid(pin)) {
                mListener.next();
            } else {
                Snackbar.make(mPinLockView, "Pin is incorrect", Snackbar.LENGTH_INDEFINITE)
                        .setAction("reset pin code", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EmailMessage message = createResetMessage();
                                mailSender.sendMail(message);
                            }
                        })
                        .show();
            }
        }
    }

    private EmailMessage createResetMessage() {
        String to = preference.getString(getString(R.string.key_email));
        String from = "pcokorie@gmail.com";
        String header = "HealthWatch - Password reset";
        String message = String.format("Test");

        return new EmailMessage(to, from, header, message);
    }

    @Override
    public void onEmpty() {
    }

    @Override
    public void onPinChange(int pinLength, String intermediatePin) {
    }

    private void showMessage(String msg) {
        Snackbar.make(mPinLockView, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void next() {
        if (fingerPrintIsSetUp()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Intent intent = new Intent(getActivity(), HomeActivity.class);
                    // startActivity(intent);
                    showMessage("Launch next activity");
                }
            }, 1000);
        }
    }

    private boolean fingerPrintIsSetUp() {

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

    public interface SignInListener {
        boolean isPinValid(String pin);

        void next();
    }
}
