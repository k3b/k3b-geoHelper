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

package de.k3b.geo.io.gpx;

/** xml-elements for gpx version 1.1 */
class GpxDef_11 {
    static final String TRKPT = "trkpt";
    static final String ATTR_LAT = "lat";
    static final String ATTR_LON = "lon";
    static final String NAME = "name";
    static final String DESC = "desc";
    static final String TIME = "time";
    static final String LINK = "link"; // <link href=.. /> also used by atom
    static final String ATTR_LINK = "href";
}

class GpxDef_10 {
    static final String WPT = "wpt"; // alias for "trkpt"
    static final String URL = "url"; // alias for "link"
}

class KmlDef_22 {
    static final String PLACEMARK = "Placemark";
    static final String DESCRIPTION = "description";
    static final String COORDINATES = "coordinates";
    static final String COORDINATES2 = "coord";
    static final String TIMESTAMP_WHEN = "when";
    static final String TIMESPAN_BEGIN = "begin";
}
