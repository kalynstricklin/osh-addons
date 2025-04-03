/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2025 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.utils.aero;

import java.time.Instant;
import java.util.Collection;


/**
 * <p>
 * Read-only interface for a flight plan / route.
 * </p>
 *
 * @author Alex Robin
 * @since Jan 27, 2025
 */
public interface IFlightPlan extends IFlightIdentification, IAircraftIdentification
{
    
    public enum FlightPlanSource
    {
        FMS, ATC, OCC, WWX
    }
    
    
    /**
     * @return The time at which the flight plan was issued (UTC)
     */
    Instant getIssueTime();
    
    
    /**
     * @return The system the flight plan was obtained from
     */
    String getSource();
    
    
    /**
     * @return Actual or estimated departure time
     */
    Instant getDepartureTime();
    
    
    /**
     * @return Estimated arrival time
     */
    Instant getArrivalTime();
    
    
    /**
     * @return The list of alternate airports
     */
    String getAlternateAirports();
    
    
    /**
     * @return The filed cruise altitude (in ft, pressure altitude, NaN if unknown)
     */
    double getCruiseAltitude();
    
    
    /**
     * @return The filed cruise true air speed (in knots, NaN if unknown)
     */
    double getCruiseSpeed();
    
    
    /**
     * @return The filed cruise mach (NaN if unknown)
     */
    double getCruiseMach();
    
    
    /**
     * @return The cost index for this flight (NaN if unknown)
     */
    double getCostIndex();
    
    
    /**
     * @return The fuel burn correction factor (NaN if unknown)
     */
    double getFuelFactor();
    
    
    /**
     * @return Coded route (may include STAR/SID and airway codes)
     */
    String getCodedRoute();
    
    
    /**
    * @return List of all waypoints composing the route (decoded form)
    */
    Collection<? extends IWaypoint> getWaypoints();
}
