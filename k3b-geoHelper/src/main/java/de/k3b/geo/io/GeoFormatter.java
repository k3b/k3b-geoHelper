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

/**
 * Converts between uri-{@link String} and geo-component-type {@link double}, {@link Date},
 *    {@link int}.
 *
 * Created by k3b on 25.03.2015.
 */
public class GeoFormatter {
    /* Converter for Datatypes */
    private static final DecimalFormat latLonFormatter
            = new DecimalFormat("#.#######", new DecimalFormatSymbols(Locale.ENGLISH));
    private static final DateFormat timeFormatter
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    public static final String LatLonNegativPrefix = "sSwW";
    public static final String LatLonPrefix = "nNeE" + LatLonNegativPrefix;

    static {
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private GeoFormatter() {}

    /** Parsing helper: Converts a lat or lon value from {@link String} to {@link double}. */
    public static double parseLatOrLon(String oldVal) throws ParseException {
        String newVal = oldVal;
        if ((newVal == null) || (newVal.length() < 1)) return IGeoPointInfo.NO_LAT_LON;
        char latLonPrefix = newVal.charAt(0);
        if (LatLonPrefix.indexOf(latLonPrefix) >= 0) {
            newVal = newVal.substring(1);
        }
        double doubleValue = latLonFormatter.parse(newVal).doubleValue();
        if (LatLonNegativPrefix.indexOf(latLonPrefix) >= 0) doubleValue *= -1;
        return doubleValue;
    }

    /** Parsing helper: Converts  a {@link double} lat or lon value to {@link String}. */
    public static String formatLatLon(double latitude) {
        if (latitude != IGeoPointInfo.NO_LAT_LON) {
            return latLonFormatter.format(latitude);
        }
        return "";
    }

    /** Parsing helper: Converts a {@link Date} value to {@link String}. */
    public static String formatDate(Date date) {
        if (date != null) {
            return timeFormatter.format(date);
        }
        return "";
    }

    /** Parsing helper: Converts a zoom {@link int} value to {@link String}. */
    public static String formatZoom(int val) {
        if (val != IGeoPointInfo.NO_ZOOM) {
            return Integer.toString(val);
        }
        return "";
    }

    /** Parsing helper: Converts zoom {@link String} value to zoom compatible int. */
    public static int parseZoom(String value) {
        if (value != null) {
            try {
                int result = Integer.parseInt(value);
                if ((result >= 0) && (result < 64)) {
                    return result;
                }
            } catch (Exception ignore) {
                // silent fail
            }
        }
        return IGeoPointInfo.NO_ZOOM;
    }
}
