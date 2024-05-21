/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.searchlight;

import org.sensorhub.api.config.DisplayInfo;
import org.sensorhub.api.sensor.SensorConfig;

/**
 * Configuration settings for the SearchlightSensor driver exposed via the OpenSensorHub Admin panel.
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
public class SearchlightConfig extends SensorConfig {

    /**
     * The unique identifier for the configured UAS sensor platform.
     */
    @DisplayInfo.Required
    @DisplayInfo(desc="Serial number or unique identifier for sensor module")
    public String serialNumber = "rgb-searchlight";

    /**
     * The pin configuration
     */
    @DisplayInfo.Required
    @DisplayInfo(label="PinConfig", desc="Pin configuration for module")
    public SearchlightPinConfig pinConfig = new SearchlightPinConfig();
}
