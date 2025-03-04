/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2020 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.service.mfapi.mf;

import java.io.IOException;
import java.util.Map;
import org.sensorhub.api.common.BigId;
import org.sensorhub.api.data.IDataStreamInfo;
import org.sensorhub.api.data.IObsData;
import org.sensorhub.api.database.IObsSystemDatabase;
import org.sensorhub.api.datastore.SpatialFilter;
import org.sensorhub.api.datastore.obs.DataStreamKey;
import org.sensorhub.api.datastore.obs.IObsStore;
import org.sensorhub.api.datastore.obs.ObsFilter;
import org.sensorhub.api.event.IEventBus;
import org.sensorhub.impl.service.sweapi.InvalidRequestException;
import org.sensorhub.impl.service.sweapi.ObsSystemDbWrapper;
import org.sensorhub.impl.service.sweapi.ServiceErrors;
import org.sensorhub.impl.service.sweapi.RestApiServlet.ResourcePermissions;
import org.sensorhub.impl.service.sweapi.resource.BaseResourceHandler;
import org.sensorhub.impl.service.sweapi.resource.RequestContext;
import org.sensorhub.impl.service.sweapi.resource.ResourceFormat;
import org.sensorhub.impl.system.DataStreamTransactionHandler;
import org.vast.data.ScalarIterator;
import net.opengis.swe.v20.DataComponent;
import org.sensorhub.impl.service.sweapi.resource.ResourceBinding;
import org.sensorhub.impl.service.sweapi.resource.RequestContext.ResourceRef;


public class TemporalPropHandler extends BaseResourceHandler<BigId, IObsData, ObsFilter, IObsStore>
{
    public static final String[] NAMES = { "tproperties" };
    
    final IEventBus eventBus;
    final IObsSystemDatabase db;
    
    
    public static class ObsHandlerContextData
    {
        public BigId dsID;
        public IDataStreamInfo dsInfo;
        public BigId foiId;
        public DataStreamTransactionHandler dsHandler;
    }
    
    
    public TemporalPropHandler(IEventBus eventBus, ObsSystemDbWrapper db, ResourcePermissions permissions)
    {
        super(db.getReadDb().getObservationStore(), db.getObsIdEncoder(), db.getIdEncoders(), permissions);
        
        this.eventBus = eventBus;
        this.db = db.getReadDb();
    }
    
    
    @Override
    protected ResourceBinding<BigId, IObsData> getBinding(RequestContext ctx, boolean forReading) throws IOException
    {
        // select binding
        var format = ctx.getFormat();
        if (format.isOneOf(ResourceFormat.JSON, ResourceFormat.AUTO))
            return new TemporalPropBindingJson(ctx, idEncoders, forReading, dataStore);
        else
            throw ServiceErrors.unsupportedFormat(format);
    }


    @Override
    protected BigId getKey(RequestContext ctx, String id) throws InvalidRequestException
    {
        return decodeID(ctx, id);
    }
    
    
    @Override
    protected String encodeKey(final RequestContext ctx, BigId key)
    {
        return idEncoder.encodeID(key);
    }


    @Override
    protected ObsFilter getFilter(ResourceRef parent, Map<String, String[]> queryParams, long offset, long limit) throws InvalidRequestException
    {
        var builder = new ObsFilter.Builder();
        builder.withFois(parent.internalID);
        
        // add predicate to remove any datastreams that has only geom
        builder.withValuePredicate(obs -> {
            var dsID = obs.getDataStreamID();
            var dsInfo = db.getDataStreamStore().get(new DataStreamKey(dsID));
            return hasProperty(dsInfo.getRecordStructure());
        });
        
        // phenomenonTime param
        var phenomenonTime = parseTimeStampArg("datetime", queryParams);
        if (phenomenonTime != null)
            builder.withPhenomenonTime(phenomenonTime);
        
        // use opensearch bbox param to filter spatially
        var bbox = parseBboxArg("bbox", queryParams);
        if (bbox != null)
        {
            builder.withPhenomenonLocation(new SpatialFilter.Builder()
                .withBbox(bbox)
                .build());
        }
        
        // geom param
        var geom = parseGeomArg("location", queryParams);
        if (geom != null)
        {
            builder.withPhenomenonLocation(new SpatialFilter.Builder()
                .withRoi(geom)
                .build());
        }
        
        // limit
        // need to limit to offset+limit+1 since we rescan from the beginning for now
        if (limit != Long.MAX_VALUE)
            builder.withLimit(offset+limit+1);
        
        return builder.build();
    }
    
    
    boolean hasProperty(DataComponent struct)
    {
        for (var it = new ScalarIterator(struct); it.hasNext(); )
        {
            var c = it.next();
            if (!TemporalPropBindingJson.SKIPPED_DEFS.contains(c.getDefinition()))
                return true;
        }
        
        return false;
    }
    
    
    @Override
    protected boolean isValidID(BigId internalID)
    {
        return false;
    }


    @Override
    protected void validate(IObsData resource)
    {
        // TODO Auto-generated method stub
    }
    
    
    @Override
    public String[] getNames()
    {
        return NAMES;
    }
}
