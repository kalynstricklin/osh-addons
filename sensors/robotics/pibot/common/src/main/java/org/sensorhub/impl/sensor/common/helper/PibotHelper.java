package org.sensorhub.impl.sensor.common.helper;

import net.opengis.swe.v20.*;
import net.opengis.swe.v20.Boolean;
import org.vast.swe.SWEHelper;
import org.vast.swe.helper.GeoPosHelper;


/**
 * helper for control for pibot
 *
 * @author Kalyn Stricklin
 *
 * @since May 2024
 */
public class PibotHelper extends SWEHelper {
    public static final String TASKING_PAN = "pan";
    public static final String TASKING_TILT = "tilt";
    public static final String TASKING_PTZ_POS = "ptzPos";
    public static final String FORWARD = "FORWARD";
     public static final String FORWARD_TURN_LEFT = "FORWARD_TURN_LEFT";
     public static final String FORWARD_TURN_RIGHT ="FORWARD_TURN_RIGHT";
     public static final String  SPIN_LEFT= "SPIN_LEFT";
     public static final String  SPIN_RIGHT ="SPIN_RIGHT";
     public static final String REVERSE_TURN_LEFT ="REVERSE_TURN_LEFT";
    public static final String REVERSE_TURN_RIGHT ="REVERSE_TURN_RIGHT";
    public static final String  REVERSE = "REVERSE";
    public static final String STOP = "STOP";
    public static final String MAGENTA = "MAGENTA";
    public static final String CYAN = "CYAN";
    public static final String GREEN = "GREEN";
    public static final String BLUE = "BLUE";
    public static final String YELLOW = "YELLOW";
    public static final String WHITE = "WHITE";
    public static final String RED = "RED";
    public static final String OFF = "OFF";
    public static final String COLORS = "Color";



    public DataChoice getSearchlightParameters(String name){
        DataChoice searchlight = this.newDataChoice();
        searchlight.setName(name);
        searchlight.setUpdatable(true);
        searchlight.setDefinition(SWEHelper.getPropertyUri("SearchlightSensor"));
        searchlight.setLabel("SearchlightSensor");
        searchlight.setDescription("An RGB light, whose color is given by one of the color choices specified");
        searchlight.addItem(COLORS, getColorComponents().copy());
        return searchlight;

    }

    public Category getColorComponents(){

        Category s = this.newCategory();
        AllowedTokens constraints = newAllowedTokens();
        constraints.addValue(MAGENTA);
        constraints.addValue(CYAN);
        constraints.addValue(GREEN);
        constraints.addValue(BLUE);
        constraints.addValue(YELLOW);
        constraints.addValue(RED);
        constraints.addValue(WHITE);
        constraints.addValue(OFF);
        s.setConstraint(constraints);
        return s;
    }

    public DataChoice getPtCameraHelpers(String name, double min, double max){

        // NOTE: commands are individual and supported using DataChoice

        // PTZ command will consist of DataChoice with items:
        // pan, tilt, presetName

        DataChoice commandData = this.newDataChoice();
        commandData.setName(name);

        // Pan, Tilt
        Quantity pan = getPanComponent(min, max);
        commandData.addItem(TASKING_PAN, pan);
        Quantity tilt = getTiltComponent(min, max);
        commandData.addItem(TASKING_TILT, tilt);

        DataRecord ptzPos = newDataRecord(2);
        ptzPos.setName("ptzPosition");
        ptzPos.setDefinition(getPropertyUri("PtzPosition"));
        ptzPos.setLabel("Absolute PTZ Position");
        ptzPos.addComponent("pan", pan.copy());
        ptzPos.addComponent("tilt", tilt.copy());
        commandData.addItem(TASKING_PTZ_POS, ptzPos);
        return commandData;

    }
    public DataChoice getDriveParameters(String name, double minPower, double maxPower)
    {
        // NOTE: commands are individual and supported using DataChoice

        DataChoice commandData = this.newDataChoice();
        commandData.setName(name);

        Quantity forward = getPowerComponent(minPower, maxPower);
        commandData.addItem(FORWARD, forward);
        Quantity reverse = getPowerComponent(minPower, maxPower);
        commandData.addItem(REVERSE, reverse);
        Quantity stop = getPowerComponent(minPower, maxPower);
        commandData.addItem(STOP, stop);
        Quantity left = getPowerComponent(minPower, maxPower);
        commandData.addItem(SPIN_LEFT, left);
        Quantity right = getPowerComponent(minPower, maxPower);
        commandData.addItem(SPIN_RIGHT, right);
        Quantity forwardRight = getPowerComponent(minPower, maxPower);
        commandData.addItem(FORWARD_TURN_RIGHT, forwardRight);
        Quantity reverseRight = getPowerComponent(minPower, maxPower);
        commandData.addItem(REVERSE_TURN_RIGHT, reverseRight);
        Quantity forwardLeft = getPowerComponent(minPower, maxPower);
        commandData.addItem(FORWARD_TURN_LEFT, forwardLeft);
        Quantity reverseLeft = getPowerComponent(minPower, maxPower);
        commandData.addItem(REVERSE_TURN_LEFT, reverseLeft);


//        commandData.add(home)
        return commandData;
    }


    public Vector home(double latitude, double longitude){
        GeoPosHelper geo = new GeoPosHelper();
        Vector longlat = geo.newLocationVectorLatLon(getPropertyUri("HomeLocation"));
        longlat.setLabel("Home");
        longlat.setDescription("Home Location of Pibot Robot");
        return longlat;
    }



    public Quantity getPowerComponent(double min, double max){
        Quantity q = this.newQuantity(getPropertyUri("Power"), "Power", "Denotes the percentage of power to apply to the drive actuators", "%", DataType.DOUBLE);
        AllowedValues constraints = newAllowedValues();
        constraints.addInterval(new double[] {min,max});
        q.setConstraint(constraints);
        return q;
    }


    public Quantity getPanComponent(double min, double max)
    {
        Quantity q = this.newQuantity(getPropertyUri("Pan"), "Pan", "Gimbal rotation (usually horizontal)", "deg", DataType.FLOAT);
        AllowedValues constraints = newAllowedValues();
        constraints.addInterval(new double[] { min, max });
        q.setConstraint(constraints);
        return q;
    }


    public Quantity getTiltComponent(double min, double max)
    {
        Quantity q = this.newQuantity(getPropertyUri("Tilt"), "Tilt", "Gimbal rotation (usually up-down)", "deg", DataType.FLOAT);
        AllowedValues constraints = newAllowedValues();
        constraints.addInterval(new double[] { min, max });
        q.setConstraint(constraints);
        return q;
    }


}
