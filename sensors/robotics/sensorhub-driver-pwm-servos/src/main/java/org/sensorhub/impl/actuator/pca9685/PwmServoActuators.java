/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2012-2015 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.actuator.pca9685;

import org.sensorhub.api.comm.ICommProvider;
import org.sensorhub.api.common.SensorHubException;
import org.sensorhub.impl.sensor.AbstractSensorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * Driver implementation to control servos connected to Adafruit PCA9685
 * 16-channels PWM daughter board using I2C commands.
 * </p>
 *
 * @author Alex Robin
 * @since Aug 27, 2015
 */
public class PwmServoActuators extends AbstractSensorModule<PwmServosConfig>
{
    static final Logger log = LoggerFactory.getLogger(PwmServoActuators.class);
    
    ICommProvider<?> commProvider;
    volatile boolean started;
    
    
    public PwmServoActuators()
    {        
    }
    
    
    @Override
    protected void doInit() throws SensorHubException
    {
        super.init(config);
        
        // generate IDs
        generateUniqueID("urn:osh:actuator:servos:", null);
        generateXmlID("PWM_SERVOS_", null);      
        
        // create control inputs
    }
    
    
    @Override
    protected void updateSensorDescription()
    {
        synchronized (sensorDescLock)
        {
            super.updateSensorDescription();
            sensorDescription.setDescription("Adafruit PCA9685 16-channels PWM servo board");
        }
    }


    @Override
    protected void doStart() throws SensorHubException
    {
        if (started)
            return;
                
        // init comm provider
        if (commProvider == null)
        {
            // we need to recreate comm provider here because it can be changed by UI
            try
            {
                if (config.commSettings == null)
                    throw new SensorHubException("No communication settings specified");
                
                var moduleReg = getParentHub().getModuleRegistry();
                commProvider = (ICommProvider<?>)moduleReg.loadSubModule(config.commSettings, true);
                commProvider.start();
            }
            catch (Exception e)
            {
                commProvider = null;
                throw e;
            }
        }
        
        
    }
    
    
    @Override
    protected void doStop() throws SensorHubException
    {
        started = false;
        
        if (commProvider != null)
        {
            commProvider.stop();
            commProvider = null;
        }
    }
    

    @Override
    public void cleanup() throws SensorHubException
    {
       
    }
    
    
    @Override
    public boolean isConnected()
    {
        return (commProvider != null);
    }
}
