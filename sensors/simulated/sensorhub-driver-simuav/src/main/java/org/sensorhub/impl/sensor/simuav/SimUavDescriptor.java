/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2012-2015 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

<<<<<<<< HEAD:sensors/hydro/sensorhub-driver-intellisense/src/main/java/org/sensorhub/impl/sensor/intellisense/IntellisenseDescriptor.java
package org.sensorhub.impl.sensor.intellisense;
========
package org.sensorhub.impl.sensor.simuav;
>>>>>>>> 08f4e0eba38597d2fa250f0a6f6c1cdc93297ad5:sensors/simulated/sensorhub-driver-simuav/src/main/java/org/sensorhub/impl/sensor/simuav/SimUavDescriptor.java

import org.sensorhub.api.module.IModule;
import org.sensorhub.api.module.IModuleProvider;
import org.sensorhub.api.module.ModuleConfig;
import org.sensorhub.impl.module.JarModuleProvider;


<<<<<<<< HEAD:sensors/hydro/sensorhub-driver-intellisense/src/main/java/org/sensorhub/impl/sensor/intellisense/IntellisenseDescriptor.java
public class IntellisenseDescriptor extends JarModuleProvider implements IModuleProvider
========
public class SimUavDescriptor extends JarModuleProvider implements IModuleProvider
>>>>>>>> 08f4e0eba38597d2fa250f0a6f6c1cdc93297ad5:sensors/simulated/sensorhub-driver-simuav/src/main/java/org/sensorhub/impl/sensor/simuav/SimUavDescriptor.java
{
    @Override
    public Class<? extends IModule<?>> getModuleClass()
    {
<<<<<<<< HEAD:sensors/hydro/sensorhub-driver-intellisense/src/main/java/org/sensorhub/impl/sensor/intellisense/IntellisenseDescriptor.java
        return IntellisenseSensor.class;
========
        return SimUavDriver.class;
>>>>>>>> 08f4e0eba38597d2fa250f0a6f6c1cdc93297ad5:sensors/simulated/sensorhub-driver-simuav/src/main/java/org/sensorhub/impl/sensor/simuav/SimUavDescriptor.java
    }
    

    
    @Override
    public Class<? extends ModuleConfig> getModuleConfigClass()
    {
<<<<<<<< HEAD:sensors/hydro/sensorhub-driver-intellisense/src/main/java/org/sensorhub/impl/sensor/intellisense/IntellisenseDescriptor.java
        return IntellisenseConfig.class;
========
        return SimUavConfig.class;
>>>>>>>> 08f4e0eba38597d2fa250f0a6f6c1cdc93297ad5:sensors/simulated/sensorhub-driver-simuav/src/main/java/org/sensorhub/impl/sensor/simuav/SimUavDescriptor.java
    }
}
