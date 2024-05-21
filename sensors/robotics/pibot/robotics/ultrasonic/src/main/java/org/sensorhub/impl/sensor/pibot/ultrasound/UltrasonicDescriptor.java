/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.ultrasound;

import org.sensorhub.api.module.IModule;
import org.sensorhub.api.module.IModuleProvider;
import org.sensorhub.api.module.ModuleConfig;
import org.sensorhub.impl.module.JarModuleProvider;

/**
 * UltrasonicDescriptor classes provide access to informative data on the OpenSensorHub driver
 *
 * @author Nick Garay
 * @since Jan. 24, 2021
 */
public class UltrasonicDescriptor extends JarModuleProvider implements IModuleProvider {

    @Override
    public Class<? extends IModule<?>> getModuleClass() {

        return UltrasonicSensor.class;
    }

    @Override
    public Class<? extends ModuleConfig> getModuleConfigClass() {

        return UltrasonicConfig.class;
    }
}
