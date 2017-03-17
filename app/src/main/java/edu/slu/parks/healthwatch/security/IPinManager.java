package edu.slu.parks.healthwatch.security;

/**
 * Created by okori on 06-Jan-17.
 */

public interface IPinManager {
    boolean isPinValid(String pin);

    void resetPin();

    boolean savePin(String pin);

    boolean saveTemporaryPin(String pin);

    boolean isTemporaryPinValid(String pin);

    void createKeys();

    boolean hasPin();

    void clearTemporaryPin();

    boolean shouldLoginIn();
}
