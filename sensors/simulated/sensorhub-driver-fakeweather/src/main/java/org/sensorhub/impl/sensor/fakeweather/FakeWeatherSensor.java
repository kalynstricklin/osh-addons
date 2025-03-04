/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
The Initial Developer is Botts Innovative Research Inc. Portions created by the Initial
Developer are Copyright (C) 2014 the Initial Developer. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.sensor.fakeweather;

import org.sensorhub.api.common.SensorHubException;
import org.sensorhub.impl.sensor.AbstractSensorModule;
import org.vast.sensorML.SMLHelper;
import net.opengis.sensorml.v20.PhysicalSystem;


/**
 * <p>
 * Driver implementation outputting simulated weather data by randomly
 * increasing or decreasing temperature, pressure, wind speed, and
 * wind direction.  Serves as a simple sensor to deploy as well as
 * a simple example of a sensor driver.
 * </p>
 *
 * @author Mike Botts
 * @since Dec 24, 2014
 */
public class FakeWeatherSensor extends AbstractSensorModule<FakeWeatherConfig>
{
    FakeWeatherOutput dataInterface;
    
    
    @Override
    protected void doInit() throws SensorHubException
    {
        super.doInit();
        
        // generate identifiers
        generateUniqueID("urn:osh:sensor:simweather:", config.serialNumber);
        generateXmlID("WEATHER_STATION_", config.serialNumber);
        
        // init main data interface
        dataInterface = new FakeWeatherOutput(this);
        addOutput(dataInterface, false);
        dataInterface.init();
    }


    @Override
    protected void updateSensorDescription()
    {
        synchronized (sensorDescLock)
        {
            super.updateSensorDescription();
            
            if (!sensorDescription.isSetDescription())
                sensorDescription.setDescription("Simulated weather station generating realistic pseudo-random measurements");
            
            var sml = new SMLHelper();
            sml.edit((PhysicalSystem)sensorDescription)
                .addIdentifier(sml.identifiers.serialNumber(config.serialNumber))
                .addCharacteristicList("operating_specs", sml.characteristics.operatingCharacteristics()
                    .add("voltage", sml.characteristics.operatingVoltageRange(110., 250., "V"))
                    .add("temperature", sml.conditions.temperatureRange(-20., 90., "Cel"))
                    .build());
        }
    }


    @Override
    protected void doStart() throws SensorHubException
    {
        if (dataInterface != null)
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

