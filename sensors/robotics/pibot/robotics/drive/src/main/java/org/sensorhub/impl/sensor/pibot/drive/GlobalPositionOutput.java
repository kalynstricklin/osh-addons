package org.sensorhub.impl.sensor.pibot.drive;

import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataEncoding;
import org.sensorhub.api.data.DataEvent;

public class GlobalPositionOutput extends DriveOutput{

    protected DataComponent dataStruct;
    protected DataEncoding dataEncoding;
    protected double samplingPeriod;
    protected long lastMsgTime = Long.MIN_VALUE;
    

    /**
     * Constructor
     *
     * @param parentSensor Sensor driver providing this output
     */
    GlobalPositionOutput(DriveSensor parentSensor) {
        super(parentSensor);
    }

    @Override
    public double getAverageSamplingPeriod()
    {
        return samplingPeriod;
    }


    @Override
    public DataComponent getRecordDescription()
    {
        return dataStruct;
    }


    @Override
    public DataEncoding getRecommendedEncoding()
    {
        return dataEncoding;
    }

    protected  void sendOutput(long msgTime, DataBlock dataBlock){
        latestRecord = dataBlock;
        latestRecordTime = msgTime;
        eventHandler.publish(new DataEvent(msgTime, this, dataBlock));
    }

}
