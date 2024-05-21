/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.video.smartcam;

import org.sensorhub.impl.sensor.common.config.VideoParameters;
import org.sensorhub.api.config.DisplayInfo;
import org.sensorhub.api.config.DisplayInfo.Required;
import org.sensorhub.api.sensor.SensorConfig;
import org.sensorhub.impl.sensor.common.config.SensorPlacement;

/**
 * Configuration module for the OpenSensorHub driver
 *
 * @author Nick Garay
 * @since 1.0.0
 */
public class SmartCamConfig extends SensorConfig {

    @Required
    @DisplayInfo(desc="Camera serial number (used as suffix to generate unique identifier URI)")
    public String serialNumber = null;

    @DisplayInfo(label = "", desc = "")
    public VideoParameters videoParameters = new VideoParameters();

    @Required
    @DisplayInfo(desc="Location information for placement of a sensor")
    public SensorPlacement sensorPlacement = new SensorPlacement();
}
