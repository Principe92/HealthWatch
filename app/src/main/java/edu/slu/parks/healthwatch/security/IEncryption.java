package edu.slu.parks.healthwatch.security;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

/**
 * Created by okori on 30-Dec-16.
 */

public interface IEncryption {
    String createKeys() throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException;

    String sign(String data) throws KeyStoreException,
            UnrecoverableEntryException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, IOException, CertificateException;

    boolean verify(String input, String signature) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableEntryException, InvalidKeyException, SignatureException;

    boolean hasKeys() throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
            IOException, UnrecoverableEntryException;
}
