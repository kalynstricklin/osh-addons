/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.common.output;

import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataEncoding;
import org.sensorhub.api.sensor.ISensorModule;
import org.sensorhub.api.sensor.SensorException;
import org.sensorhub.impl.sensor.AbstractSensorOutput;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base Sensor Output for Sentinel Smart Sensors
 *
 * @author Nick Garay
 * @since 1.0.0
 */
public abstract class BaseSensorOutput<SensorType extends ISensorModule<?>> extends AbstractSensorOutput<SensorType> implements Runnable {

    protected Thread workerThread;

    protected AtomicBoolean doWork = new AtomicBoolean(false);

    protected static final int MAX_NUM_TIMING_SAMPLES = 10;

    protected int dataFrameCount = 0;

    protected final long[] timingHistogram = new long[MAX_NUM_TIMING_SAMPLES];

    protected final Object histogramLock = new Object();

    protected long lastDataFrameTimeMillis;

    protected DataComponent dataStruct;

    protected DataEncoding dataEncoding;

    public BaseSensorOutput(String name, SensorType parentSensor) {
        super(name, parentSensor);
    }

    @Override
    public DataComponent getRecordDescription() {

        return dataStruct;
    }

    @Override
    public DataEncoding getRecommendedEncoding() {

        return dataEncoding;
    }

    @Override
    public double getAverageSamplingPeriod() {

        long accumulator = 0;

        synchronized (histogramLock) {

            for (int idx = 0; idx < MAX_NUM_TIMING_SAMPLES; ++idx) {

                accumulator += timingHistogram[idx];
            }
        }

        return accumulator / (double) MAX_NUM_TIMING_SAMPLES;
    }

    /**
     * Terminates processing data for output
     */
    protected abstract void stop();

    /**
     * Initializes the data structure for the output, defining the fields, their ordering,
     * and data types.
     */
    protected abstract void init() throws SensorException;

    /**
     * Begins processing data for output
     */
    protected abstract void start() throws SensorException;
}
