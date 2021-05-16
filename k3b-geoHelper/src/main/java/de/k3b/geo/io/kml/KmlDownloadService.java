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

package de.k3b.geo.io.kml;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipOutputStream;

import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.DownloadSymbolsBaseService.ITranslateSymbolUri;
import de.k3b.geo.io.DownloadSymbolsToDirService;
import de.k3b.geo.io.DownloadSymbolsToZipService;

/** Dowloads Symbols in List<IGeoPointInfo> points and saves them either to local file or to Zip/kmz */
public class KmlDownloadService {
    protected final String userAgent;
    private final ITranslateSymbolUri translateSymbolUri;

    /**
     * @param userAgent a string identifying the calling app.
     *                  i.e. "MyHelloWikipediaApp/1.0 (https://github.com/MyName/MyHelloWikipediaApp)"
     *                  see https://meta.wikimedia.org/wiki/Special:MyLanguage/User-Agent_policy
     * @param translateSymbolUri Under Android you can use this to translate File-Uris to Android-Content-uris
     */
    public KmlDownloadService(String userAgent, ITranslateSymbolUri translateSymbolUri) {
        this.userAgent = userAgent;
        this.translateSymbolUri = translateSymbolUri;
    }

    /**
     * Examples use of {@link #saveAs(List, File)}
     * * saveAs(pints, new File("/path/to/GrandCanaria.kml"))
     * ** also generates symbols like /path/to/GrandCanaria/aeropuerto.jpg
     * * saveAs(pints, new File("/path/to/GrandCanaria.kmz"))
     * ** the generated kmz-zip file caontains
     * *** doc.kml
     * *** some symbol file files/aeropuerto.jpg
     * */
    public void saveAs(List<IGeoPointInfo> points, File outFile) throws IOException {
        String outFileName = outFile.getName().toLowerCase();
        if (outFileName.endsWith(".kmz") || outFileName.endsWith(".zip")) {
            // kmz = kml in zip file
            // see https://developers.google.com/kml/documentation/kmzarchives
            // conventions: only first kml in zip is used (usually doc.kml)
            // referenced relative local items in zip-folder "files"

            DownloadSymbolsToZipService downloadService = new DownloadSymbolsToZipService(userAgent);
            downloadService.translateSymbolUri(translateSymbolUri);

            outFile.getParentFile().mkdirs();
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(outFile));

            points = downloadService.zipOutputStream(zip, "files").convert(points);

            KmlFormatter.export(points, new PrintWriter(downloadService.createOutputStream("doc.kml", null, 0)));
            zip.close();
        } else {
            DownloadSymbolsToDirService downloadService = new DownloadSymbolsToDirService(userAgent);
            downloadService.translateSymbolUri(translateSymbolUri);

            String baseName = FilenameUtils.getBaseName(outFile.getPath());
            File dir = new File(outFile.getParentFile(), baseName);
            dir.mkdirs();

            points = downloadService.dir(dir).convert(points);
            KmlFormatter.export(points, new PrintWriter(new FileOutputStream(outFile)));
        }
    }
}
