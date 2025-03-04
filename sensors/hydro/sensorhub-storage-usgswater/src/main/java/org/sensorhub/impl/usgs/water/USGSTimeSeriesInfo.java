/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2021 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.usgs.water;

import java.time.Instant;
import org.sensorhub.api.data.DataStreamInfo;
import org.sensorhub.api.data.IDataStreamInfo;
import org.vast.util.TimeExtent;


public class USGSTimeSeriesInfo extends DataStreamInfo implements IDataStreamInfo
{
    protected String siteNum;
    protected String paramCd;


    @Override
    public TimeExtent getPhenomenonTimeRange()
    {
        return validTime;
    }


    @Override
    public TimeExtent getResultTimeRange()
    {
        return validTime;
    }
    

    /*
     * Builder
     */
    public static class Builder extends USGSTimeSeriesInfoBuider<Builder, USGSTimeSeriesInfo>
    {
        public Builder()
        {
            this.instance = new USGSTimeSeriesInfo();
        }

        public static Builder from(USGSTimeSeriesInfo base)
        {
            return new Builder().copyFrom(base);
        }
    }


    @SuppressWarnings("unchecked")
    public static abstract class USGSTimeSeriesInfoBuider<B extends USGSTimeSeriesInfoBuider<B, T>, T extends USGSTimeSeriesInfo>
        extends DataStreamInfoBuilder<B, T>
    {
        protected USGSTimeSeriesInfoBuider()
        {
        }


        public B withTimeRange(Instant begin, Instant end)
        {            
            instance.validTime = TimeExtent.period(begin, end);
            return (B)this;
        }


        public B withSiteNum(String siteNum)
        {
            instance.siteNum = siteNum;
            return (B)this;
        }


        public B withParamCode(String paramCd)
        {
            instance.paramCd = paramCd;
            return (B)this;
        }
    }
}
