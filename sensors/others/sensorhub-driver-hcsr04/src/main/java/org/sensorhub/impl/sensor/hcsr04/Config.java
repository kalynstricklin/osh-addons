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

package org.sensorhub.impl.sensor.hcsr04;

import org.sensorhub.api.config.DisplayInfo.Required;
import org.sensorhub.api.sensor.SensorConfig;
import java.util.ArrayList;


public class Config extends SensorConfig {
    @Required
    public String serialNumber = "0001";

    @Required
    public String gpioInput = "24";

    @Required
    public String gpioOutput = "23";

    public String command = String.valueOf(true);

    public Config()
    {
        this.moduleClass = Sensor.class.getCanonicalName();
    }
}