package edu.slu.parks.healthwatch.model.pressure;

import edu.slu.parks.healthwatch.R;

/**
 * Created by okori on 13-Jan-17.
 */
public class LowPressure implements IPressure {
    @Override
    public PressureType getPressureType() {
        return PressureType.LOW_BLOOD_PRESSURE;
    }

    @Override
    public int getColor() {
        return R.color.pressure_low;
    }

    @Override
    public String toString() {
        return "Low Blood Pressure";
    }
}
