package edu.slu.parks.healthwatch.bluetooth;

/**
 * Created by okori on 22-Nov-16.
 */

public enum Phase {
    INFLATING(0),
    DEFLATING(1),
    SYSTOLIC(2),
    DIASTOLIC(3),
    DONE(4);

    int value;

    Phase(int value) {
        this.value = value;
    }

    public static Phase toEnum(int value) {
        for (Phase type : Phase.values()) {
            if (type.value == value) return type;
        }

        return Phase.INFLATING;
    }
}
