/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2021 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.service.hivemq;

import java.time.Duration;
import org.sensorhub.api.comm.mqtt.InvalidTopicException;
import org.sensorhub.api.security.ISecurityManager;
import org.slf4j.Logger;
import org.vast.util.Asserts;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.UnsubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.parameter.UnsubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.parameter.UnsubscribeInboundOutput;
import com.hivemq.extension.sdk.api.services.Services;


public class OshUnsubscribeHandler implements UnsubscribeInboundInterceptor
{
    static final int REQ_TIMEOUT_MS = 5000;
    
    final OshExtension oshExt;
    final Logger log;
    
    
    OshUnsubscribeHandler(OshExtension oshExt)
    {
        this.oshExt = Asserts.checkNotNull(oshExt, OshExtension.class);
        this.log = oshExt.log;
    }
    
    
    @Override
    public void onInboundUnsubscribe(final UnsubscribeInboundInput unsubscribeIn, final UnsubscribeInboundOutput unsubscribeOut)
    {
        var async = unsubscribeOut.async(Duration.ofMillis(REQ_TIMEOUT_MS));
        Services.extensionExecutorService().submit(() -> {
            try
            {
                // get user ID and client ID
                var userId = unsubscribeIn.getConnectionInformation().getConnectionAttributeStore()
                    .getAsString(OshAuthenticator.MQTT_USER_PROP)
                    .orElse(ISecurityManager.ANONYMOUS_USER);
                var clientId = unsubscribeIn.getClientInformation().getClientId();
                
                // process each topic
                
                for (var topic: unsubscribeIn.getUnsubscribePacket().getTopicFilters())
                {
                    // unsubscribe from
                    try
                    {
                        oshExt.unsubscribeFromTopic(userId, clientId, topic);
                    }
                    catch (InvalidTopicException e)
                    {
                        log.debug("Invalid topic: {}", topic);
                    }
                    
                }
            }
            catch (Throwable e)
            {
                log.error("Error handling UNSUBSCRIBE message", e);
            }
            finally
            {
                async.resume();
            }   
        });
    }

}
