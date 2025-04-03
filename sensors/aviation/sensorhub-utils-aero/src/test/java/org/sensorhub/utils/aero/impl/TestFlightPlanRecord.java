/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2025 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.utils.aero.impl;

import static org.junit.Assert.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.Test;
import org.vast.data.DataBlockProxy;


public class TestFlightPlanRecord
{

    @Test
    public void testGetSetMethods()
    {
        var schema = FlightPlanRecord.getSchema("fp");
        var fp = DataBlockProxy.generate(schema, FlightPlanRecord.class);
        fp.wrap(schema.createDataBlock());
        
        var time = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        var source = "SWIM";
        var flightNum = "DAL156";
        var fltDate = Instant.parse("2024-11-05T10:20:00Z");
        var origApt = "KDEN";
        var destApt = "KATL";
        var altApts = "KMCI,KBHM";
        var departTime = time.plus(2, ChronoUnit.HOURS);
        var tailNum = "NX5600";
        var acType = "B738";
        var ci = 95;
        var sar = 1.012;
        var route = "KDEN QUAKY1 GCN DCT WINEN DCT NEERO DCT PDT CHINS5 KATL";
        
        fp.setIssueTime(time);
        fp.setSource(source);
        fp.setFlightNumber(flightNum);
        fp.setFlightDate(fltDate);
        fp.setOriginAirport(origApt);
        fp.setDestinationAirport(destApt);
        fp.setAlternateAirports(altApts);
        fp.setDepartureTime(departTime);
        fp.setTailNumber(tailNum);
        fp.setAircraftType(acType);
        fp.setCostIndex(ci);
        fp.setFuelFactor(sar);
        fp.setCodedRoute(route);
        
        System.out.println(fp);

        assertEquals(time, fp.getIssueTime());
        assertEquals(source, fp.getSource());
        assertEquals(flightNum, fp.getFlightNumber());
        assertEquals(fltDate, fp.getFlightDate());
        assertEquals(origApt, fp.getOriginAirport());
        assertEquals(destApt, fp.getDestinationAirport());
        assertEquals(altApts, fp.getAlternateAirports());
        assertEquals(departTime, fp.getDepartureTime());
        assertEquals(tailNum, fp.getTailNumber());
        assertEquals(acType, fp.getAircraftType());
        assertEquals(ci, fp.getCostIndex(), 1e-5);
        assertEquals(sar, fp.getFuelFactor(), 1e-5);
        assertEquals(route, fp.getCodedRoute());
    }

}
