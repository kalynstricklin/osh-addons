/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2019 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.service.sta;

import org.sensorhub.api.common.BigId;
import org.sensorhub.api.data.IDataStreamInfo;
import org.sensorhub.api.datastore.DataStoreException;
import org.sensorhub.api.datastore.obs.DataStreamKey;
import org.sensorhub.api.datastore.obs.IDataStreamStore;

/**
 * <p>
 * Extension to datastream store interface to handle associate with Things
 * entities
 * </p>
 *
 * @author Alex Robin
 * @date Oct 20, 2019
 */
public interface ISTADataStreamStore extends IDataStreamStore
{
    
    public DataStreamKey add(long thingID, IDataStreamInfo dsInfo) throws DataStoreException;
    
    
    public IDataStreamInfo put(long thingID, DataStreamKey key, IDataStreamInfo value);
    
    
    public void putThingAssoc(long thingID, long dsID);
    
    
    public BigId getAssociatedThing(long dataStreamID);
}
