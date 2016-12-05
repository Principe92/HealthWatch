package edu.slu.parks.healthwatch.bluetooth;

/**
 * Created by okori on 22-Nov-16.
 */

public enum Phase {
    INFLATING(300),
    DEFLATING(400),
    SYSTOLIC(500),
    DIASTOLIC(600),
    DONE(700),
    START(800),
    UNKNOWN(900);

    int value;

    Phase(int value) {
        this.value = value;
    }

    public static Phase toEnum(int value) {
        for (Phase type : Phase.values()) {
            if (type.value == value) return type;
        }

        return Phase.UNKNOWN;
    }
}
