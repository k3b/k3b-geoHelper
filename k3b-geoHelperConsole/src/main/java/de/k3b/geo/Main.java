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
package de.k3b.geo;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipOutputStream;

import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.DownloadSymbolsToDirService;
import de.k3b.geo.io.DownloadSymbolsToZipService;
import de.k3b.geo.io.kml.KmlFormatter;
import de.k3b.geo.io.wikipedia.DownloadService;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String USER_AGENT = "AndroidGeo2ArticlesMap/0.0 (https://github.com/k3b/AndroidGeo2ArticlesMap)";

    public static void main(String[] args) throws Exception {

        DownloadService service = new DownloadService("en.wikipedia.org", USER_AGENT);

        String lat = "28.12722";
        String lon = "-15.43139";

        List<IGeoPointInfo> points = service.getGeoPointInfos(lat, lon);

        File outFile = null;
        if (false) {
            outFile = new File("./test/testgeo.kml").getAbsoluteFile();
            DownloadSymbolsToDirService downloadSymbolsToDirService = new DownloadSymbolsToDirService(USER_AGENT);
            String baseName = FilenameUtils.getBaseName(outFile.getPath());
            File dir = new File(outFile.getParentFile(), baseName);
            dir.mkdirs();

            points = downloadSymbolsToDirService.dir(dir).convert(points);
            KmlFormatter.export(points, new PrintWriter(new FileOutputStream(outFile)));
        } else {
            outFile = new File("./testgeo.kml.zip").getAbsoluteFile();

            DownloadSymbolsToZipService downloadSymbolsToZipService = new DownloadSymbolsToZipService(USER_AGENT);
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(outFile));

            points = downloadSymbolsToZipService.zipOutputStream(zip, "symbols").convert(points);

            KmlFormatter.export(points, new PrintWriter(downloadSymbolsToZipService.createOutputStream("test.kml", null, 0)));
            zip.close();
        }

        for (IGeoPointInfo geo : points) {
            System.out.print(String.format("got lat=%f lon=%f\n", geo.getLatitude(),geo.getLongitude()));
        }
        LOGGER.info("Exported to " + outFile +
                " with " + points.size() +
                " elements");
    }

}
