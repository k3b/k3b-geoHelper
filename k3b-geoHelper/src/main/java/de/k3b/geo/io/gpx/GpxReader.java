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
 * Reads {@link de.k3b.geo.api.GeoPointDto} from gpx file or stream.<br/>
 *
 * inspired by http://stackoverflow.com/questions/672454/how-to-parse-gpx-files-with-saxreader
 */
public class GpxReader<T extends IGeoPointInfo> extends GpxReaderBase implements IGeoInfoHandler {
    private List<T> track;

    /**
     * Creates a new GpxReader
     * @param reuse if not null this instance is cleared and then reused for every new gpx found
     */
    public GpxReader(final GeoPointDto reuse) {
        super(null,reuse);
        this.onGotNewWaypoint = this; // cannot do this in constructor
    }

    public List<T> getTracks(InputSource in) throws IOException {
        track = new ArrayList<T>();
        parse(in);
        return track;
    }

    /** is called for every completed gpx-trackpoint */
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
