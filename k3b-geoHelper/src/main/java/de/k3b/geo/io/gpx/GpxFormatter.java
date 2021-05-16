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

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.api.ILocation;
import de.k3b.geo.io.GeoUriDef;

/**
 * Formats {@link de.k3b.geo.api.GeoPointDto}-s or {@link de.k3b.geo.api.ILocation}-s as
 * gpx-xml-fragment without the xml-root element and with no xml-namespace.<br/>
 *
 * ```java
 *  StringBuilder xmlString = new StringBuilder()
 *       .append("<gpx>\n");
 *
 *  GeoPointDto geo = new GeoPointDto()
 *       .setLatitude(52.1)
 *       .setLongitude(9.2);
 *  GpxFormatter.toGpx(xmlString, geo);
 *
 *  xmlString.append("\n</gpx>\n");
 *
 *  System.out.print(xmlString);
 *
 * ```
 * Created by k3b on 07.01.2015.
 */
public class GpxFormatter {
	private static final String TEMP_AMP = "##!!##!!";
    public static final DateFormat TIME_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    private static final String HEADER = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<gpx version='1.1' xmlns='http://www.topografix.com/GPX/1/1'\n" +
            "\txmlns:topografix='http://www.topografix.com/GPX/Private/TopoGrafix/0/1'\n" +
            "\txmlns:k3b='uri:https://github.com/k3b/k3b-geoHelper/'\n" +
            "\txmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n" +
            "\txsi:schemaLocation='http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.topografix.com/GPX/Private/TopoGrafix/0/1 http://www.topografix.com/GPX/Private/TopoGrafix/0/1/topografix.xsd'>";
    private static final String FOOTER = "</gpx>";

    // if indent>0 do pretty format with new-line and indention
    private static int indent = 0;

    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private GpxFormatter() {}

    public static void export(List<IGeoPointInfo> geoInfos, PrintWriter printWriter) throws IOException {
        if (printWriter != null) {
            try {
                printWriter.println(toGpxXml(geoInfos));
            } finally {
                printWriter.close();
            }
        }
    }

    public static String toGpxXml(List<IGeoPointInfo> geoInfos) {
        StringBuilder buffer = new StringBuilder();

        int oldIndent = indent;
        try {
            indent++;
            for (IGeoPointInfo geoInfo : geoInfos) {
                if (!GeoPointDto.isEmpty(geoInfo)) {

                    if (buffer.length() == 0) {
                        buffer.append(HEADER);
                    }
                    toGpx(buffer, geoInfo);
                }
            }
        } finally {
            if (buffer.length() > 0) {
                buffer.append(FOOTER);
            }
            indent = oldIndent;
        }
        return buffer.toString();
    }

    /** Add gpx-xml-fragments to result */
    public static StringBuilder toGpx(StringBuilder result, IGeoPointInfo location) {
        return toGpx(result, location.getLatitude(), location.getLongitude(),
                location.getTimeOfMeasurement(), location.getName(),location.getDescription(),
                location.getLink(), location.getSymbol(), location.getId(), location.getZoomMin(), location.getZoomMax());
    }

    /** Add gpx-xml-fragments to result */
    public static StringBuilder toGpx(StringBuilder result, ILocation location,
                                     String description, String link) {
        return toGpx(result, location.getLatitude(), location.getLongitude(),
                location.getTimeOfMeasurement(), location.toString(),description, link, null, null, -1,-1);
    }

    /** Add gpx-xml-fragments to result */
    private static StringBuilder toGpx(StringBuilder result, double latitude, double longitude,
                                       Date timeOfMeasurement, String name,
                                       String description, String link, String symbol, String id, int zoomMin, int zoomMax) {
        indent(result).append("<" +
                XmlDefinitions.GpxDef_11.TRKPT +
                " " +
                XmlDefinitions.GpxDef_11.ATTR_LAT +
                "='")
                .append(latitude)
                .append("' " +
                        XmlDefinitions.GpxDef_11.ATTR_LON +
                        "='")
                .append(longitude)
                .append("'>");
        if (indent > 0) indent++;
        if (name != null) {
            addElement(result, XmlDefinitions.GpxDef_11.NAME, name);
        }
        if (description != null) {
            addElement(result, XmlDefinitions.GpxDef_11.DESC, description);
        }
        if (link != null) {
            indent(result).append("<" +
                    XmlDefinitions.GpxDef_11.LINK +
                    " " +
                    XmlDefinitions.GpxDef_11.ATTR_LINK +
                    "='")
                    .append(escapeAttribute(link))
                    .append("' />");
        }

        if (timeOfMeasurement != null || symbol != null || zoomMax > 0 || zoomMin > 0 || id != null) {
            indent(result).append("<extensions>");
            if (indent > 0) indent++;

            if (timeOfMeasurement != null) {
                addElement(result, "k3b:" + XmlDefinitions.GpxDef_11.TIME, TIME_FORMAT.format(timeOfMeasurement));
            }
            if (symbol != null) {
                addElement(result, "k3b:" + XmlDefinitions.GpxDef_11.IMAGE, symbol);
            }
            if (id != null) {
                addElement(result, "k3b:" + GeoUriDef.ID, id);
            }
            if (zoomMin > 0) {
                addElement(result, "k3b:" + GeoUriDef.ZOOM, "" + zoomMin);
            }
            if (zoomMax > 0) {
                addElement(result, "k3b:" + GeoUriDef.ZOOM_MAX, "" + zoomMax);
            }
            if (indent > 0) indent--;

            indent(result).append("</extensions>");
        }
        if (indent > 0) indent--;
        indent(result).append("</" +
                XmlDefinitions.GpxDef_11.TRKPT +
                ">\n");
        return result;
    }

    private static StringBuilder indent(StringBuilder result) {
        if (indent > 0) {
            result.append('\n');
            for (int i = 0; i < indent; i++) {
                result.append('\t');
            }
        }
        return result;
    }

    /** add (name>value(/name> to result */
    private static void addElement(StringBuilder result, String name, String value) {
        indent(result).append("<").append(name).append(">").append(escapeElement(value)).append("</").append(name).append(">");
    }

    /** replace chars that are illegal for xml elements */
    private static String escapeElement(String value) {
        if (value != null) {
            return value
                    .replace("&",TEMP_AMP)
                    .replace("<","&lt;")
                    .replace(">","&gt;")
                    .replace(TEMP_AMP,"&amp;") // to prevent "&lt;", "&gt;" from being escaped
                    ;
        }
        return null;
    }

    /** q&d: replace chars that are illegal for xml attributes */
    private static String escapeAttribute(String value) {
        if (value != null) {
            return escapeElement(value
                    .replace('\n',' ')
                    .replace('\r',' ')
                    .replace('\'','"')
            ).replace("  ", " ");
        }
        return null;
    }

}
