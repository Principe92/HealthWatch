package edu.slu.parks.healthwatch.model.pressure;

/**
 * Created by okori on 13-Jan-17.
 */

public enum PressureType {
    LOW_BLOOD_PRESSURE,
    NORMAL,
    PRE_HYPERTENSION,
    HIGH_BLOOD_PRESSURE_1,
    HIGH_BLOOD_PRESSURE_2,
    HIGH_BLOOD_PRESSURE_CRISIS;

    public static IPressure GetType(int value) {
        if (value < 80) return new LowPressure();
        else if (value < 120) return new NormalPressure();
        else if (value < 139) return new PreHypertension();
        else if (value < 159) return new FirstHighBloodPressure();
        else if (value <= 180) return new SecondHighBloodPressure();
        else return new CriticalPressure();
    }
}
