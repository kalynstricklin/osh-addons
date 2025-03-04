/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2012-2021 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.service.hivemq;

import org.sensorhub.api.config.DisplayInfo;
import org.sensorhub.api.config.DisplayInfo.Required;
import org.sensorhub.api.service.ServiceConfig;


public class MqttServerConfig extends ServiceConfig
{
    
    @Required
    @DisplayInfo(desc="Path to folder containing HiveMQ configuration file")
    String configFolder = "hivemq-config";
    
    
    @Required
    @DisplayInfo(desc="Path to folder that will contain HiveMQ data files")
    String dataFolder = "hivemq-data";
    
    
    @DisplayInfo(desc="Endpoint of websocket proxy to be deployed on the hub HTTP server")
    public String webSocketProxyEndpoint = "/mqtt";
    
    
    @DisplayInfo(desc="Set to enable a websocket proxy at the specified endpoint")
    public boolean enableWebSocketProxy = false;
    
    
    @DisplayInfo(label="Require Authentication", desc="Set to require remote users to be authentified before they can use this service")
    public boolean requireAuth = false;
}
