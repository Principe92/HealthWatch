package edu.slu.parks.healthwatch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.noelchew.sparkpostutil.library.EmailListener;

import edu.slu.parks.healthwatch.fragments.EmailFragment;
import edu.slu.parks.healthwatch.fragments.SignInFragment;
import edu.slu.parks.healthwatch.fragments.SignUpFragment;
import edu.slu.parks.healthwatch.security.IPinManager;
import edu.slu.parks.healthwatch.security.IPreference;
import edu.slu.parks.healthwatch.security.PinManager;
import edu.slu.parks.healthwatch.security.Preference;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements EmailFragment.EmailListener,
        SignInFragment.SignInListener, SignUpFragment.SignUpListener, EmailListener, PinManager.PinManagerListener {
    private static final String TAG = "screen";
    private IPreference preference;
    private IPinManager pinManager;
    private boolean goHome;
    private boolean pinVerified;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_login);
        preference = new Preference(this);
        pinManager = new PinManager(this, this);

        Intent intent = getIntent();
        if (intent != null) {
            goHome = intent.getBooleanExtra(Constants.GOHOME, false);
        }

        if (state != null) return;

        Fragment screen = getFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, screen, TAG)
                .commit();

    }

    public Fragment getFragment() {
        return pinManager.hasPin() ? new SignInFragment() : new EmailFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {


        switch (requestCode) {
            case Constants.REQUEST_FINGERPRINT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    SignUpFragment fragment = (SignUpFragment) getSupportFragmentManager().findFragmentByTag(TAG);
                    if (fragment != null)
                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    @Override
    public void saveEmail(String email) {
        preference.saveString(email, getString(R.string.key_email));
    }

    @Override
    public void changeTitle(String title) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setTitle(title);
    }

    @Override
    public void next(boolean pinVerified) {
        this.pinVerified = pinVerified;

        if (goHome) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            onBackPressed();
        }
    }


    @Override
    public void onBackPressed() {
        if (pinVerified)
            super.onBackPressed();
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        showMessage(getString(R.string.reset_mail_sent));
    }

    @Override
    public void onError(String errorMessage) {
        showMessage(errorMessage);
    }
}

