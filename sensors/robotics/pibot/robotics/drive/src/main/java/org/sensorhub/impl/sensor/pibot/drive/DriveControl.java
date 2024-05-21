/*
 * The contents of this file are subject to the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain
 * one at http://mozilla.org/MPL/2.0/.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) 2021-2024 Botts Innovative Research, Inc. All Rights Reserved.
 */
package org.sensorhub.impl.sensor.pibot.drive;

import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataChoice;
import net.opengis.swe.v20.DataComponent;
import org.sensorhub.api.command.CommandException;
import org.sensorhub.impl.sensor.AbstractSensorControl;
import org.sensorhub.impl.sensor.common.helper.PibotHelper;
import org.vast.data.DataChoiceImpl;

/**
 * Control specification and provider for PiBot DriveSensor Module
 *
 * @author Nick Garay Kalyn Stricklin
 * @since Jan. 24, 2021
 * @updated May 2024
 */


public class DriveControl extends AbstractSensorControl<DriveSensor> {

    DataChoice commandData;
    private static final String SENSOR_CONTROL_NAME = "DriveControl";

    //define and set default values
    double MIN_POWER = 0.0;
    double MAX_POWER = 100.0;

    protected DriveControl(DriveSensor parentSensor) {

        super(SENSOR_CONTROL_NAME, parentSensor);
    }
    @Override
    public DataComponent getCommandDescription() {

        return commandData;
    }

    protected void init(){
        PibotHelper pibotHelper = new PibotHelper();
        commandData = pibotHelper.getDriveParameters(SENSOR_CONTROL_NAME, MIN_POWER, MAX_POWER);
    }

    @Override
    protected boolean execCommand(DataBlock command) throws CommandException {
        boolean commandExecuted = true;

        try{
//            DriveDirection direction = DriveDirection.fromString(itemID);
            DriveDirection direction;

//            getLogger().debug("command: {}", command);
            DataChoice commandMsg = commandData.copy();
            commandMsg.setData(command);

            DataComponent component = ((DataChoiceImpl) commandMsg).getSelectedItem();
//            getLogger().debug("component selected!!!: {}", component);
            String itemID = component.getName();
            DataBlock data = component.getData();
            getLogger().debug("component name!!!: {}", itemID);
            getLogger().debug("component data!!!: {}", data.getDoubleValue());

            double power = data.getDoubleValue();
            power = (power <= MIN_POWER) ? MIN_POWER : Math.min(power, MAX_POWER);
//            getLogger().debug("direction: {}", direction);
            getLogger().debug("power: {}", power);

            if(itemID.equalsIgnoreCase("Forward")){
//                direction = DriveDirection.fromString(itemID);
                parentSensor.forward(power);
            }else if(itemID.equalsIgnoreCase("Reverse")){
//                direction = DriveDirection.fromString(itemID);
                parentSensor.reverse(power);
            }else if (itemID.equalsIgnoreCase("Spin_Left")) {
//                direction = DriveDirection.fromString(itemID);
                parent.spinLeft(power);
            } else if (itemID.equalsIgnoreCase("Spin_Right")) {
//                direction = DriveDirection.fromString(itemID);
                parentSensor.spinRight(power);
            }else{
                commandExecuted = false;
            }
//            parentSensor.move(direction, power);

        }catch(Exception e){
            throw new CommandException("Error connecting to PiBot Drive Control", e);
        }
        return commandExecuted;
    }

}

//public class DriveControl extends BaseSensorControl<DriveSensor> {
//
//    private static final String SENSOR_CONTROL_NAME = "DriveControl";
//
//    private static final double MIN_POWER = 0.0;
//
//    private static final double MAX_POWER = 100.0;
//
//    protected DriveControl(DriveSensor parentSensor) {
//        super(SENSOR_CONTROL_NAME, parentSensor);
//    }
//
//    @Override
//    public boolean execCommand(DataBlock command) throws CommandException {
//
//        try {
//
//            DataRecord commandData = commandDataStruct.copy();
//
//            commandData.setData(command);
//
//            DataComponent directionComponent = commandData.getField("Command");
//
//            DriveDirection direction = DriveDirection.fromString(directionComponent.getData().getStringValue());
//
//            DataComponent powerComponent = commandData.getField("Power");
//
//            DataBlock data = powerComponent.getData();
//
//            double power = data.getDoubleValue();
//
//            power = (power <= MIN_POWER) ? MIN_POWER : Math.min(power, MAX_POWER);
//
//            getLogger().debug("direction: {}", direction);
//            getLogger().debug("power: {}", power);
//
//            parentSensor.move(direction, power);
//
//        } catch (Exception e) {
//
//            throw new CommandException("Failed to command the UltrasonicSensor module: ", e);
//        }
//
//        return true;
//    }
//
//    @Override
//    protected void init() {
//
//        SWEHelper factory = new SWEHelper();
//        commandDataStruct = factory.createRecord()
//                .name(getName())
//                .updatable(true)
//                .definition(SWEHelper.getPropertyUri("DriveSensor"))
//                .label("DriveSensor")
//                .description("An drive actuator for locomotion")
//                .addField("Command",
//                        factory.createCategory()
//                                .name("Motors")
//                                .label("Motors")
//                                .definition(SWEHelper.getPropertyUri("MotorControl"))
//                                .description("Controls direction of motion for tracked robotics platform")
//                                .addAllowedValues(
//                                        DriveDirection.FORWARD.name(),
//                                        DriveDirection.FORWARD_TURN_LEFT.name(),
//                                        DriveDirection.FORWARD_TURN_RIGHT.name(),
//                                        DriveDirection.SPIN_LEFT.name(),
//                                        DriveDirection.SPIN_RIGHT.name(),
//                                        DriveDirection.REVERSE_TURN_LEFT.name(),
//                                        DriveDirection.REVERSE_TURN_RIGHT.name(),
//                                        DriveDirection.REVERSE.name(),
//                                        DriveDirection.STOP.name())
//                                .value(DriveDirection.STOP.name())
//                                .build())
//                .addField("Power",
//                        factory.createQuantity()
//                                .name("MotorPower")
//                                .label("Motor Power")
//                                .definition(SWEHelper.getPropertyUri("Power"))
//                                .description("Denotes the percentage of power to apply to the drive actuators")
//                                .addAllowedInterval(MIN_POWER, MAX_POWER)
//                                .uomCode("%")
//                                .build())
//                .build();
//    }
//}
