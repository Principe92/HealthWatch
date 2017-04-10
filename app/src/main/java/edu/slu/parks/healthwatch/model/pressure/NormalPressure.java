package edu.slu.parks.healthwatch.model.pressure;

import edu.slu.parks.healthwatch.R;

/**
 * Created by okori on 13-Jan-17.
 */
public class NormalPressure implements IPressure {
    @Override
    public PressureType getPressureType() {
        return PressureType.NORMAL;
    }

    @Override
    public int getColor() {
        return R.color.pressure_normal;
    }

    @Override
    public String toString() {
        return "Normal Pressure";
    }
}
