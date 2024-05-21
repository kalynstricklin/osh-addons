/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.common.control;

import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataRecord;
import org.sensorhub.api.command.CommandException;
import org.sensorhub.api.sensor.ISensorModule;
import org.sensorhub.api.sensor.SensorException;
import org.sensorhub.impl.sensor.AbstractSensorControl;

/**
 * Base Sensor Control for Sentinel Smart Sensors
 *
 * @author Nick Garay
 * @since Jan. 24, 2021
 */
public abstract class BaseSensorControl<SensorType extends ISensorModule<?>> extends AbstractSensorControl<SensorType> {

    protected DataRecord commandDataStruct;

    public BaseSensorControl(String sensorControlName, SensorType parentSensor) {
        super(sensorControlName, parentSensor);
    }

    @Override
    public DataComponent getCommandDescription() {

        return commandDataStruct;
    }

    @Override
    public boolean execCommand(DataBlock command) throws CommandException {
        return false;
    }

    protected abstract void init();
}
