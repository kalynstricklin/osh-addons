/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.drive;

import org.sensorhub.api.config.DisplayInfo;
import org.sensorhub.api.sensor.SensorConfig;

import java.util.EnumSet;

/**
 * Configuration settings for the DriveSensor Driver exposed via the OpenSensorHub Admin panel.
 *
 * Configuration settings take the form of
 * <code>
 *     DisplayInfo(desc="Description of configuration field to show in UI")
 *     public Type configOption;
 * </code>
 *
 * Containing an annotation describing the setting and if applicable its range of values
 * as well as a public access variable of the given Type
 *
 * @author Nick Garay
 * @since Feb. 15, 2021
 */
public class DriveConfig extends SensorConfig {

    public enum MsgTypes{
        GPS_STATUS,
        GPS_RAW_INT,
        GLOBAL_POSITION,
        BATTERY_STATUS

    }
    public enum CmdTypes{
        VELOCITY,


    }

    @DisplayInfo(desc="Pibot messages to expose through this sensor interface")
    public EnumSet<MsgTypes> activeMessages = EnumSet.noneOf(MsgTypes.class);

    @DisplayInfo(desc="Pibot commands to expose through this sensor interface")
    public EnumSet<CmdTypes> activeCommands = EnumSet.noneOf(CmdTypes.class);


    /**
     * The unique identifier for the configured UAS sensor platform.
     */
    @DisplayInfo.Required
    @DisplayInfo(desc="Serial number or unique identifier for UAS sensor platform")
    public String serialNumber = "drive-sensor";

    /**
     * The pin configuration
     */
    @DisplayInfo.Required
    @DisplayInfo(label="PinConfig", desc="Pin configuration for module")
    public DrivePinConfig pinConfig = new DrivePinConfig();
}
