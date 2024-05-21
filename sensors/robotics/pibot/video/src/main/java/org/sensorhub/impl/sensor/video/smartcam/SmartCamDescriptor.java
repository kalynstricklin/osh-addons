/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.video.smartcam;

import org.sensorhub.api.module.IModule;
import org.sensorhub.api.module.IModuleProvider;
import org.sensorhub.api.module.ModuleConfig;
import org.sensorhub.impl.module.JarModuleProvider;

/**
 * Descriptor classes provide access to informative data on the OpenSensorHub driver
 *
 * @author Nick Garay
 * @since 1.0.0
 */
public class SmartCamDescriptor extends JarModuleProvider implements IModuleProvider {

    @Override
    public Class<? extends IModule<?>> getModuleClass() {

        return SmartCamSensor.class;
    }

    @Override
    public Class<? extends ModuleConfig> getModuleConfigClass() {

        return SmartCamConfig.class;
    }
}
