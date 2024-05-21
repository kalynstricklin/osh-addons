/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.video.smartcam;

import org.sensorhub.api.common.SensorHubException;
import org.sensorhub.api.sensor.SensorException;
import org.sensorhub.impl.sensor.AbstractSensorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Sensor module for the OpenSensorHub driver
 *
 * @author Nick Garay
 * @since 1.0.0
 */
public class SmartCamSensor extends AbstractSensorModule<SmartCamConfig> {

    private Logger logger = LoggerFactory.getLogger(SmartCamSensor.class);

    private AtomicBoolean isConnected = new AtomicBoolean(false);

    private SmartCamImageOutput imageOutput;

    private SmartCamLocationOutput locationOutput;

    @Override
    public void doInit() throws SensorHubException {

        super.doInit();

        logger.debug("Initializing");

        generateUniqueID("urn:sentinel:sensor:smartcam:", config.serialNumber);

        generateXmlID("SENTINEL_SMARTCAM_", config.serialNumber);

        imageOutput = new SmartCamImageOutput(this);

        try {

            imageOutput.init();

            addOutput(imageOutput, false);

        } catch (SensorException e) {

            logger.error("Failed to initialize {}", imageOutput.getName());

            throw new SensorHubException("Failed to initialize " + imageOutput.getName());
        }

        locationOutput = new SmartCamLocationOutput(this);

        try {

            locationOutput.init();

            addOutput(locationOutput, false);

        } catch (SensorException e) {

            logger.error("Failed to initialize {}", locationOutput.getName());

            throw new SensorHubException("Failed to initialize " + locationOutput.getName());
        }
    }

    @Override
    public boolean isConnected() {

        return isConnected.get();
    }

    @Override
    public void doStart() throws SensorHubException {

        logger.debug("Starting");

        super.doStart();

        if(null != imageOutput) {

            try {

                imageOutput.start();

            } catch (SensorException e) {

                logger.error("Failed to start {} due to {}", imageOutput.getName(), e);
            }
        }

        if(null != locationOutput) {

            try {

                locationOutput.start();

            } catch (SensorException e) {

                logger.error("Failed to start {} due to {}", locationOutput.getName(), e);
            }
        }

        isConnected.set(true);

        logger.debug("Started");
    }

    @Override
    public void doStop() throws SensorHubException {

        logger.debug("Stopping");

        super.doStop();

        if(null != imageOutput) {

            imageOutput.stop();
        }

        if(null != locationOutput) {

            locationOutput.stop();
        }

        isConnected.set(false);

        logger.debug("Stopped");
    }
}
