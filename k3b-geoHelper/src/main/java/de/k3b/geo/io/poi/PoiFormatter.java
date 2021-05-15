/*
 * Copyright (c) 2021 by k3b.
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
package de.k3b.geo.io.poi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.api.ILocation;
import de.k3b.geo.io.GeoFormatter;
import de.k3b.geo.io.GeoUriDef;
import de.k3b.util.XmlUtil;

/**
 * Formats {@link de.k3b.geo.api.GeoPointDto}-s as
 * poi-xml-fragment without the xml-root element and with no xml-namespace.<br/>
 *
 * ```java
 *  StringBuilder xmlString = new StringBuilder()
 *       .append("<poi>\n");
 *
 *  GeoPointDto geo = new GeoPointDto()
 *       .setLatitude(52.1)
 *       .setLongitude(9.2);
 *  PoiFormatter.toPoi(xmlString, geo);
 *
 *  xmlString.append("\n</poi>\n");
 *
 *  System.out.print(xmlString);
 *
 * ```
 * Created by k3b on 07.01.2015.
 */

public class PoiFormatter {
    public static final DateFormat TIME_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    private static final String HEADER = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<k3b xmlns='uri:https://github.com/k3b/k3b-geoHelper/'>";
    private static final String FOOTER = "\n</k3b>";
    private static final String INDENT = "\n\t";

    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String toPoiXml(List<IGeoPointInfo> geoInfos) {
        StringBuilder buffer = new StringBuilder();

        try {
            for (IGeoPointInfo geoInfo : geoInfos) {
                if (!GeoPointDto.isEmpty(geoInfo)) {

                    if (buffer.length() == 0) {
                        buffer.append(HEADER);
                    }
                    toPoi(buffer.append(INDENT), geoInfo);
                }
            }
        } finally {
            if (buffer.length() > 0) {
                buffer.append(FOOTER);            }
        }
        return buffer.toString();
    }

    private PoiFormatter() {
    }

    /** Add poi-xml-fragments to result */
    public static StringBuilder toPoi(StringBuilder result, IGeoPointInfo location) {
        return toPoi(result, location.getId(), location.getLatitude(), location.getLongitude(),
                location.getTimeOfMeasurement(), location.getName(),location.getDescription(),
                location.getLink(), location.getSymbol(), location.getZoomMin(),
                location.getZoomMax());
    }

    /** Add poi-xml-fragments to result */
    public static StringBuilder toPoi(StringBuilder result, ILocation location) {
        return toPoi(result, null, location.getLatitude(), location.getLongitude(),
                location.getTimeOfMeasurement(), null, null, null, null, -1, -1);
    }

    /** Add poi-xml-fragments to result */
    private static StringBuilder toPoi(StringBuilder result, String id, double latitude, double longitude,
                                       Date timeOfMeasurement, String name,
                                       String description, String link, String symbol, int zoomMin, int zoomMax) {
        // <poi ll="52,9" n="theName" link="theLink" s="theIconUrl"  d="theDesc" t="2015-02-10T08:04:45Z" z="5" z2="7">
        result.append("<" + GeoUriDef.XML_ELEMENT_POI);
        addAttr(result,GeoUriDef.DESCRIPTION , description);
        addAttr(result,GeoUriDef.ID , id);
        addAttr(result,GeoUriDef.LAT_LON,
                String.format(Locale.US, "%f,%f", latitude,longitude) ,
                !GeoPointDto.isEmpty(latitude,longitude));
        addAttr(result,GeoUriDef.TIME ,
                GeoFormatter.formatDate(timeOfMeasurement),
                timeOfMeasurement != null);
        addAttr(result,GeoUriDef.NAME , name);
        addAttr(result,GeoUriDef.LINK , link);
        addAttr(result,GeoUriDef.SYMBOL , symbol);
        addAttr(result,GeoUriDef.ZOOM , ""+ zoomMin, zoomMin > 0);
        addAttr(result,GeoUriDef.ZOOM_MAX , ""+ zoomMax, zoomMax > 0);
        result.append(" />");
        return result;
    }

    /** add (name>value(/name> to result */
    private static void addAttr(StringBuilder result, String name, String value) {
        boolean condition = value != null;
        addAttr(result, name, value, condition);
    }

    private static void addAttr(StringBuilder result, String name, String value, boolean condition) {
        if (condition) {
            result.append(" ").append(name).append("='").append(XmlUtil.escapeXmlAttribute(value)).append("'");
        }
    }
}
