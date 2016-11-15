package edu.slu.parks.healthwatch.model;

/**
 * Created by okori on 14-Nov-16.
 */

public enum GraphType {
    LiNE(0),
    BAR(1);

    int value;

    GraphType(int value) {
        this.value = value;
    }

    public static GraphType toEnum(int value) {
        for (GraphType type : GraphType.values()) {
            if (type.value == value) return type;
        }

        return null;
    }
}
