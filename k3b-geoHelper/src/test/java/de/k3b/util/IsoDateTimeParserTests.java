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

package de.k3b.util;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by k3b on 12.02.2015.
 */
public class IsoDateTimeParserTests {
    // SSS is millisecs and Z is timezone relative to gmt.
    private static final SimpleDateFormat ISO_FULL = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private static Date EXPECTED_WITH_MILLISECS    = IsoDateTimeParser.toDate(2001,12,24,12,34,56,789, TimeZone.getTimeZone("GMT"));
    private static Date EXPECTED_WITHOUT_MILLISECS = IsoDateTimeParser.toDate(2001,12,24,12,34,56,0, TimeZone.getTimeZone("GMT"));

    @BeforeClass
    public static void setUp() {
        ISO_FULL.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    // @Test // ignore lost millisecs
    public void shoudParseWithTimeZoneAndMillisecs() throws Exception {
        // 13 hours in timezone +01:00 is 12 hours in timezone +00:00
        assertEquals(EXPECTED_WITH_MILLISECS, "2001-12-24T13:34:56.789+01:00");
    }

    @Test
    public void shoudParseWithTimeZone() throws Exception {
        // 13 hours in timezone +01:00 is 12 hours in timezone +00:00
        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T13:34:56+01:00");
    }

    @Test
    public void shoudParseZulo() throws Exception {
        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T12:34:56Z");
        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T12:34:56+0000");
        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T12:34:56+0000Z");

        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T12:34:56-00:00");
        // used by MyTracks for Android           2015-02-10T08:04:45.000Z
        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T12:34:56.000Z");
        // assertEquals(EXPECTED_WITHOUT_MILLISECS, "2015-02-10T08:04:45.000Z");
    }

    @Test
    public void shoudParseZuloWithMillisecs() throws Exception {
        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T12:34:56.789Z");
        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T12:34:56.789-0000");
        assertEquals(EXPECTED_WITHOUT_MILLISECS, "2001-12-24T12:34:56.789+00:00");
    }

    static public void assertEquals(Date expected,
                                    String actualString) {
        Date actual = IsoDateTimeParser.parse(actualString);
        Assert.assertNotNull(actualString, actual);
        if (actual.getTime() != expected.getTime()) {
            long diff = (actual.getTime() - expected.getTime()) / 1000;
            Assert.assertEquals("dif in secs: " + diff, ISO_FULL.format(expected) + " ("+ expected.getTime() + ")", actualString + "("+ actual.getTime() + ")");
        }
    }
}
