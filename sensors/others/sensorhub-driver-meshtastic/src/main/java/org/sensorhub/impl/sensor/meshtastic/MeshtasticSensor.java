/***************************** BEGIN LICENSE BLOCK ***************************
 The contents of this file are subject to the Mozilla Public License, v. 2.0.
 If a copy of the MPL was not distributed with this file, You can obtain one
 at http://mozilla.org/MPL/2.0/.

 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.

 Copyright (C) 2020-2025 Botts Innovative Research, Inc. All Rights Reserved.
 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.meshtastic;

import org.meshtastic.proto.MeshProtos;
import org.sensorhub.api.comm.ICommProvider;
import org.sensorhub.api.common.SensorHubException;
import org.sensorhub.impl.module.RobustConnection;
import org.sensorhub.impl.sensor.AbstractSensorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vast.ogc.om.MovingFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Driver implementation for the sensor.
 * <p>
 * This class is responsible for providing sensor information, managing output registration,
 * and performing initialization and shutdown for the driver and its outputs.
 */
public class MeshtasticSensor extends AbstractSensorModule<MeshtasticConfig> {
    static final String UID_PREFIX = "urn:osh:sensor:meshtastic:";
    static final String XML_PREFIX = "meshtastic";

    private static final Logger logger = LoggerFactory.getLogger(MeshtasticSensor.class);
    private ICommProvider<?> commProvider;
    RobustConnection connection;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    AtomicBoolean isProcessing = new AtomicBoolean(false);

    // MESHTASTIC MESSAGE VARIABLES
    private static final int START1 = 0x94;
    private static final int START2 = 0xC3;

    // DEFINE meshtastic protobuf handler
    MeshtasticHandler meshtasticHandler;

    // DEFINE OUTPUTS
    MeshtasticOutputTextMessage textOutput;
    MeshtasticOutputPosition posOutput;
    MeshtasticOutputNodeInfo nodeInfoOutput;
    MeshtasticOutputGeneric genericOutput;

    // DEFINE CONTROL
    MeshtasticControlTextMessage meshtasticControlTextMessage;


    @Override
    public void doInit() throws SensorHubException {
        super.doInit();
        logger.info("=== MeshtasticSensor doInit() called ===");

        // Generate identifiers
        generateUniqueID(UID_PREFIX, config.serialNumber);
        generateXmlID(XML_PREFIX, config.serialNumber);
        logger.info("Serial number: {}", config.serialNumber);

        tryConnection();

        // Add outputs
        createOutputs();

        // Add controls
        createControls();

        initHandlers();
    }

    public void createOutputs() {
        textOutput = new MeshtasticOutputTextMessage(this);
        addOutput(textOutput, false);
        textOutput.doInit();

        posOutput = new MeshtasticOutputPosition(this);
        addOutput(posOutput, false);
        posOutput.doInit();

        nodeInfoOutput = new MeshtasticOutputNodeInfo(this);
        addOutput(nodeInfoOutput, false);
        nodeInfoOutput.doInit();

        genericOutput = new MeshtasticOutputGeneric(this);
        addOutput(genericOutput, false);
        genericOutput.doInit();
    }

    public void createControls() {
        meshtasticControlTextMessage = new MeshtasticControlTextMessage(this);
        addControlInput(meshtasticControlTextMessage);
    }

    public void initHandlers() {
        meshtasticHandler = new MeshtasticHandler(this);
    }

    @Override
    public void doStart() throws SensorHubException {
        logger.info("=== MeshtasticSensor doStart() called ===");
        connection.waitForConnection();
        logger.info("Connection established, sending handshake...");

        // send "handshake" to start receiving protobufs
        MeshProtos.ToRadio handshake = MeshProtos.ToRadio.newBuilder()
                // TODO: Verify response ID in future if needed
                .setWantConfigId(0)
                .build();

        boolean sent = sendMessage(handshake);
        logger.info("Handshake sent: {}", sent);

        // BEGIN PROCESSING DATA
        logger.info("Starting processing thread...");
        startProcessing();
    }

    public void tryConnection() throws SensorHubException {
        logger.debug("Attempting to connect to Meshtastic device...");

        if (commProvider == null) {

            try {
                if (config.commSettings == null)
                    throw new SensorHubException("No communication settings specified");

                connection = new RobustConnection(this, config.connection, "Meshtastic Device") {
                    @Override
                    public boolean tryConnect() throws IOException {
                        try {
                            logger.info("tryConnect: Loading comm provider...");
                            var moduleReg = getParentHub().getModuleRegistry();
                            commProvider = (ICommProvider<?>) moduleReg.loadSubModule(config.commSettings, true);
                            logger.info("tryConnect: Comm provider loaded: {}", commProvider.getClass().getSimpleName());
                            commProvider.start();
                            logger.info("tryConnect: Comm provider started, isStarted={}", commProvider.isStarted());

                            return true;

                        } catch (SensorHubException e) {
                            logger.error("tryConnect failed", e);
                            reportError("Cannot connect to Meshtastic device", e, true);
                            return false;
                        }
                    }
                };
                connection.waitForConnection();
            } catch (SensorHubException e) {
                commProvider = null;
                throw new SensorHubException("Cannot connect to Meshtastic device", e);
            }
        }
    }

    @Override
    public void doStop() throws SensorHubException {
        super.doStop();

        //Stop processing the loop
        isProcessing.set(false);

        // Stop comm
        if(commProvider != null){
            try {
                commProvider.getInputStream().close();
            } catch (IOException e) {
                getLogger().warn("Failed to close input stream", e);
            }
        }


        if(connection != null) {
            connection.cancel();
            connection = null;
        }
    }

    @Override
    public boolean isConnected() {
        return connection.isConnected();
    }

    // THIS METHOD IS UTILIZED IN ALL THE OUTPUTS. IT CREATES A FOI USING PARENT SENSOR METHOD
    String addFoi( String meshNodeId) {
        String foiUID = UID_PREFIX + ":foi:" + meshNodeId;

        if (!foiMap.containsKey(foiUID))
        {
            // Generate small SensorML for FOI (in this case the system is the FOI)
            MovingFeature foi = new MovingFeature();
            foi.setId(meshNodeId);
            foi.setUniqueIdentifier(foiUID);
            foi.setName(meshNodeId);
            foi.setDescription("meshtastic Node " + meshNodeId);

            // REGISTER FOI
            addFoi(foi);

            getLogger().debug("New meshtastic Node added as FOI: {}", foiUID);

        }

        return foiUID;
    }


    // PROCESSING THREAD
    private void startProcessing() {
        // CHECK TO SEE IF ALREADY PROCESSING
        if (isProcessing.get()){
            return;
        }

        executor.execute(() -> {
            logger.info("=== Processing thread started ===");
            // try-with-resources to auto-close
            try (InputStream in = commProvider.getInputStream()){
                logger.info("Got input stream, waiting for data...");
                isProcessing.set(true);
                while (isProcessing.get()) {
                    int b;
                    // find START1 in Input Stream, indicating that a Protobuf messages is being sent
                    do {
                        b = in.read();
                        if (b == -1) return;
//                        if (b != START1) {
////                            optional: treat as debug ASCII
//                            System.out.print((char) b);
//                        }
                    } while (b != START1);

                    // If START1(0x94) is found, expect START2
                    b = in.read();
                    // invalid header
                    if (b != START2) {
                        getLogger().warn("Invalid header");
                        continue;
                    }

                    // GET LENGTH OF PROTOBUF MESSAGE
                    int lenMSB = in.read();
                    int lenLSB = in.read();
                    if (lenMSB == -1 || lenLSB == -1) break;

                    int length = ((lenMSB & 0xFF) << 8) | (lenLSB & 0xFF);
                    if (length <= 0 || length > 512) {
                        getLogger().info("Invalid length, resyncing...");
                        continue;
                    }

                    // PROTOBUF DATA
                    byte[] payload = new byte[length];
                    int read = 0;
                    while (read < length) {
                        int r = in.read(payload, read, length - read);
                        if (r == -1) break;
                        read += r;
                    }

                    if (read < length) {
                        getLogger().info("Truncated packet, resyncing...");
                        continue;
                    }

                    // parse protobuf
                    try {
                        MeshProtos.FromRadio msg = MeshProtos.FromRadio.parseFrom(payload);
                        if(msg.hasPacket()){
                            MeshProtos.MeshPacket packet = msg.getPacket();
                            meshtasticHandler.handlePacket(packet);
                        }
//                        getLogger().info("New message: " + msg);
                    } catch (Exception e) {
                        getLogger().error("Invalid protobuf: " + e.getMessage());
                    }
                }

            } catch (IOException e) {
                if (isProcessing.get()) {
                    getLogger().error("Error reading from comm provider", e);
                    if (connection != null) {
                        connection.reconnect();
                    }
                }
            } finally {
                isProcessing.set(false);
            }
        });
    }

    public boolean sendMessage(MeshProtos.ToRadio message) {
        byte[] bytes = message.toByteArray();

        int len = bytes.length;
        byte[] header = new byte[4];
        header[0] = (byte) START1;
        header[1] = (byte) START2;
        header[2] = (byte) ((len >> 8) & 0xFF);
        header[3] = (byte) (len & 0xFF);

        try {
            OutputStream os = commProvider.getOutputStream();
            os.write(header);
            os.write(bytes);
            os.flush();
            // if it sends
            return true;
        } catch (IOException e) {
            getLogger().error("Failed to send handshake message", e);
            return false;
        }

    }

}
