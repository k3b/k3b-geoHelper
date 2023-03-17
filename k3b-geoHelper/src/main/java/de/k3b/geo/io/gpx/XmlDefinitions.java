/*
 * Copyright (c) 2015-2021 by k3b.
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

public class XmlDefinitions {

    /** xml-elements for gpx version 1.0 */
    public static class GpxDef_10 {
        public static final String WPT = "wpt"; // alias for "trkpt"
        public static final String URL = "url"; // alias for "link"
    }

    /** xml-elements for kml */
    public static class KmlDef_22 {
        public static final String PLACEMARK = "Placemark";
        public static final String DESCRIPTION = "description";

        // note KmlDef_22.COORDINATES use lon,lat reverse order
        public static final String COORDINATES = "coordinates";
        public static final String COORDINATES2 = "coord";
        public static final String TIMESTAMP_WHEN = "when";
        public static final String TIMESPAN_BEGIN = "begin";

        // kml-symbols: icons are defined seperatly from icon use
        public static final String ICON_DEFINITION = "IconStyle";
        public static final String ATTR_DEFINITION_ID = "id";
        public static final String ICON_DEFINITION_URL = "href";
        public static final String ICON_REFERENCE_ID = "styleUrl";
    }

    /** xml-elements for wikimedia (i.e. wikipedia or wikivoyage) */
    public static class WikimediaDef {
        public static final String PAGE = "page";
        public static final String ATTR_LINK = "fullurl"; // <page fullurl=...
        public static final String ATTR_ID = "pageid";
        public static final String ATTR_TITLE = "title";
        public static final String ATTR_TIME = "touched";

        public static final String COORDINATE = "co";

        public static final String IMAGE = "thumbnail";
        public static final String ATTR_IMAGE = "source";

        public static final String DESCRIPTION = "extract";

        /** used by wikidata */
        public static final String NAME = "label";

    }

    /** xml-elements for gpx version 1.1 */
    public static class GpxDef_11 {
        public static final String TRKPT = "trkpt";
        public static final String ATTR_LAT = "lat";
        public static final String ATTR_LON = "lon";
        public static final String NAME = "name";
        public static final String DESC = "desc";
        public static final String TIME = "time";
        public static final String LINK = "link"; // <link href=.. /> also used by atom
        public static final String ATTR_LINK = "href";
        public static final String IMAGE = "sym";
    }
}

