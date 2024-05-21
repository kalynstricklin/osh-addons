/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.ultrasound;

import org.sensorhub.impl.sensor.common.control.BaseSensorControl;
import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataRecord;
import org.sensorhub.api.command.CommandException;
import org.vast.data.DataChoiceImpl;
import org.vast.swe.SWEHelper;

/**
 * Control specification and provider for PiBot UltrasonicSensor Module
 *
 * @author Nick Garay
 * @since Jan. 24, 2021
 */
public class UltrasonicControl extends BaseSensorControl<UltrasonicSensor> {

    private static final String SENSOR_CONTROL_NAME = "UltrasonicControl";

    private static final double MIN_ANGLE = 20.0;

    private static final double MAX_ANGLE = 160.0;

    protected UltrasonicControl(UltrasonicSensor parentSensor) {
        super(SENSOR_CONTROL_NAME, parentSensor);
    }

    @Override
    public boolean execCommand(DataBlock command) throws CommandException {

        boolean commandExecuted = true;

        try {

            DataRecord commandData = commandDataStruct.copy();

            commandData.setData(command);

            DataComponent component = ((DataChoiceImpl)commandData.getField("Command")).getSelectedItem();

            String commandId = component.getName();

            DataBlock data = component.getData();

            if(commandId.equalsIgnoreCase("Pan")) {

                double angle = data.getDoubleValue();

                angle = (angle <= MIN_ANGLE) ? MIN_ANGLE : Math.min(angle, MAX_ANGLE);

                parentSensor.rotateTo(angle);

            } else if (commandId.equalsIgnoreCase("RangeDetect")) {

                boolean power = data.getBooleanValue();

                if (power) {

                    parentSensor.detectRange();
                }

            } else {

                commandExecuted = false;
            }

        } catch (Exception e) {

            throw new CommandException("Failed to command the UltrasonicSensor module: ", e);
        }

        return commandExecuted;
    }

    @Override
    protected void init() {

        SWEHelper factory = new SWEHelper();
        commandDataStruct = factory.createRecord()
                .name(getName())
                .updatable(true)
                .definition(SWEHelper.getPropertyUri("UltrasonicSensor"))
                .label("UltrasonicSensor")
                .description("An ultrasonic sensor")
                .addField("Command",
                        factory.createChoice()
                                .addItem("Pan",
                                        factory.createQuantity()
                                                .name("Angle")
                                                .updatable(true)
                                                .definition(SWEHelper.getPropertyUri("servo-angle"))
                                                .description("The angle in degrees to which the servo is to turn")
                                                .addAllowedInterval(MIN_ANGLE, MAX_ANGLE)
                                                .uomCode("deg")
                                                .value(0.0)
                                                .build())
                                .addItem("RangeDetect",
                                        factory.createBoolean()
                                                .name("Power")
                                                .label("Power")
                                                .definition(SWEHelper.getPropertyUri("Power"))
                                                .description("Flag to command turning the range detection on or off")
                                                .value(false)
                                                .build())
                                .build())
                .build();
    }
}
