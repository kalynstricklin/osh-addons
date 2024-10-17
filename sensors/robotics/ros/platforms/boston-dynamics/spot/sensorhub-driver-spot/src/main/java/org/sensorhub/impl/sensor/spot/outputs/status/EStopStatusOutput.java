/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License, v. 2.0.
 If a copy of the MPL was not distributed with this file, You can obtain one
 at http://mozilla.org/MPL/2.0/.

 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.

 Copyright (C) 2023 - 2024 Botts Innovative Research, Inc. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.spot.outputs.status;

import net.opengis.swe.v20.DataBlock;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.sensorhub.api.data.DataEvent;
import org.sensorhub.impl.ros.config.RosMasterConfig;
import org.sensorhub.impl.ros.nodes.pubsub.RosSubscriberNode;
import org.sensorhub.impl.ros.output.RosSensorOutput;
import org.sensorhub.impl.ros.utils.RosUtils;
import org.sensorhub.impl.sensor.spot.SpotSensor;
import org.sensorhub.impl.sensor.spot.config.SpotStatusConfig;
import org.slf4j.LoggerFactory;
import org.vast.data.DataArrayImpl;
import org.vast.swe.SWEHelper;
import spot_msgs.EStopState;
import spot_msgs.EStopStateArray;

import java.net.URI;
import java.util.List;

/**
 * Defines the output of the emergency stop status from the platform and publishes the observations thereof
 *
 * @author Nick Garay
 * @since Sept. 25, 2023
 */
public class EStopStatusOutput extends RosSensorOutput<SpotSensor> {


    /**
     * Possible fault type values received
     */
    private static final String[] TYPES = {"TYPE_UNKNOWN", "TYPE_HARDWARE", "TYPE_SOFTWARE"};

    /**
     * Possible status values received
     */
    private static final String[] STATES = {"STATE_UNKNOWN", "STATE_ESTOPPED", "STATE_NOT_ESTOPPED"};

    /**
     * Name of the output
     */
    private static final String SENSOR_OUTPUT_NAME = "EStopStatus";

    /**
     * Label for the output
     */
    private static final String SENSOR_OUTPUT_LABEL = "EStop Status";

    /**
     * Description of the output
     */
    private static final String SENSOR_OUTPUT_DESCRIPTION = "The status of the eStop system";

    /**
     * ROS Node name assigned at creation
     */
    private static final String NODE_NAME_STR = "/SensorHub/spot/estop_status";

    /**
     * The ROS executor process that manages the lifecycle of ROS nodes and services
     */
    private NodeMainExecutor nodeMainExecutor;

    /**
     * ROS Node used to listen for messages on a specific topic
     */
    private RosSubscriberNode subscriberNode;

    /**
     * Constructor
     *
     * @param parentSpotSensor Sensor driver providing this output
     */
    public EStopStatusOutput(SpotSensor parentSpotSensor) {

        super(SENSOR_OUTPUT_NAME, parentSpotSensor, LoggerFactory.getLogger(BatteryStatusOutput.class));

        getLogger().debug("Output created");
    }

    @Override
    public void doInit() {

        getLogger().debug("Initializing Output");

        defineRecordStructure();

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();

        SpotStatusConfig.EStopStatusOutput config =
                getParentProducer().getConfiguration().spotStatusConfig.eStopStatusOutput;

        subscriberNode = new RosSubscriberNode(
                NODE_NAME_STR, config.eStopStatusTopic, EStopStateArray._TYPE, this);

        initSamplingTime();

        getLogger().debug("Initializing Output Complete");
    }

    @Override
    public void doStart() {

        RosMasterConfig config = parentSensor.getConfiguration().rosMaster;

        NodeConfiguration nodeConfiguration = RosUtils.getNodeConfiguration(
                config.localHostIp, NODE_NAME_STR, URI.create(config.uri));

        nodeMainExecutor.execute(subscriberNode, nodeConfiguration);
    }

    @Override
    public void doStop() {

        nodeMainExecutor.shutdownNodeMain(subscriberNode);

        nodeMainExecutor.shutdown();
    }

    @Override
    public boolean isAlive() {

        return !(nodeMainExecutor == null || nodeMainExecutor.getScheduledExecutorService().isShutdown());
    }

    @Override
    protected void defineRecordStructure() {

        // Get an instance of SWE Factory suitable to build components
        SWEHelper sweFactory = new SWEHelper();

        // Create data record description
        dataStruct = sweFactory.createRecord()
                .name(getName())
                .updatable(true)
                .definition(SWEHelper.getPropertyUri(SENSOR_OUTPUT_NAME))
                .label(SENSOR_OUTPUT_LABEL)
                .description(SENSOR_OUTPUT_DESCRIPTION)
                .addField("SampleTime", sweFactory.createTime().asSamplingTimeIsoUTC().build())
                .addField("stateCount", sweFactory.createCount()
                        .label("State Count")
                        .description("Number of reported e-stop states")
                        .definition(SWEHelper.getPropertyUri("state_count"))
                        .id("stateCount"))
                .addField("stateArray", sweFactory.createArray()
                        .label("State Array")
                        .description("List of e-stop states reported")
                        .definition(SWEHelper.getPropertyUri("state_array"))
                        .withVariableSize("stateCount")
                        .withElement("stateRecord", sweFactory.createRecord()
                                .label("State Record")
                                .description("State parameters reported by the platform")
                                .definition(SWEHelper.getPropertyUri("state_record"))
                                .addField("stateName", sweFactory.createText()
                                        .label("State Name")
                                        .description("Name of the e-stop state as reported")
                                        .definition(SWEHelper.getPropertyUri("state_name")))
                                .addField("description", sweFactory.createText()
                                        .label("Description")
                                        .description("Description of the reported e-stop state")
                                        .definition(SWEHelper.getPropertyUri("description")))
                                .addField("type", sweFactory.createText()
                                        .label("Type")
                                        .description("Reported e-stop state type")
                                        .definition(SWEHelper.getPropertyUri("type")))
                                .addField("state", sweFactory.createText()
                                        .label("State")
                                        .description("The actual reported e-stop state")
                                        .definition(SWEHelper.getPropertyUri("state")))
                        ))
                .build();

        dataEncoding = sweFactory.newTextEncoding(",", "\n");
    }

    @Override
    public void onNewMessage(Object object) {

        updateSamplingPeriodHistogram();

        EStopStateArray message = (EStopStateArray) object;

        // Need to recreate the data record structure or createDataBlock will fail the second and subsequent times.
        defineRecordStructure();
        DataBlock dataBlock = dataStruct.createDataBlock();
        // Pair the data record with the data block.
        // This is necessary for updating the array size after setting the array count.
        dataStruct.setData(dataBlock);

        List<EStopState> estopStates = message.getEstopStates();

        int index = 0;

        dataBlock.setDoubleValue(index++, System.currentTimeMillis() / 1000.);
        // Set the array count.
        dataBlock.setIntValue(index++, estopStates.size());

        // Update the array size. This can only be done after setting the array count.
        var stateArray = ((DataArrayImpl) dataStruct.getComponent("stateArray"));
        stateArray.updateSize();
        dataBlock.updateAtomCount();

        for (EStopState currentState : estopStates) {

            dataBlock.setStringValue(index++, currentState.getName());
            dataBlock.setStringValue(index++, currentState.getStateDescription());
            dataBlock.setStringValue(index++, TYPES[currentState.getType()]);
            dataBlock.setStringValue(index++, STATES[currentState.getState()]);
        }

        latestRecord = dataBlock;

        latestRecordTime = System.currentTimeMillis();

        eventHandler.publish(new DataEvent(latestRecordTime, EStopStatusOutput.this, dataBlock));
    }
}