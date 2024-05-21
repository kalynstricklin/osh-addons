/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.drive;

/**
 * Enumeration of the current state values of the DriveSensor, state values correspond to one of:
 * <code>STOP</code>
 * <code>FORWARD</code>
 * <code>FORWARD_TURN_LEFT</code>
 * <code>FORWARD_TURN_RIGHT</code>
 * <code>SPIN_LEFT</code>
 * <code>SPIN_RIGHT</code>
 * <code>REVERSE_TURN_LEFT</code>
 * <code>REVERSE_TURN_RIGHT</code>
 * <code>REVERSE</code>
 * <code>UNKNOWN</code>
 *
 * @author Nick Garay
 * @since Feb. 15, 2021
 */
public enum DriveDirection {

    STOP,
    FORWARD,
    FORWARD_TURN_LEFT,
    FORWARD_TURN_RIGHT,
    SPIN_LEFT,
    SPIN_RIGHT,
    REVERSE_TURN_LEFT,
    REVERSE_TURN_RIGHT,
    REVERSE,
    UNKNOWN;

    public static DriveDirection fromString(String name) {
        for (DriveDirection state : DriveDirection.values()) {
            if (state.name().equalsIgnoreCase(name)) {
                return state;
            }
        }
        return UNKNOWN;
    }
}
