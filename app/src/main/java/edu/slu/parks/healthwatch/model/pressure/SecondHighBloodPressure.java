package edu.slu.parks.healthwatch.model.pressure;

import edu.slu.parks.healthwatch.R;

/**
 * Created by okori on 13-Jan-17.
 */
public class SecondHighBloodPressure implements IPressure {
    @Override
    public PressureType getPressureType() {
        return PressureType.HIGH_BLOOD_PRESSURE_2;
    }

    @Override
    public int getColor() {
        return R.color.pressure_high_2;
    }
}
