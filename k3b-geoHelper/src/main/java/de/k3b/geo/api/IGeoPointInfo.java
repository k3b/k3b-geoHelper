/*
 * Copyright (c) 2015-2016 by k3b.
 *
 * This file is part of k3b-geoHelper library.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.k3b.geo.api;

import java.util.Date;

/**
 * A location or trackpoint that can be displayed in a locationmap.<br/>
 * <p/>
 * Created by k3b on 13.01.2015.
 */
public interface IGeoPointInfo extends ILocation, Cloneable  {
    public static final double NO_LAT_LON = Double.MAX_VALUE;
    public static final int NO_ZOOM = -1;

    /** Mandatory: Latitude, in degrees north. <br/>
     * In show view: navigate map to this location.<br/>
     * In geo data: map display data.<br/>
     * NO_LAT_LON if not set.<br/>
     * persistet as geo:lat,lon or geo:0,0?q=lat,lon.
     * */
    double getLatitude();

    /** Mandatory: Longitude, in degrees east.  <br/>
     * In show view: navigate map to this location.<br/>
     * In geo data: map display data.<br/>
     * NO_LAT_LON if not set.<br/>
     * persistet as geo:lat,lon or geo:0,0?q=lat,lon.
     * */
    double getLongitude();

    /** Optional:
     * In show view: navigate map to this zoom level.<br/>
     * In geo data: filter - this item is only shown if current zoom-level is >= this value.<br/>
     * NO_LAT_LON if not set.<br/>
     * NO_ZOOM means no lower bound.<br/>
     * persistet in geo-uri as geo:...&z=4
     * */
    int getZoomMin();

    /** Optional in geo data as filter criteria: this item is only shown
     * if current zoom-level is <= this value. NO_ZOOM means no upper bound.<br/>
     * persistet in geo-uri as geo:...&z2=6
     * */
    int getZoomMax();

    /** Optional: Date when the measurement was taken. Null if unknown.<br/>
     * This may be shown in a map as an alternative label<br/>
     * or used as a filter to include only geopoints of a certain date range.<br/>
     * persistet in geo-uri as geo:...&t=2015-03-24T15:39:52z  */
    Date getTimeOfMeasurement();

    /** Optional: Short non-unique text used as marker label. <br/>
     * Null if not set.<br/>
     * In show view after clicking on a marker: Caption/Title in the bubble.<br/>
     * persistet in geo-uri as geo:?q=...(name)
     * */
    String getName();

    /** Optional: Detailed description of the point displayed in popup on long-click.<br/>
     * Null if not set.<br/>
     * In show view after clicking on a marker: Text in the bubble.<br/>
     * persistet in geo-uri as geo:...&d=someDescription
     * */
    String getDescription();

    /** Optional: if not null: a unique id for this item.<br/>
     * persistet in geo-uri as geo:...&id=4711
     * */
    String getId();

    /** Optional: if not null: link-url belonging to this item.<br/>
     * In show view after clicking on a marker: clock on button ">" opens this url.<br/>
     * persistet in geo-uri as geo:...&link=https://path/to/file.html
     * */
    String getLink();
    /** Optional: if not null: icon-url belonging to this item.<br/>
     * persistet in geo-uri as geo:...&s=https://path/to/file.png
     * */
    String getSymbol();

    IGeoPointInfo clone();
}
