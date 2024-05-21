/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.camera;

import org.sensorhub.impl.sensor.pibot.common.config.GpioEnum;
import org.sensorhub.api.config.DisplayInfo;

/**
 * Configuration settings for the CameraSensor Driver Pins exposed via the OpenSensorHub Admin panel.
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
 * @since Feb. 16, 2021
 */
public class CameraPinConfig {

    @DisplayInfo.Required
    @DisplayInfo(label="Pan Servo Pin", desc="The GPIO pin to control the pan servo of the Camera Module")
    public GpioEnum panServoPin = GpioEnum.PIN_14;

    @DisplayInfo.Required
    @DisplayInfo(label="Tilt Servo Pin", desc="The GPIO pin to control the tilt servo of the Camera Module")
    public GpioEnum tiltServoPin = GpioEnum.PIN_13;
}
