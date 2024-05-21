/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.camera;

import org.sensorhub.api.module.IModule;
import org.sensorhub.api.module.IModuleProvider;
import org.sensorhub.api.module.ModuleConfig;
import org.sensorhub.impl.module.JarModuleProvider;

/**
 * CameraDescriptor classes provide access to informative data on the OpenSensorHub driver
 *
 * @author Nick Garay
 * @since Feb. 16, 2021
 */
public class CameraDescriptor extends JarModuleProvider implements IModuleProvider {

    @Override
    public Class<? extends IModule<?>> getModuleClass() {

        return CameraSensor.class;
    }

    @Override
    public Class<? extends ModuleConfig> getModuleConfigClass() {

        return CameraConfig.class;
    }
}
