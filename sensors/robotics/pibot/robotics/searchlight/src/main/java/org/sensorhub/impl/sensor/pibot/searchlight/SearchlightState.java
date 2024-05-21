/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.searchlight;

/**
 * Enumeration of the current state values of the SearchlightSensor, state values correspond to one of:
 * <code>OFF</code>
 * <code>WHITE</code>
 * <code>RED</code>
 * <code>MAGENTA</code>
 * <code>BLUE</code>
 * <code>CYAN</code>
 * <code>GREEN</code>
 * <code>YELLOW</code>
 * <code>UNKNOWN</code>
 *
 * @author Nick Garay
 * @since Jan. 24, 2021
 */
public enum SearchlightState {
    OFF,
    WHITE,
    RED,
    MAGENTA,
    BLUE,
    CYAN,
    GREEN,
    YELLOW,
    UNKNOWN;

    public static SearchlightState fromString(String name) {
        for (SearchlightState state : SearchlightState.values()) {
            if (state.name().equalsIgnoreCase(name)) {
                return state;
            }
        }
        return UNKNOWN;
    }
}
