package edu.slu.parks.healthwatch.model.pressure;

import edu.slu.parks.healthwatch.R;

/**
 * Created by okori on 13-Jan-17.
 */
public class PreHypertension implements IPressure {
    @Override
    public PressureType getPressureType() {
        return PressureType.PRE_HYPERTENSION;
    }

    @Override
    public int getColor() {
        return R.color.pressure_pre;
    }
}
