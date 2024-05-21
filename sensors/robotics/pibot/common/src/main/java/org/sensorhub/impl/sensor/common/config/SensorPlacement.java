/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.common.config;

import org.sensorhub.api.config.DisplayInfo;

/**
 * Configuration module for the OpenSensorHub driver pertaining to logical position of a sensor
 * within a personal residential environment, someones home.
 *
 * @author Nick Garay
 * @since 1.0.0
 */
public class SensorPlacement {

    /**
     * List of locations
     */
    public enum Locations
    {
        LIVING_ROOM("Living Room"),
        DINING_ROOM("Dining Room"),
        DEN("Den"),
        KITCHEN("Kitchen"),
        FOYER("Foyer"),
        GARAGE("Garage"),
        MASTER_SUITE("Master Suite"),
        BEDROOM_1("Bedroom 1"),
        BEDROOM_2("Bedroom 2"),
        BEDROOM_3("Bedroom 3"),
        BEDROOM_4("Bedroom 4"),
        BEDROOM_5("Bedroom 5"),
        BATHROOM_1("Bathroom 1"),
        BATHROOM_2("Bathroom 2"),
        BATHROOM_3("Bathroom 3"),
        BATHROOM_4("Bathroom 4"),
        BATHROOM_5("Bathroom 5"),
        OTHER("Other");

        /**
         * Name of the enumerated value
         */
        private String name;

        /**
         * Constructor
         *
         * @param name the name to assign to new enumerated value
         */
        Locations(String name) {

            this.name = name;
        }

        @Override
        public String toString() {

            return name;
        }
    }

    @DisplayInfo.Required
    @DisplayInfo(desc="Location of sensor placement, for custom location select \"Other\"")
    public Locations location = Locations.FOYER;

    @DisplayInfo(desc="Location of sensor placement if other than specified list of locations")
    public String customLocation = "";
}
