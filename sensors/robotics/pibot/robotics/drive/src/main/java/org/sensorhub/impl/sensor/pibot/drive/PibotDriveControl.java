package org.sensorhub.impl.sensor.pibot.drive;

import net.opengis.swe.v20.DataChoice;
import net.opengis.swe.v20.DataComponent;
import org.sensorhub.impl.sensor.AbstractSensorControl;
import org.sensorhub.impl.sensor.common.helper.PibotHelper;
import org.vast.swe.helper.GeoPosHelper;

public class PibotDriveControl extends AbstractSensorControl<DriveSensor> {

    DataChoice commandData;
    protected PibotDriveControl(DriveSensor parentSensor) {
        super("DriveControl", parentSensor);
    }

    protected void init(){
        PibotHelper pibotHelper = new PibotHelper();

    }

    @Override
    public DataComponent getCommandDescription() {
        return null;
    }
}
