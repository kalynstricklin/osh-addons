/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2012-2015 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.sensor.fakegps;

import org.sensorhub.api.common.SensorHubException;


/**
 * <p>
 * Driver implementation generating simulated GPS trajectories for multiple
 * vehicles in a given area.
 * </p>
 *
 * @author Alex Robin
 * @since Dec 19, 2020
 */
public class FakeGpsNetwork extends FakeGpsSensor
{
    
    public FakeGpsNetwork()
    {
    }
    
    
    @Override
    protected void doInit() throws SensorHubException
    {
        super.doInit();
        
        // create FOIs
        for (var route: dataInterface.routes)
            for (var asset: route.assets)
                addFoi(asset);
    }
    
    
    @Override
    protected void updateSensorDescription()
    {
        synchronized (sensorDescLock)
        {
            super.updateSensorDescription();
            sensorDescription.setDescription(FakeGpsNetworkDescriptor.DRIVER_DESC);
        }
    }


    @Override
    protected void doStart() throws SensorHubException
    {
        dataInterface.start();        
    }
    

    @Override
    protected void doStop() throws SensorHubException
    {
        if (dataInterface != null)
            dataInterface.stop();
    }
    

    @Override
    public void cleanup() throws SensorHubException
    {
       
    }
    
    
    @Override
    public boolean isConnected()
    {
        return true;
    }
}
