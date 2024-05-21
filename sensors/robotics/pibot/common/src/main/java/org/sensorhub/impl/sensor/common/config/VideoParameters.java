/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.common.config;

import org.sensorhub.api.config.DisplayInfo;

public class VideoParameters {

    @DisplayInfo.Required
    @DisplayInfo(label = "", desc = "Width of the video frames")
    public int videoFrameWidth = 1024;

    @DisplayInfo.Required
    @DisplayInfo(label = "", desc = "Height of the video frames")
    public int videoFrameHeight = 768;
}
