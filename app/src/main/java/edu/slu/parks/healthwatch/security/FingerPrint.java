package edu.slu.parks.healthwatch.security;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import edu.slu.parks.healthwatch.R;
import edu.slu.parks.healthwatch.utils.Constants;

/**
 * Created by okori on 17-Jan-17.
 */
public class FingerPrint implements IFingerPrint {

    static final String DEFAULT_KEY_NAME = "default_key";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    private final Context context;
    private final IPreference preference;
    private final IEncryption encryption;
    private final FingerPrintListener mListener;
    private FingerprintManager mFingerprintManager;
    private CancellationSignal mCancellationSignal;
    private boolean mSelfCancelled;
    private Cipher cipher;
    private String keyName;

    public FingerPrint(Context context, FingerPrintListener mListener) {
        this.context = context;
        this.preference = new Preference(context);
        this.encryption = new Encryption(context, DEFAULT_KEY_NAME);
        this.mListener = mListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = context.getSystemService(FingerprintManager.class);
            buildCipher();
        }
    }

    @Override
    public void startListening() {
        if (!canUseFingerPrint()) return;
        if (!isFingerprintAuthAvailable()) return;
        if (!initCipher()) return;

        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    if (!mSelfCancelled) {
                        showError(errString);
                    }
                }

                @Override
                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                    showError(helpString);
                }

                private void showError(CharSequence string) {
                    Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    showError("Fingerprint not recognized");
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    mListener.onAuthenticated();
                }
            }, null);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean initCipher() {
        try {
            KeyStore mKeyStore;
            mKeyStore = KeyStore.getInstance(Constants.KEYSTORE);
            mKeyStore.load(null);

            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | InvalidKeyException | java.security.cert.CertificateException | UnrecoverableEntryException ignored) {
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            KeyStore mKeyStore;
            mKeyStore = KeyStore.getInstance(Constants.KEYSTORE);
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }

            KeyGenerator mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, Constants.KEYSTORE);
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException | KeyStoreException | NoSuchProviderException ignored) {
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void buildCipher() {
        Cipher defaultCipher;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher = defaultCipher;
            keyName = DEFAULT_KEY_NAME;
            createKey(keyName, false);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignored) {
        }
    }

    @Override
    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void setUseFingerPrint(boolean use) {
        preference.saveBoolean(use, context.getString(R.string.key_fingerprint));
    }

    @Override
    public boolean canUseFingerPrint() {
        return preference.getBoolean(context.getString(R.string.key_fingerprint));
    }

    @Override
    public boolean isFingerprintAuthAvailable() {

        // noinspection ResourceType
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && mFingerprintManager != null && mFingerprintManager.isHardwareDetected();
    }

    @Override
    public boolean IsFingerPrintSetUp() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = context.getSystemService(KeyguardManager.class);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                    != PackageManager.PERMISSION_GRANTED) {

                if (mListener != null) mListener.requestFingerPrintPermission();

                return false;
            }

            if (keyguardManager.isKeyguardSecure() && mFingerprintManager.hasEnrolledFingerprints()) {
                return true;

            } else {

                if (mListener != null) mListener.addFingerPrints();

                return false;
            }
        }

        return true;
    }

    @Override
    public void requestFingerPrintPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.USE_FINGERPRINT},
                    Constants.REQUEST_FINGERPRINT);
        }
    }

    @Override
    public void addFingerPrints(View view, final Activity activity) {
        Snackbar.make(view, "Fingerprint is not setup", Snackbar.LENGTH_INDEFINITE)
                .setAction("Go to settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), 0);
                    }
                })
                .show();
    }

    public interface FingerPrintListener {
        void requestFingerPrintPermission();

        void addFingerPrints();

        void onAuthenticated();
    }
}
