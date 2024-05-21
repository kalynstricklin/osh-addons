/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.ultrasound;

import org.sensorhub.impl.sensor.pibot.common.config.GpioEnum;
import org.sensorhub.api.config.DisplayInfo;

/**
 * Configuration settings for the UltrasonicSensor Driver Pins exposed via the OpenSensorHub Admin panel.
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
 * @since Jan. 24, 2021
 */
public class UltrasonicPinConfig {

    @DisplayInfo.Required
    @DisplayInfo(label="Transmit Pin", desc="The GPIO pin for the Ultrasonic transmit signal")
    public GpioEnum echoPin = GpioEnum.PIN_30;

    @DisplayInfo.Required
    @DisplayInfo(label="Receive Pin", desc="The GPIO pin for the Ultrasonic receive signal")
    public GpioEnum triggerPin = GpioEnum.PIN_31;

    @DisplayInfo.Required
    @DisplayInfo(label="Servo Pin", desc="The GPIO pin to control the pan servo of the Ultrasonic Module")
    public GpioEnum servoPin = GpioEnum.PIN_04;
}
