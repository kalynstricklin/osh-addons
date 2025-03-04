package org.sensorhub.impl.sensor.isa;

import org.sensorhub.api.data.DataEvent;
import org.vast.swe.SWEConstants;


public class AtmosPrecipOutput extends ISAOutput
{
    static final String[] PRECIP_CLASSES = {
        "UNKNOWN", "DRIZZLE", "FREEZING_DRIZZLE",
        "FREEZING_RAIN", "HAIL", "ICE_CRYSTALS",
        "ICE_PELLETS", "NONE", "RAIN", "RAIN_SHOWER",
        "RAIN_SNOW_MIX", "SLEET", "SNOW", "SNOW_GRAINS",
        "SNOW_PELLETS", "SNOW_SHOWER", "THUNDERSTORM"};
    
    
    public AtmosPrecipOutput(ISASensor parentSensor)
    {
        super("precip", parentSensor);
        ISAHelper isa = new ISAHelper();
        
        // output structure
        dataStruct = isa.createRecord()
            .name(this.name)
            .definition(ISAHelper.getIsaUri("Atmospheric_Precipitation"))
            .label("Atmospheric Precipitation")
            .addSamplingTimeIsoUTC("time")
            .addField("category", isa.createCategory()
                .definition(ISAHelper.getIsaUri("Precip_Class"))
                .description("The type of precipitation observed.")
                .addAllowedValues(PRECIP_CLASSES)
                .addNilValue(PRECIP_CLASSES[0], SWEConstants.NIL_UNKNOWN))
            .addField("rate", isa.createQuantity()
                .definition(ISAHelper.getCfUri("lwe_precipitation_rate"))
                .description("The rate of the precipitation.")
                .uomCode("mm/h")
                .addNilValue(Double.NaN, SWEConstants.NIL_UNKNOWN))
            .build();
                
        // default output encoding
        dataEnc = isa.newTextEncoding(",", "\n");
    }
    
    
    @Override
    public double getAverageSamplingPeriod()
    {
        return 30.;
    }


    @Override
    protected void sendSimulatedMeasurement()
    {
        var now = parentSensor.getCurrentTime();
        if (nextRecordTime > now)
            return;
        
        var dataBlk = dataStruct.createDataBlock();
        
        int i = 0;        
        dataBlk.setDoubleValue(i++, ((double)now)/1000.);
        dataBlk.setStringValue(i++, PRECIP_CLASSES[(int)(Math.random()*PRECIP_CLASSES.length)]); // precip class
        dataBlk.setDoubleValue(i++, (int)(Math.random()*100) / 10.); // rate
        
        nextRecordTime = now + (long)(getAverageSamplingPeriod()*1000);
        latestRecordTime = now;
        latestRecord = dataBlk;
        eventHandler.publish(new DataEvent(latestRecordTime, this, dataBlk));
    }
}
