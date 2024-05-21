/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.searchlight;

import net.opengis.swe.v20.DataChoice;
import net.opengis.swe.v20.DataComponent;
import org.sensorhub.impl.sensor.AbstractSensorControl;
import org.sensorhub.impl.sensor.common.control.BaseSensorControl;
import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataRecord;
import org.sensorhub.api.command.CommandException;
import org.sensorhub.impl.sensor.common.helper.PibotHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vast.data.AbstractDataBlock;
import org.vast.data.DataBlockMixed;
import org.vast.data.DataChoiceImpl;
import org.vast.swe.SWEHelper;

/**
 * Control specification and provider for PiBot SearchlightSensor Module
 *
 * @author Nick Garay
 * @since Jan. 24, 2021
 */

public class SearchlightControl extends AbstractSensorControl<SearchlightSensor>{
    DataChoice commandData;

    private static final String SENSOR_CONTROL_NAME = "SearchlightControl";
    private static final Logger logger = LoggerFactory.getLogger(SearchlightControl.class);
    protected SearchlightControl(SearchlightSensor parentSensor) {
        super(SENSOR_CONTROL_NAME, parentSensor);
    }

    protected  void init(){
        PibotHelper pibotHelper = new PibotHelper();
        commandData = pibotHelper.getSearchlightParameters(getName());
    }

    @Override
    protected boolean execCommand(DataBlock command)throws CommandException{
        try{
            getLogger().debug("command: {}", command);
            DataChoice commandMsg = commandData.copy();
            commandMsg.setData(command);

            DataComponent component = ((DataChoiceImpl) commandMsg).getSelectedItem();
            String itemID = component.getName();
            DataBlock data = component.getData();

            SearchlightState searchLightColor = SearchlightState.fromString(data.getStringValue());
            logger.debug("searchlight color: {}", searchLightColor);
            logger.debug("Sending color to searchlight output!");
            parentSensor.setSearchlightState(searchLightColor);

        }catch (Exception e){
            throw new CommandException("Error during connection to Searchlight Control ", e);
        }

        return true;
    }

    @Override
    public DataComponent getCommandDescription() {
        return commandData;
    }
}


//public class SearchlightControl extends BaseSensorControl<SearchlightSensor> {
//
//
//    DataChoice dataChoice;
//
//    private static final String SENSOR_CONTROL_NAME = "SearchlightControl";
//    private static final Logger logger = LoggerFactory.getLogger(SearchlightControl.class);
//
//    protected SearchlightControl(SearchlightSensor parentSensor) {
//        super(SENSOR_CONTROL_NAME, parentSensor);
//    }
//
//    @Override
//    public boolean execCommand(DataBlock command) throws CommandException {
//
//        try {
//            if(command instanceof DataBlockMixed){
//                logger.debug("command: {}", command);
//                DataBlockMixed commandData = new DataBlockMixed();
//                commandData.setBlock(0, (AbstractDataBlock) command);
//                logger.debug("command data: {}", commandData.getStringValue(0));
//                SearchlightState searchLightColor = SearchlightState.fromString(commandData.getStringValue(0));
//                logger.debug("searchlight color: {}", searchLightColor);
//                parentSensor.setSearchlightState(searchLightColor);
//
//            }else{
//                DataRecord commandData = commandDataStruct.copy();
////                logger.debug("command data: {}", commandData.toString());
//                commandData.setData(command);
//                SearchlightState searchLightColor = SearchlightState.fromString(commandData.getData().getStringValue());
////                logger.debug("searchlight color: {}", commandData.getData().getStringValue());
//                parentSensor.setSearchlightState(searchLightColor);
//
//            }
//
//
//        } catch (Exception e) {
//
//            throw new CommandException("Failed to command the SearchlightSensor module: ", e);
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
//                .definition(SWEHelper.getPropertyUri("SearchlightSensor"))
//                .label("SearchlightSensor")
//                .description("An RGB light, whose color is given by one of the color choices specified")
//                .addField("Color",
//                        factory.createCategory()
//                                .name("RGB Color")
//                                .label("RGB Color")
//                                .definition(SWEHelper.getPropertyUri("Color"))
//                                .description("The color state of the searchlight")
//                                .addAllowedValues(
//                                        SearchlightState.OFF.name(),
//                                        SearchlightState.WHITE.name(),
//                                        SearchlightState.RED.name(),
//                                        SearchlightState.MAGENTA.name(),
//                                        SearchlightState.BLUE.name(),
//                                        SearchlightState.CYAN.name(),
//                                        SearchlightState.GREEN.name(),
//                                        SearchlightState.YELLOW.name())
//                                .value(SearchlightState.OFF.name())
//                                .build())
//                .build();
//    }
//}
