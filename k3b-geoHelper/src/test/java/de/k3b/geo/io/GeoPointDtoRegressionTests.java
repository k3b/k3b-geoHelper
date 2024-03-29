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

package de.k3b.geo.io;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.gpx.GpxReader;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * Regressionstests: load *.kml/*.gpx/*.poi file from resources
 * and make shure that for every parsed GeoPointDto in the result is identical to id of poi.<br/>
 *
 * Created by k3b on 19.04.2015.
 *
 * Parameter Tests see See https://github.com/Pragmatists/JUnitParams
 */
@RunWith(JUnitParamsRunner.class)
public class GeoPointDtoRegressionTests {

    private static final String REGRESSION_ROOT = "/de/k3b/geo/io/regressionTests/";
    private static final GeoUri formatter = new GeoUri(GeoUri.OPT_DEFAULT);

    private String currentResourceName = null;
    private String lastUri = null;

    @Test
    @Parameters({
            // "1, debug.xml",
            "0, empty.xml",
            "1, gpx11.gpx",
            "1, gpx10.gpx",
            "5, kml22.kml",
            "2, gpx-similar.gpx",
            "10, poi.xml",
            "2, wikimedia.poi",
            "14, geo-uri.xml",
            "15, https-mapservice-urls.xml"})
    public void checkPoiResource(int expectedNumberOfPois, String resourceName)  {
        List<IGeoPointInfo> pois = getiGeoPointInfos(resourceName);
        Assert.assertEquals("expectedNumberOfPois", expectedNumberOfPois, pois.size());
        int index = 1;
        for (IGeoPointInfo poi : pois) {
            checkPoi(index++, poi, poi.getId());
        }
    }

    private List<IGeoPointInfo> getiGeoPointInfos(String resourceName) {
        List<IGeoPointInfo> pois = null;
        try (InputStream xmlStream = getStream(resourceName)) {
            GpxReader<IGeoPointInfo> parser = new GpxReader<IGeoPointInfo>(null) {
                @Override
                protected GeoUri createGeoUriParser(int modes) {
                    lastUri = null;
                    return new GeoUri(modes) {
                        @Override
                        public <TGeo extends GeoPointDto>  TGeo fromUri(String uri, TGeo parseResult) {
                            lastUri = uri;
                            return super.fromUri(uri, parseResult);
                        }
                    };
                }
            };
            pois = parser.getTracks(new InputSource(xmlStream));
        } catch (Exception e) {
            Assert.fail("cannot load  " +
                    resourceName + ": " + e.getMessage() + "\n" + e.getStackTrace());
            e.printStackTrace();
        }
        return pois;
    }

    private InputStream getStream(String _resourceName) {
        this.currentResourceName = REGRESSION_ROOT + _resourceName;

        // this does not work with test-resources :-(
        // or i donot know how to do it with AndroidStudio-1.02/gradle-2.2
        InputStream result = this.getClass().getResourceAsStream(this.currentResourceName);

        if (result == null) {
            File prjRoot = new File(".").getAbsoluteFile();
            while (prjRoot.getName().compareToIgnoreCase("LocationMapViewer") != 0) {
                prjRoot = prjRoot.getParentFile();
                if (prjRoot == null) return null;
            }

            // assuming this src folder structure:
            // .../LocationMapViewer/k3b-geoHelper/src/test/resources/....
            File resourceFile = new File(prjRoot, "k3b-geoHelper/src/test/resources" + _resourceName);

            this.currentResourceName = resourceFile.getAbsolutePath(); // . new Path(resourceName).get;
            try {
                result = new FileInputStream(this.currentResourceName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    private void checkPoi(int index, IGeoPointInfo poi, String expected) {
        String actual= formatter.toUriString(new GeoPointDto(poi).setId(null));

        Assert.assertEquals(this.currentResourceName + "[" + index +
                "]: ", expected, actual);
    }
}
