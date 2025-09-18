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

package org.sensorhub.impl.sensor.trupulse;

import net.opengis.sensorml.v20.PhysicalSystem;
import org.sensorhub.api.comm.ICommProvider;
import org.sensorhub.api.common.SensorHubException;
import org.sensorhub.impl.sensor.AbstractSensorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vast.sensorML.SMLHelper;


/**
 * <p>
 * Driver implementation supporting the Laser Technology TruPulse 360 Laser Rangefinder.
 * The TruPulse 360 includes GeoSpatial Orientation ("azimuth"), as well as inclination
 * and direct distance. When combined with a sensor that measures GPS location of the 
 * TruPulse sensor, one can calculate the geospatial position of the target.
 * </p>
 *
 * @author Mike Botts
 * @author Alex Robin
 * @since June 8, 2015
 */
public class TruPulseSensor extends AbstractSensorModule<TruPulseConfig>
{
    static final Logger log = LoggerFactory.getLogger(TruPulseSensor.class);
    
    ICommProvider<?> commProvider;
    TruPulseOutput rangeOutput;
    
    
    public TruPulseSensor()
    {        
    }
    
    
    @Override
    protected void doInit() throws SensorHubException
    {
        super.doInit();
        
        // generate identifiers: use serial number from config or first characters of local ID
        generateUniqueID("urn:lasertech:trupulse360:", config.serialNumber);
        generateXmlID("TRUPULSE_", config.serialNumber);
        
        // init main data interface
        rangeOutput = new TruPulseOutput(this);
        addOutput(rangeOutput, false);
        rangeOutput.init();
    }


    @Override
    protected void updateSensorDescription()
    {
        synchronized (sensorDescLock)
        {
            super.updateSensorDescription();

            sensorDescription.setDescription("Laser range finder for determining distance, inclination, and azimuth");

            var sml = new SMLHelper();

            sml.edit((PhysicalSystem) sensorDescription)
                    .addIdentifier(sml.identifiers.serialNumber("TP360I-" + config.serialNumber))
                    .addClassifier(sml.classifiers.sensorType("TruPulse Laser Range Finder"))
                    .addClassifier(sml.classifiers.sensorType(
                            "http://vocab.nerc.ac.uk/collection/D01/current/D0100001",  // generic laser sensor
                            "http://vocab.nerc.ac.uk/collection/L05/current/102"       // laser rangefinders
                    ))

                    .addCharacteristicList("operating_specs", sml.characteristics.operatingCharacteristics()
                            .add("voltage", sml.characteristics.operatingVoltageRange(3.0, 3.0, "V")) // AA battery
                            .add("temperature", sml.conditions.temperatureRange(-20.0, 60.0, "Cel"))
                            .add("battery_type", sml.createText()
                                    .label("Battery Type")
                                    .value("2 × AA (Alkaline or Lithium)"))
                            .add("battery_life", sml.createText()
                                    .label("Battery Life")
                                    .value("6 hours (Alkaline), 40 hours (Lithium)"))
                            .add("environmental_rating", sml.createText()
                                    .label("Environmental Rating")
                                    .value("IP67 - Waterproof & Dustproof")))

                    .addCapabilityList("system_caps", sml.capabilities.systemCapabilities()
                            .add("range", sml.createQuantity()
                                    .label("Measurement Range")
                                    .description("Min and max distance measurement range")
                                    .uomCode("m")
                                    .value(2500)) // maximum value
                            .add("range_min", sml.createQuantity()
                                    .label("Minimum Range")
                                    .uomCode("m")
                                    .value(0.5))
                            .add("accuracy_distance_high_quality", sml.capabilities.absoluteAccuracy(0.1, "m"))
                            .add("accuracy_distance_low_quality", sml.capabilities.absoluteAccuracy(0.2, "m"))
                            .add("distance_resolution_high_quality", sml.createQuantity()
                                    .label("Distance Resolution (High Quality)")
                                    .uomCode("m")
                                    .value(0.01))
                            .add("distance_resolution_low_quality", sml.createQuantity()
                                    .label("Distance Resolution (Low Quality)")
                                    .uomCode("m")
                                    .value(0.1))
                            .add("inclination_accuracy_near", sml.capabilities.absoluteAccuracy(0.1, "deg"))
                            .add("inclination_accuracy_far", sml.capabilities.absoluteAccuracy(0.2, "deg"))
                            .add("inclination_range", sml.createQuantity()
                                    .label("Inclination Measurement Range")
                                    .uomCode("deg")
                                    .value(90))
                            .add("azimuth_accuracy", sml.capabilities.absoluteAccuracy(1.0, "deg"))
                            .add("optics_magnification", sml.createQuantity()
                                    .label("Magnification")
                                    .uomCode("x")
                                    .value(5))
                            .add("display_type", sml.createText()
                                    .label("Display Type")
                                    .value("Ultra-bright LED HUD with 5 brightness levels"))
                            .add("connectivity", sml.createText()
                                    .label("Wireless Connectivity")
                                    .value("Bluetooth Classic & BLE (iOS, Android, Windows)"))
                            .add("laser_class", sml.createText()
                                    .label("Laser Class")
                                    .value("Class 1 (IEC60825-1 Ed.3:2014)"))
                            .add("laser_wavelength", sml.createQuantity()
                                    .label("Laser Wavelength")
                                    .uomCode("nm")
                                    .value(905))
                            .add("certifications", sml.createText()
                                    .label("Certifications")
                                    .value("FCC, CE, RoHS, REACH, WEEE, OIML D11, MIL-STD-810G"))
                    );

        }
    }


    @Override
    protected void doStart() throws SensorHubException
    {
        // init comm provider
        if (commProvider == null)
        {
            try
            {
                if (config.commSettings == null)
                    throw new SensorHubException("No communication settings specified");
                
                // start comm provider
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
        
        // start measurement stream
        rangeOutput.start(commProvider);
    }
    

    @Override
    protected void doStop() throws SensorHubException
    {
        if (rangeOutput != null)
            rangeOutput.stop();
        
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