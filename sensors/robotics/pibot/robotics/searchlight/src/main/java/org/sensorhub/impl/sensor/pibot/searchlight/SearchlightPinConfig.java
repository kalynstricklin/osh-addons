/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.searchlight;

import org.sensorhub.impl.sensor.pibot.common.config.GpioEnum;
import org.sensorhub.api.config.DisplayInfo;

/**
 * Configuration settings for the SearchlightSensor Driver Pins exposed via the OpenSensorHub Admin panel.
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
public class SearchlightPinConfig {

    @DisplayInfo.Required
    @DisplayInfo(label="Red LED Pin", desc="The GPIO pin for the red led")
    public GpioEnum redLedPin = GpioEnum.PIN_03;

    @DisplayInfo.Required
    @DisplayInfo(label="Green LED Pin", desc="The GPIO pin for the green led")
    public GpioEnum greenLedPin = GpioEnum.PIN_02;

    @DisplayInfo.Required
    @DisplayInfo(label="Blue LED Pin", desc="The GPIO pin for the blue led")
    public GpioEnum blueLedPin = GpioEnum.PIN_05;
}
