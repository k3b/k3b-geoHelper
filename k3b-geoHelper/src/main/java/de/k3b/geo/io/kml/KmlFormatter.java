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
package de.k3b.geo.io.kml;

import org.apache.commons.io.FilenameUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoFormatter;
import de.k3b.geo.io.GeoUriDef;
import de.k3b.util.XmlUtil;

/** convert List<IGeoPointInfo> to kml file format */
public class KmlFormatter implements Closeable {
    private static final String HEADER = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<kml xmlns='http://www.opengis.net/kml/2.2'\n" +
            "\txmlns:atom='http://www.w3.org/2005/Atom'\n" +
            "\txmlns:gx='http://www.google.com/kml/ext/2.2'\n" +
            "\txmlns:k3b='uri:https://github.com/k3b/k3b-geoHelper/' >\n" +
            "\t<Document>";
    private static final String FOOTER = "\t</Document>\n" +
            "</kml>";
    private static final String SYMBOL_HEADER = "\t\t<Style id='default'>";
    private static final String SYMBOL_FOOTER = "\t\t</Style>\n";
    private static final String IDENT3 = "\t\t\t";
    private final PrintWriter printWriter;

    private boolean created = false;
    private boolean symbolCreated = false;

    public static void export(List<IGeoPointInfo> geoInfos, PrintWriter printWriter) throws IOException {
        KmlFormatter exporter = null;
        try {
            exporter = new KmlFormatter(printWriter);

            for (IGeoPointInfo geoInfo : geoInfos) {
                exporter.writeSymbolDefinition(geoInfo);
            }
            for (IGeoPointInfo geoInfo : geoInfos) {
                if (!GeoPointDto.isEmpty(geoInfo)) {
                    exporter.writeGeoInformation(geoInfo);
                }
            }
        } finally {
            if (exporter != null) {
                exporter.close();
            }
        }
    }

    private KmlFormatter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    private void writeSymbolDefinition(IGeoPointInfo geoInfo) {
        String symbol = geoInfo != null ? geoInfo.getSymbol() : null;
        if (symbol != null && symbol.length() > 0) {
            writeSymbolHeaderIfNecessary();

            String baseName = getSymbolname(symbol);
            pr(IDENT3 + "<IconStyle id='" + baseName +"'><Icon><href>" + XmlUtil.escapeXMLElement(symbol) +
                    "</href></Icon></IconStyle>");
        }
    }

    private String getSymbolname(String symbol) {
        return XmlUtil.escapeXmlAttribute(FilenameUtils.getBaseName(symbol));
    }

    private void writeGeoInformation(IGeoPointInfo geoInfo) {
        if (symbolCreated) {
            pr(SYMBOL_FOOTER);
            symbolCreated = false;
        }
        if (geoInfo != null) {
            writeHeaderIfNecessary();
            pr("\t\t<Placemark>");

            pr("description", geoInfo.getDescription());
            pr("name", geoInfo.getName());

            pr("k3b:" + GeoUriDef.ID, geoInfo.getId());
            if (geoInfo.getSymbol() != null) {
                pr("styleUrl", "#" + getSymbolname(geoInfo.getSymbol()));
            }

            if (geoInfo.getLink() != null) {
                pr(IDENT3 + "<atom:link href='" + XmlUtil.escapeXmlAttribute(geoInfo.getLink()) +"' />");
            }

            boolean hasGeo = !GeoPointDto.isEmpty(geoInfo);
            if (hasGeo || geoInfo.getTimeOfMeasurement() != null) {
                pr(IDENT3 + "<Point>");
                if (hasGeo) {
                    // note KmlDef_22.COORDINATES use lon,lat reverse order
                    pr(String.format(Locale.US, IDENT3 + "\t<coordinates>%f,%f</coordinates>",
                            geoInfo.getLongitude(), geoInfo.getLatitude()));
                }
                if (geoInfo.getTimeOfMeasurement() != null) {
                    pr(IDENT3 + "\t<when>" + XmlUtil.escapeXMLElement(GeoFormatter.formatDate(geoInfo.getTimeOfMeasurement())) + "</when>");
                }
                pr(IDENT3 + "</Point>");
            }
            if (geoInfo.getZoomMin() > 0) {
                pr("k3b:"+ GeoUriDef.ZOOM, "" + geoInfo.getZoomMin());
            }
            if (geoInfo.getZoomMax() > 0) {
                pr("k3b:"+ GeoUriDef.ZOOM_MAX, "" + geoInfo.getZoomMax());
            }

            pr("\t\t</Placemark>");
        }
    }

    private void writeHeaderIfNecessary() {
        if (!created) {
            created = true;
            pr(HEADER);
        }
    }

    private void writeSymbolHeaderIfNecessary() {
        writeHeaderIfNecessary();
        if (!symbolCreated) {
            symbolCreated = true;
            pr(SYMBOL_HEADER);
        }
    }

    private void pr(String tag, String value) {
        if (notEmpty(value)) {
            pr(IDENT3 +
                    "<" + tag +">" +
                    XmlUtil.escapeXMLElement(value) +
                    "</" + tag +">");
        }
    }

    private boolean notEmpty(String value) {
        return value != null && value.length() > 0;
    }

    protected void pr(String xml) {
        printWriter.println(xml);
    }

    @Override
    public void close() throws IOException {
        if (created) pr(FOOTER);
        printWriter.flush();
        printWriter.close();
    }

}
