/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2012-2015 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.security.gxoauth;

import org.sensorhub.api.module.IModule;
import org.sensorhub.api.module.ModuleConfig;
import org.sensorhub.impl.module.JarModuleProvider;


/**
 * <p>
 * Descriptor of OAuth client module, needed for automatic discovery by
 * the ModuleRegistry.
 * </p>
 *
 * @author Alex Robin
 * @since Nov 19, 2016
 */
public class OAuthClientDescriptor extends JarModuleProvider
{

    @Override
    public Class<? extends IModule<?>> getModuleClass()
    {
        return OAuthClient.class;
    }


    @Override
    public Class<? extends ModuleConfig> getModuleConfigClass()
    {
        return OAuthClientConfig.class;
    }

}
