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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.k3b.geo.api.ILocation;

/**
 * Formats {@link de.k3b.geo.api.GeoPointDto}-s or {@link de.k3b.geo.api.ILocation}-s as
 * gpx-xml-fragment without the xml-root element and with no xml-namespace.<br/>
 *
 * Created by k3b on 07.01.2015.
 */
public class GpxFormatter {
    static final DateFormat TIME_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /** Add gpx-xml-fragments to result */
    public static StringBuffer toGpx(StringBuffer result, ILocation location,
                                     String description, String link) {
        return toGpx(result, location.getLatitude(), location.getLongitude(),
                location.getTimeOfMeasurement(), location.toString(),description, link);
    }

    /** Add gpx-xml-fragments to result */
    private static StringBuffer toGpx(StringBuffer result, double latitude, double longitude,
                                      Date timeOfMeasurement, String name,
                                      String description, String link) {
        result.append("<" +
                GpxDef_11.TRKPT +
                " " +
                GpxDef_11.ATTR_LAT +
                "='")
                .append(latitude)
                .append("' " +
                        GpxDef_11.ATTR_LON +
                        "='")
                .append(longitude)
                .append("'>");
        if (timeOfMeasurement != null) {
            addElement(result, GpxDef_11.TIME, TIME_FORMAT.format(timeOfMeasurement).toString());
        }
        if (name != null) {
            addElement(result, GpxDef_11.NAME, name);
        }
        if (description != null) {
            addElement(result, GpxDef_11.DESC, description);
        }
        result.append("<" +
                GpxDef_11.LINK +
                " " +
                GpxDef_11.ATTR_LINK +
                "='")
                .append(link)
                .append("' />");

        result.append("</" +
                GpxDef_11.TRKPT +
                ">\n");
        return result;
    }

    private static void addElement(StringBuffer result, String name, String value) {
        result.append("<").append(name).append(">").append(value).append("</").append(name).append(">");
    }
}
