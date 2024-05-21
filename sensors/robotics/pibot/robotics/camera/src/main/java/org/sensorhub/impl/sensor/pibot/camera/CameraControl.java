/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.camera;

import net.opengis.swe.v20.DataChoice;
import org.sensorhub.impl.sensor.AbstractSensorControl;
import org.sensorhub.impl.sensor.common.control.BaseSensorControl;
import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataRecord;
import org.sensorhub.api.command.CommandException;
import org.sensorhub.impl.sensor.common.helper.PibotHelper;
import org.vast.data.DataChoiceImpl;
import org.vast.swe.SWEHelper;

/**
 * Control specification and provider for PiBot UltrasonicSensor Module
 *
 * @author Nick Garay Kalyn Stricklin
 *
 * @since Jan. 24, 2021
 */
//public class CameraControl extends BaseSensorControl<CameraSensor> {
//
//    private static final String SENSOR_CONTROL_NAME = "CameraControl";
//
//    private static final double MIN_ANGLE = 20.0;
//
//    private static final double MAX_ANGLE = 160.0;
//
//    protected CameraControl(CameraSensor parentSensor) {
//        super(SENSOR_CONTROL_NAME, parentSensor);
//    }
//
//    @Override
//    public boolean execCommand(DataBlock command) throws CommandException {
//
//        boolean commandExecuted = true;
//
//        try {
//
//            DataRecord commandData = commandDataStruct.copy();
//
//            commandData.setData(command);
//
//            DataComponent component = ((DataChoiceImpl)commandData.getField("Command")).getSelectedItem();
//
//            String commandId = component.getName();
//
//            DataBlock data = component.getData();
//
//            double angle = data.getDoubleValue();
//
//            angle = (angle <= MIN_ANGLE) ? MIN_ANGLE : Math.min(angle, MAX_ANGLE);
//
//            if(commandId.equalsIgnoreCase("Pan")) {
//
//                parentSensor.panTo(angle);
//
//            } else if(commandId.equalsIgnoreCase("Tilt")) {
//
//                parentSensor.tiltTo(angle);
//
//            } else {
//
//                commandExecuted = false;
//            }
//
//        } catch (Exception e) {
//
//            throw new CommandException("Failed to command the CameraSensor module: ", e);
//        }
//
//        return commandExecuted;
//    }
//
//    @Override
//    protected void init() {
//
//        SWEHelper factory = new SWEHelper();
//        commandDataStruct = factory.createRecord()
//                .name(getName())
//                .updatable(true)
//                .definition(SWEHelper.getPropertyUri("CameraSensor"))
//                .label("CameraSensor")
//                .description("An camera sensor")
//                .addField("Command",
//                        factory.createChoice()
//                                .addItem("Pan",
//                                        factory.createQuantity()
//                                                .name("Angle")
//                                                .updatable(true)
//                                                .definition(SWEHelper.getPropertyUri("servo-angle"))
//                                                .description("The angle in degrees to which the servo is to turn")
//                                                .addAllowedInterval(MIN_ANGLE, MAX_ANGLE)
//                                                .uomCode("deg")
//                                                .value(0.0)
//                                                .build())
//                                .addItem("Tilt",
//                                        factory.createQuantity()
//                                                .name("Angle")
//                                                .updatable(true)
//                                                .definition(SWEHelper.getPropertyUri("servo-angle"))
//                                                .description("The angle in degrees to which the servo is to turn")
//                                                .addAllowedInterval(MIN_ANGLE, MAX_ANGLE)
//                                                .uomCode("deg")
//                                                .value(0.0)
//                                                .build())
//                                .build())
//                .build();
//    }
//}


public class CameraControl extends AbstractSensorControl<CameraSensor>{

    DataChoice commandData;

    private static final String SENSOR_CONTROL_NAME = "CameraControl";
    private static final double MIN_ANGLE = 20.0;
    private static final double MAX_ANGLE = 160.0;
    protected CameraControl(CameraSensor parentSensor) {
        super(SENSOR_CONTROL_NAME, parentSensor);
    }

    protected void init(){
        PibotHelper pibotHelper = new PibotHelper();
        commandData = pibotHelper.getPtCameraHelpers(getName(), MIN_ANGLE, MAX_ANGLE);
    }

    @Override
    protected boolean execCommand(DataBlock command) throws CommandException{
        boolean commandExecuted = true;
        try{
            getLogger().debug("command: {}", command);
            DataChoice commandMsg = commandData.copy();
            commandMsg.setData(command);

            DataComponent component = ((DataChoiceImpl) commandMsg).getSelectedItem();
            String itemID = component.getName();
            DataBlock data = component.getData();

            double angle = data.getDoubleValue();
            angle = (angle <= MIN_ANGLE) ? MIN_ANGLE : Math.min(angle, MAX_ANGLE);

            if(itemID.equalsIgnoreCase("Pan")) {
                parentSensor.panTo(angle);
            } else if(itemID.equalsIgnoreCase("Tilt")) {
                parentSensor.tiltTo(angle);
            } else {
                commandExecuted = false;
            }



        }catch (Exception e){
            throw new CommandException("Error during connection to Camera Control ", e);
        }

        return commandExecuted;
    }
    @Override
    public DataComponent getCommandDescription() {
        return commandData;
    }
}