/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.
Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.

Copyright (C) 2019 Botts Innovative Research, Inc. All Rights Reserved. 

******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.sensor.kinect;

import java.nio.ByteBuffer;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.VideoHandler;
import org.sensorhub.api.data.DataEvent;
import org.sensorhub.api.sensor.SensorException;
import org.sensorhub.impl.sensor.videocam.VideoCamHelper;
import org.vast.data.DataBlockMixed;

import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataEncoding;
import net.opengis.swe.v20.DataStream;

class KinectInfraredOutput extends KinectOutputInterface {

	private static final String ERR_STR = new String("Error while initializing IR output");

	private static final String STR_NAME = new String("irCamera");

	protected DataStream irStream;

	public KinectInfraredOutput(KinectSensor parentSensor, Device kinectDevice) {

        super(STR_NAME, parentSensor, kinectDevice);
    }
	
	protected KinectInfraredOutput(String name, KinectSensor parentSensor, Device kinectDevice) {

		super(name, parentSensor, kinectDevice);
	}

	@Override
	public DataComponent getRecordDescription() {

		return irStream.getElementType();
	}

	@Override
	public DataEncoding getRecommendedEncoding() {

		return irStream.getEncoding();
	}

	@Override
	public void init() throws SensorException {

		device.setVideoFormat(getParentProducer().getConfiguration().irFormat);

		try {

			VideoCamHelper irHelper = new VideoCamHelper();

			irStream = irHelper.newGrayscaleOutput(getName(), getParentProducer().getConfiguration().frameWidth,
					getParentProducer().getConfiguration().frameHeight);

		} catch (Exception e) {

			throw new SensorException(ERR_STR, e);
		}
	}

	@Override
	public void start() {

		device.startVideo(new VideoHandler() {

			@Override
			public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {

				DataBlock dataBlock = irStream.getElementType().createDataBlock();

				byte[] channelData = new byte[getParentProducer().getConfiguration().frameWidth
						* getParentProducer().getConfiguration().frameHeight];

				getChannelData(frame, channelData);

				double samplingTime = System.currentTimeMillis() / MS_PER_S;
				
				dataBlock.setDoubleValue(IDX_TIME_DATA_COMPONENT, samplingTime);
						
				((DataBlockMixed) dataBlock).getUnderlyingObject()[IDX_PAYLOAD_DATA_COMPONENT].setUnderlyingObject(channelData);
				
				latestRecord = dataBlock;

				latestRecordTime = System.currentTimeMillis();

				eventHandler.publish(new DataEvent(latestRecordTime, KinectInfraredOutput.this, dataBlock));

				frame.position(0);
			}
		});
	}

	protected void getChannelData(ByteBuffer frame, byte[] channelData) {

		for (short y = 0; y < getParentProducer().getConfiguration().frameHeight; ++y) {

			for (short x = 0; x < getParentProducer().getConfiguration().frameWidth; ++x) {

				int offset = (x + y * getParentProducer().getConfiguration().frameWidth);

				channelData[offset] = frame.get(offset);
			}
		}
	}
}
