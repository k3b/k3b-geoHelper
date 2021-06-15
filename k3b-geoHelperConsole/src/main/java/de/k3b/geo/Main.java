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
package de.k3b.geo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.Geo2WikipediaDownloadWithSymbolsService;

/** tiny j2se console program to test the lib on a pc without Android */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String USER_AGENT = "AndroidGeo2ArticlesMap/0.0 (https://github.com/k3b/AndroidGeo2ArticlesMap)";

    public static void main(String[] args) throws Exception {

        try {
            Geo2WikipediaDownloadWithSymbolsService service = new Geo2WikipediaDownloadWithSymbolsService("en.wikipedia.org", USER_AGENT, null);

            String lat = "28.12722";
            String lon = "-15.43139";

            List<IGeoPointInfo> points = null;
            File outFile = null;

            points = service.saveAs(lat, lon, outFile = new File("./test/gpx/test2geo200.gpz"));
            for (IGeoPointInfo geo : points) {
                System.out.print(String.format("got lat=%f lon=%f\n", geo.getLatitude(), geo.getLongitude()));
            }
            LOGGER.info("Exported to " + outFile +
                    " with " + points.size() +
                    " elements");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
