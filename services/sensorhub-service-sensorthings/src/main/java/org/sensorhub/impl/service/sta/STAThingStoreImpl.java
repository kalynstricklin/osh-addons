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

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Stream;
import org.h2.mvstore.RangeCursor;
import org.sensorhub.api.common.BigId;
import org.sensorhub.api.datastore.DataStoreException;
import org.sensorhub.api.datastore.feature.FeatureKey;
import org.sensorhub.api.datastore.feature.IFeatureStoreBase.FeatureField;
import org.sensorhub.impl.datastore.DataStoreUtils;
import org.sensorhub.impl.datastore.h2.MVBaseFeatureStoreImpl;
import org.sensorhub.impl.datastore.h2.MVDataStoreInfo;
import org.sensorhub.impl.datastore.h2.MVFeatureParentKey;
import org.sensorhub.impl.datastore.h2.MVDatabaseConfig.IdProviderType;
import org.sensorhub.impl.service.sta.STALocationStoreTypes.MVThingLocationKey;
import org.vast.ogc.gml.IFeature;
import org.vast.util.Asserts;


/**
 * <p>
 * Extension of {@link MVBaseFeatureStoreImpl} to handle JOIN queries of
 * Things by Location.
 * </p>
 *
 * @author Alex Robin
 * @date Oct 29, 2019
 */
class STAThingStoreImpl extends MVBaseFeatureStoreImpl<IFeature, FeatureField, STAThingFilter> implements ISTAThingStore
{
    STALocationStoreImpl locationStore;
    
    
    STAThingStoreImpl()
    {
    }
    
    
    public static STAThingStoreImpl open(STADatabase db, int idScope, IdProviderType idProviderType, MVDataStoreInfo dataStoreInfo)
    {
        return (STAThingStoreImpl)new STAThingStoreImpl().init(db.getMVStore(), idScope, idProviderType, dataStoreInfo);
    }
    
    
    @Override
    public synchronized FeatureKey add(BigId parentID, IFeature feature) throws DataStoreException
    {
        Asserts.checkNotNull(feature, IFeature.class);
        
        long internalID = idProvider.newInternalID(feature);
        var newKey = new MVFeatureParentKey(
            parentID.getIdAsLong(),
            BigId.fromLong(idScope, internalID),
            FeatureKey.TIMELESS);

        // add to store
        put(newKey, feature, true, false);
        return newKey;
    }
    
    
    @Override
    public IFeature put(FeatureKey key, IFeature feature)
    {
        try
        {
            ensureFullFeatureKey(key);
            DataStoreUtils.checkFeatureObject(feature);
            
            var fk = new MVFeatureParentKey(0L, key.getInternalID(), key.getValidStartTime());
            return put(fk, feature, true, true);
        }
        catch (DataStoreException e)
        {
            throw new IllegalStateException(e.getMessage());
        }
    }


    Stream<FeatureKey> getThingKeysByCurrentLocation(long locationID)
    {
        var first = new MVThingLocationKey(0, locationID, Instant.MAX);
        var last = new MVThingLocationKey(Long.MAX_VALUE, locationID, Instant.MIN);
        
        class Holder { Long value = null; }
        var lastThing = new Holder();
        
        var cursor = new RangeCursor<>(locationStore.locationThingTimeIndex, first, last);
        return cursor.keyStream()
            .filter(k -> !Objects.equals(k.thingID, lastThing.value))
            .filter(k -> isLatestLocation(k))
            .peek(k -> lastThing.value = k.thingID)
            .map(k -> new FeatureKey(BigId.fromLong(idScope, k.thingID)));
    }
    
    
    boolean isLatestLocation(MVThingLocationKey key)
    {
        // we need to check that this location is actually the latest for this thing
        
        var beforeLatest = new MVThingLocationKey(key.thingID, 0, Instant.MAX);
        var latestKey = locationStore.thingTimeLocationIndex.ceilingKey(beforeLatest);
        
        return latestKey != null &&
            latestKey.thingID == key.thingID &&
            Objects.equals(latestKey.time, key.time);
    }
    
    
    @Override
    protected Stream<Entry<MVFeatureParentKey, IFeature>> getIndexedStream(STAThingFilter filter)
    {
        var locationFilter = filter.getLocations();
        if (locationFilter != null)
        {
            return locationStore.selectKeys(locationFilter)
                .flatMap(k -> getThingKeysByCurrentLocation(k.getInternalID().getIdAsLong()))
                .map(k -> featuresIndex.getEntry(k));
        }
        
        return super.getIndexedStream(filter);
    }


    @Override
    public STAThingFilter.Builder filterBuilder()
    {
        return new STAThingFilter.Builder();
    }
    
}
