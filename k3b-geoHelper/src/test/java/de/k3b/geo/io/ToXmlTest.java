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
package de.k3b.geo.io;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.gpx.GeoXmlOrTextParser;
import de.k3b.geo.io.gpx.GpxFormatter;
import de.k3b.geo.io.kml.KmlFormatter;
import de.k3b.geo.io.poi.PoiFormatter;
import de.k3b.util.IsoDateTimeParser;

public class ToXmlTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToXmlTest.class);

    private final IGeoPointInfo geo = new GeoPointDto()
            .setLatLon(53.1099972, 8.7178206)
            .setName("myName  with forbidden chars <hello world='' >")
            .setDescription("my Description  with forbidden chars <hello world='' >")
            .setSymbol("https://server/path/to/Symbol.jpg?hello=world&32")
            .setLink("https://server/path/to/link?hello=world&32")
            .setTimeOfMeasurement(IsoDateTimeParser.parse("2014-12-19T21:13:21Z"))

            // unsupported
            .setId("myId with forbidden chars <hello world='' >")
            .setZoomMax(33)
            .setZoomMin(12)
            ;
    IGeoPointInfo geoMinimal = new GeoPointDto().setLatLon(53, 8);
    IGeoPointInfo geoEmpty = new GeoPointDto();
    private List<IGeoPointInfo> geoInfos = Arrays.asList(geo, geoMinimal, geoEmpty);

    @Test
    public void testKmlExport() throws IOException {
        StringWriter stringWriter = new StringWriter();

        KmlFormatter.export(geoInfos, new PrintWriter(stringWriter));

        String resultXml = stringWriter.toString();
        checkGeneratedXml(resultXml, "testKmlExport\n");
    }

    @Test
    public void testPoiExport() {
        String resultXml = PoiFormatter.toPoiXml(geoInfos);
        checkGeneratedXml(resultXml, "testPoiExport\n");
    }

    @Test
    public void testGpxExport() {
        String resultXml = GpxFormatter.toGpxXml(geoInfos);
        checkGeneratedXml(resultXml, "testGpxExport\n");
    }

    private void checkGeneratedXml(String resultXml, String logMessage) {
        LOGGER.info(logMessage + resultXml);

        List<IGeoPointInfo> importResult = new ArrayList<>();
        try {
            importResult = new GeoXmlOrTextParser<IGeoPointInfo>().get(new GeoPointDto(), resultXml);
        } catch (Throwable ex) {
            Assert.fail("Cannot parse xml\n"
                    + resultXml
                    + "\n" + ex.getMessage());
        }
        Assert.assertEquals("size (all except empty)", geoInfos.size() - 1, importResult.size());
        Assert.assertEquals(
                "xml",
                toXmlString(geo),
                toXmlString(importResult.get(0)));
    }

    protected String toXmlString(IGeoPointInfo geo) {
        return PoiFormatter.toPoi(new StringBuilder(), geo).toString();
    }
}