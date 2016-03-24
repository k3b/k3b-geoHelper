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

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;

/**
 * Created by EVE on 20.04.2015.
 */
public class GeoXmlOrTextParserTests {
    @Test
    public void parseXml()  {
        String data = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<root><poi ll='52.2,9.2'/><poi ll='52.1,9.1'/></root>";

        List<IGeoPointInfo> result = new GeoXmlOrTextParser<IGeoPointInfo>().get(new GeoPointDto(), data);
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void parseXmlFragment()  {
        String data = "<poi ll='52.2,9.2'/><poi ll='52.1,9.1'/>";

        List<IGeoPointInfo> result = new GeoXmlOrTextParser<IGeoPointInfo>().get(new GeoPointDto(), data);
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void parseUris()  {
        String data = "#test uri\n" +
            "geo:52.1,9.1\n" +
            "geo:52.2,9.2\n";

        List<IGeoPointInfo> result = new GeoXmlOrTextParser<IGeoPointInfo>().get(new GeoPointDto(), data);
        Assert.assertEquals(2, result.size());
    }


}
