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

package de.k3b.geo.io;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.util.IsoDateTimeParser;

/**
 * converts between string an geo-component-type.
 *
 * Created by k3b on 25.03.2015.
 */
public class GeoFormatter {
    /* converter for Datatypes */
    private static final DecimalFormat latLonFormatter = new DecimalFormat("#.#######", new DecimalFormatSymbols(Locale.ENGLISH));
    private static final DateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /** parsing helper: converts a double value from string to double */
    public static double parseLatOrLon(String val) throws ParseException {
        if ((val == null) || (val.length() == 0)) return IGeoPointInfo.NO_LAT_LON;
        return latLonFormatter.parse(val).doubleValue();
    }

    public static String formatLatLon(double latitude) {
        if (latitude != IGeoPointInfo.NO_LAT_LON) {
            return latLonFormatter.format(latitude);
        }
        return "";
    }

    public static String formatDate(Date date) {
        if (date != null) {
            return timeFormatter.format(date);
        }
        return "";
    }

    public static String formatZoom(int val) {
        if (val != IGeoPointInfo.NO_ZOOM) {
            return Integer.toString(val);
        }
        return "";
    }

    /** parsing helper: converts value into zoom compatible int */
    public static int parseZoom(String value) {
        if (value != null) {
            try {
                int result = Integer.parseInt(value);
                if ((result >= 0) && (result < 64)) {
                    return result;
                }
            } catch (Exception ignore) {
            }
        }
        return IGeoPointInfo.NO_ZOOM;
    }
}
