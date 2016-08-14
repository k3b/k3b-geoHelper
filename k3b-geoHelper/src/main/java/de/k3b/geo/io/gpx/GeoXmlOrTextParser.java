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

import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoFileRepository;
import de.k3b.geo.io.GeoUri;

/**
 * Parser to extract {@link IGeoPointInfo} items from {@link String}.
 *
 * Created by k3b on 20.04.2015.
 */
public class GeoXmlOrTextParser<T extends IGeoPointInfo> {

    /** Get {@link IGeoPointInfo} items from cr/lf delimited Text lines or from xml fragments. */
    public List<T> get(String textLinesOrXml) {
        return get(new GeoPointDto(), textLinesOrXml);
    }

    /** Get {@link IGeoPointInfo} items from cr/lf delimited Text lines or from xml fragments. */
    public List<T> get(GeoPointDto geoPointDtoFactoryItem, String textLinesOrXml) {
        if (textLinesOrXml == null) return null;

        if (textLinesOrXml.startsWith(GeoFileRepository.COMMENT) || textLinesOrXml.startsWith(GeoUri.GEO_SCHEME)) {
            // lines of comments "#.." or geo-uris seperated by cr/lf
            GeoFileRepository<T> parser = new GeoFileRepository<T>(null, geoPointDtoFactoryItem) {
                @Override
                protected boolean isValid(IGeoPointInfo geo) {
                    return true;
                }
            };
            StringReader rd = new StringReader(textLinesOrXml);
            ArrayList<T> result = new ArrayList<T>();

            try {
                parser.load(result, rd);
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            String textXml = textLinesOrXml;
            if (!textLinesOrXml.startsWith("<?xml")) {
                // to allow xml-fragments without xml-root element
                textXml = "<xml>" + textLinesOrXml + "</xml>";
            }
            GpxReader<T> parser = new GpxReader<T>(geoPointDtoFactoryItem);
            StringReader rd = new StringReader(textXml);
            List<T> result = null;

            try {
                result = parser.getTracks(new InputSource(rd));
            } catch (IOException e) {
                e.printStackTrace();
            }
            rd.close();
            return result;
        }
    }

}
