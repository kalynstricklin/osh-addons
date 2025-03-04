/**
 * The contents of this file are subject to the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one
 * at http://mozilla.org/MPL/2.0/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * Copyright (C) 2023-2024 Botts Innovative Research, Inc. All Rights Reserved.
 */

package org.sensorhub.impl.ros.nodes.service;

import org.ros.concurrent.CancellableLoop;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementation of a ROS Service Client.  Nodes of this type publish a message to a service within the
 * ROS ecosystem and receive a response indicating the success or failure of the service call.
 *
 * @param <RequestType>  The type of request being supported
 * @param <ResponseType> The type of the responses to be sent
 * @author Nicolas Garay
 * @since Aug 29, 2023
 */
public class RosServiceClient<RequestType, ResponseType> extends AbstractNodeMain {

    /**
     * Logger
     */
    protected final Logger logger = LoggerFactory.getLogger(RosServiceClient.class);

    /**
     * Name of the node
     */
    private final String nodeName;

    /**
     * Name of the service
     */
    private final String serviceName;

    /**
     * Type of the service being created
     */
    private final String serviceType;

    /**
     * The ROS service client
     */
    private ServiceClient<RequestType, ResponseType> serviceClient;

    /**
     * Message queue of messages to publish
     */
    private final LinkedBlockingQueue<RequestType> messagesQueue = new LinkedBlockingQueue<>();

    /**
     * The listener to invoke with the <code>RES</code> type when the service call completes
     */
    private ServiceResponseListener<ResponseType> serviceResponseListener;

    /**
     * Reports of the service client has connected.
     */
    private boolean isConnected = false;

    /**
     * Constructor
     *
     * @param nodeName    name of the node
     * @param serviceName name of the service
     * @param serviceType the type of the service being invoked
     */
    public RosServiceClient(final String nodeName, final String serviceName, final String serviceType) {

        this.nodeName = nodeName;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
    }

    @Override
    public GraphName getDefaultNodeName() {

        return GraphName.of(nodeName);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        try {

            if (serviceClient == null) {

                serviceClient = connectedNode.newServiceClient(this.serviceName, this.serviceType);
            }

            if (!isConnected) {

                isConnected = serviceClient.isConnected();

                connectedNode.executeCancellableLoop(new CancellableLoop() {
                    @Override
                    protected void loop() throws InterruptedException {

                        RequestType currentMessage;

                        currentMessage = messagesQueue.take();


                        serviceClient.call(currentMessage, serviceResponseListener);
                    }
                });
            }

        } catch (ServiceNotFoundException exception) {

            isConnected = false;

            logger.error("Failed to connect to service {}", serviceName);
        }
    }

    /**
     * Creates a message buffer for the type messages
     *
     * @return a message buffer of the correct type
     */
    public RequestType getNewMessageBuffer() {

        return serviceClient.newMessage();
    }

    /**
     * Enqueues a service request
     *
     * @param message the message to send to the service
     */
    public void enqueueServiceRequest(RequestType message) {

        try {

            messagesQueue.put(message);

        } catch (InterruptedException e) {

            logger.error(e.getMessage());

            Thread.currentThread().interrupt();
        }
    }

    /**
     * Reports of the service client has connected.  This should be called
     * after executing the node executable to get an indication if the service
     * client was able to connect to the service.
     *
     * @return <code>true</code> if connected successfully, <code>false</code> otherwise
     */
    public boolean isConnected() {

        return isConnected;
    }

    /**
     * Assigns the service response listener
     *
     * @param listener The listener invoked when a service response is received
     */
    public void setServiceResponseListener(ServiceResponseListener<ResponseType> listener) {

        this.serviceResponseListener = listener;
    }
}
