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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.InputSource;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;

/**
 * Class to read {@link List} of {@link de.k3b.geo.api.IGeoPointInfo} points from gpx/kml/xml/... file or stream.
 *
 * ![GpxReader-getTracks](GpxReader-getTracks.png)
 **
 * ```java
 * GpxReader<IGeoPointInfo> parser = new GpxReader<IGeoPointInfo>();
 * List<IGeoPointInfo> points = parser.getTracks(new InputSource(new FileReader( "test.gpx")));
 * for (IGeoPointInfo geo : points) {
 *    System.out.print(String.format("got lat=%f lon=%f\n", geo.getLatitude(),geo.getLongitude()));
 * }
 * ```
 *
 *
 * Supported formats:
 *
 * * [gpx-1.0](https://github.com/k3b/androFotoFinder/wiki/data#gpx10) and [gpx-1.1](https://github.com/k3b/androFotoFinder/wiki/data#gpx) files
 * * [kml-2.2](https://github.com/k3b/androFotoFinder/wiki/data#kml) files used by google
 * * [wikimedia](https://github.com/k3b/androFotoFinder/wiki/data#wikimedia) that is used by web-apis of wikipedia and wikivoyage
 * * [poi](https://github.com/k3b/androFotoFinder/wiki/data#poi) files, k3b-s internal xml format
 *
 * Inspired by http://stackoverflow.com/questions/672454/how-to-parse-gpx-files-with-saxreader
 */
public class GpxReader<T extends IGeoPointInfo> extends GpxReaderBase implements IGeoInfoHandler {
    /** Used to collect the reived points */
    private List<T> track;

    /**
     * Creates a new GpxReader.
     *
     * @since 1.1.5
     */
    public GpxReader() {
        this(new GeoPointDto());
    }
    /**
     * Creates a new GpxReader.
     *
     * @param reuse if not null this instance is cleared and then reused for every new gpx found. This way the reader can load different implementations of {@link de.k3b.geo.api.IGeoPointInfo}
     */
    public GpxReader(final GeoPointDto reuse) {
        super(null,reuse);
        this.onGotNewWaypoint = this; // cannot do this in constructor
    }

    /**
     * Call the parser and return the points contained in the stream
     *
     * ![GpxReader-getTracks](GpxReader-getTracks.png)
     *
     * @startuml GpxReader-getTracks.png
     * title Read gpx/kml/poi/xml data to List
     * interface List
     * interface IGeoPointInfo
     *
     * class GpxReader
     * GpxReader : getTracks
     *
     * GpxReader -> List
     * List *-- IGeoPointInfo
     * InputSource -> GpxReader : "file:points.gpx"
     * InputSource -> GpxReader : "file:points.kml"
     * InputSource -> GpxReader : "file:points.poi"
     * InputSource -> GpxReader : "https://de.wikivoyage.org/w/api.php?..."
     * @enduml
     *
     */
    public List<T> getTracks(InputSource in) throws IOException {
        track = new ArrayList<T>();
        parse(in);
        return track;
    }

    /** Is called for every completed gpx-trackpoint to collect the received tracks. */
    @Override
    public boolean onGeoInfo(IGeoPointInfo geoInfo) {
        if (mReuse != null) {
            track.add((T) mReuse.clone());
        } else {
            track.add((T) this.current);
        }
        return true;
    }
}
