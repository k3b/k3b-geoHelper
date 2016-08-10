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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.util.IsoDateTimeParser;

/**
 * Converts between a {@link IGeoPointInfo} and a uri {@link String}.
 *
 * ---
 *
 * ![GeoUri-fromUri](GeoUri-fromUri.png)
 *
 * ```java
 * GeoUri parser = new GeoUri(GeoUri.OPT_DEFAULT);
 *
 * IGeoPointInfo geo = parser.fromUri("geo:52.1,9.2?z=14");
 *
 * System.out.print(String.format("got lat=%f lon=%f", geo.getLatitude(),geo.getLongitude()));
 * ```
 *
 * ---
 *
 * ![GeoUri-toUriString](GeoUri-toUriString.png)
 *
 * ```java
 * GeoUri formater = new GeoUri(GeoUri.OPT_DEFAULT);
 * GeoPointDto geo = new GeoPointDto()
 *      .setLatitude(52.1)
 *      .setLongitude(9.2)
 *      .setZoomMin(14);
 * String geoUri = formater.toUriString(geo);
 * ```
 *
 * ---
 *
 * Format:
 *
 * * geo:{lat}{,lon{,hight_ignore}}}{?q={lat}{,lon}{,hight_ignore}{(name)}}{&uri=uri}{&id=id}{&d=description}{&z=zmin{&z2=zmax}}{&t=timeOfMeasurement}
 *
 * Example (with {@link de.k3b.geo.io.GeoUri#OPT_FORMAT_REDUNDANT_LAT_LON} set):
 *
 * * geo:52.1,9.2?q=52.1,9.2(name)&z=5&z2=7&uri=uri&d=description&id=id&t=1991-03-03T04:05:06Z
 *
 * This should be compatible with standard http://tools.ietf.org/html/draft-mayrhofer-geo-uri-00
 * and with googlemap for android.
 *
 * This implementation has aditional non-standard parameters for LocationViewer clients.
 * 
 * For details see [supported geo uri formats](https://github.com/k3b/k3b-geoHelper/wiki/data#geo)
 *
 *
 * Created by k3b on 13.01.2015.
 */
public class GeoUri {
    /* constants that define behaviour of fromUri and toUri */

    /** Option for {@link GeoUri#GeoUri(int)}: */
    public static final int OPT_DEFAULT = 0;

    /** Option for {@link GeoUri#GeoUri(int)} to influence {@link #toUriString(IGeoPointInfo)}: Add lat/long twice.
     *
     * Example with opton set (and understood by google):
     *
     * * geo:52.1,9.2?q=52.1,9.2
     *
     * Example with opton not set (and not understood by google):
     *
     * * geo:52.1,9.2
     *
     */
    public static final int OPT_FORMAT_REDUNDANT_LAT_LON = 1;

    /** Option for {@link GeoUri#GeoUri(int)} for {@link #fromUri(String)} :
     * If set try to get {@link IGeoPointInfo#getTimeOfMeasurement()},
     * {@link IGeoPointInfo#getLatitude()}, {@link IGeoPointInfo#getLongitude()},
     * {@link IGeoPointInfo#getName()} from other fields.
     *
     * Example:
     *
     * * "geo:?d=I was in (Hamburg) located at 53,10 on 1991-03-03T04:05:06Z"
     *
     * would set {@link IGeoPointInfo#getTimeOfMeasurement()},
     * {@link IGeoPointInfo#getLatitude()}, {@link IGeoPointInfo#getLongitude()},
     * {@link IGeoPointInfo#getName()} from {@link IGeoPointInfo#getDescription()} .
     */
    public static final int OPT_PARSE_INFER_MISSING = 0x100;

    /**
     * Default for url-encoding.
     */
    private static final String DEFAULT_ENCODING = "UTF-8";
    public static final String GEO_SCHEME = "geo:";
    public static final String AREA_SCHEME = "geoarea:";
    public static final java.lang.String HTTPS_SCHEME = "https:";
    public static final java.lang.String HTTP_SCHEME = "http:";

    /* Regular expressions used by the parser.<br/>
       '(?:"+something+")"' is a non capturing group; "\s" white space */
    private final static String regexpName = "(?:\\s*\\(([^\\(\\)]+)\\))"; // i.e. " (hallo world)"
    private final static Pattern patternName = Pattern.compile(regexpName);
    private final static String regexpDouble = "([+\\-" + GeoFormatter.LatLonPrefix +
            "]?[0-9\\.]+)"; // i.e. "-123.456" or "S123.456"
    private final static String regexpDoubleOptional = regexpDouble + "?";
    private final static String regexpCommaDouble = "(?:\\s*,\\s*" + regexpDouble + ")"; // i.e. " , +123.456"
    private final static String regexpCommaDoubleOptional = regexpCommaDouble + "?";
    private final static String regexpLatLonAlt = regexpDouble + regexpCommaDouble + regexpCommaDoubleOptional;
    private final static String regexpLatLonLatLon = regexpDouble + regexpCommaDouble + regexpCommaDouble + regexpCommaDouble;
    private final static Pattern patternLatLonAlt = Pattern.compile(regexpLatLonAlt);
    private final static Pattern patternLatLonLatLon = Pattern.compile(regexpLatLonLatLon);
    private final static Pattern patternTime = Pattern.compile("([12]\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ)");

    private final static String regexpHref = "(?:\\s*href\\s?\\=\\s?['\"]([^'\"]*)['\"])"; // i.e. href='hallo'
    private final static Pattern patternHref = Pattern.compile(regexpHref);

    private final static String regexpSrc = "(?:\\s*src\\s?\\=\\s?['\"]([^'\"]*)['\"])"; // i.e. src='hallo'
    private final static Pattern patternSrc = Pattern.compile(regexpSrc);

    /* Current state */

    /** Formating/parsing options */
    private final int options;

    /** For uri-formatter: Next delimiter for a parameter. can be "?" or "&"  */
    private String delim;

    /** Create with options from OPT_xxx */
    public GeoUri(int options) {
        this.options = options;
    }

    /**
     * Load {@link IGeoPointInfo} from uri-{@link String}
     *
     * ![GeoUri-fromUri](GeoUri-fromUri.png)
     *
	 * For details see [supported geo uri formats](https://github.com/k3b/k3b-geoHelper/wiki/data#geo)
	 *
     * @startuml GeoUri-fromUri.png
     * title Convert uri string to geo-point 
     * interface IGeoPointInfo
     *
     * class GeoUri
     * GeoUri : fromUri
     *
     * GeoUri -> IGeoPointInfo
     * String -> GeoUri : "geo:52.1,9.2?..."
     * String -> GeoUri : "http://maps.google..."
     * @enduml
     *
     * 
     */
    public IGeoPointInfo fromUri(String uri) {
        return fromUri(uri, new GeoPointDto());
    }

    /** Load {@link IGeoPointInfo} from uri-{@link String} into parseResult. 
     *
     * ![GeoUri-fromUri](GeoUri-fromUri.png)
	 *
	 * For details see [supported geo uri formats](https://github.com/k3b/k3b-geoHelper/wiki/data#geo)
	 */
    public <TGeo extends GeoPointDto>  TGeo fromUri(String uri, TGeo parseResult) {
        if (uri == null) return null;

        if (uri.startsWith(HTTP_SCHEME) || uri.startsWith(HTTPS_SCHEME)) {
            String uriLowercase = uri.toLowerCase();
            if (uriLowercase.indexOf("yandex.") >= 0) return getYandexUri(uri, parseResult);
            if (uriLowercase.indexOf("openstreetmap.") >= 0) return getOpenstreetmapUri(uri, parseResult);
            if (uriLowercase.indexOf(".here.") >= 0) return getHereUri(uri, parseResult);
            if (uriLowercase.indexOf(".google.") >= 0) return getGoogleUri(uri, parseResult);

            // unknown. try default
            return uriParamParse(uri, parseResult);

        }
        if (uri.startsWith(HTTP_SCHEME) || uri.startsWith(HTTPS_SCHEME) || uri.startsWith(GEO_SCHEME)) {
            return uriParamParse(uri, parseResult);
        }

        // unknown format
        return null;
    }

    private <TGeo extends GeoPointDto> TGeo getYandexUri(String uri, TGeo parseResult) {
        // https://www.yandex.com/maps/?ll=9.2,52.1&z=14
        String search = "map=";

        // special ll= handling lat / lon are spwapped
        int dataStart = contentIndexBehind(uri, "ll=");
        String[] parts = getParts(uri, dataStart, "[,?&]", 2);
        if (parts != null) {
            setLatLonZoom(parseResult, parts[1], parts[0], null);
        }
        return uriParamParse(uri, parseResult);
    }

    private static void setLatLonZoom(GeoPointDto parseResult, String latString, String lonString, String zoom) {
        if ((parseResult.getZoomMin() == GeoPointDto.NO_ZOOM) && (zoom != null)) {
            parseResult.setZoomMin(GeoFormatter.parseZoom(zoom));
        }

        try {
            // !!! isNaN does not work
            if ((latString != null) && GeoPointDto.isEmpty(parseResult.getLatitude())) parseResult.setLatitude(GeoFormatter.parseLatOrLon(latString));
            if ((lonString != null) && GeoPointDto.isEmpty(parseResult.getLongitude()))  parseResult.setLongitude(GeoFormatter.parseLatOrLon(lonString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private <TGeo extends GeoPointDto> TGeo getOpenstreetmapUri(String uri, TGeo parseResult) {
        // https://www.openstreetmap.org/?#map=14/52.1/9.2">
        // https://www.openstreetmap.org/#map=14/52.1/9.2">
        // https://www.openstreetmap.org/#14/52.1/9.2">
        int dataStart = contentIndexBehind(uri, "#map=");
        if (dataStart < 0) dataStart = contentIndexBehind(uri, "/#");
        String[] parts = getParts(uri, dataStart, "[/?&]", 3);
        if (parts != null) {
            setLatLonZoom(parseResult, parts[1], parts[2], parts[0]);
        }
        return uriParamParse(uri, parseResult);
    }

    private String[] getParts(String stringToParse, int dataStart, String delimiter, int minPartCount) {
        if (dataStart >= 1) {
            String[] parts = stringToParse.substring(dataStart).split(delimiter);
            if ((parts != null) && (parts.length >= minPartCount)) return parts;
        }
        return null;
    }

    private <TGeo extends GeoPointDto> TGeo getHereUri(String uri, TGeo parseResult) {
        // https://wego.here.com/?map=52.1,9.2,14
        // https://share.here.com/52.1,9.2,14
        int dataStart = contentIndexBehind(uri, "map=");
        if (dataStart < 0) dataStart=uri.lastIndexOf("/") + 1;
        String[] parts = getParts(uri, dataStart, "[,&?]", 2);
        if (parts != null) {
            String zoom = (parts.length <= 2) ? null : parts[2];
            setLatLonZoom(parseResult, parts[0], parts[1], zoom);
        }
        return uriParamParse(uri, parseResult);
    }

    private int contentIndexBehind(String uri, String search) {
        int result = uri.indexOf(search);
        if (result >= 0) return result + search.length();
        return result;
    }

    private <TGeo extends GeoPointDto> TGeo getGoogleUri(String uri, TGeo parseResult) {
        uri = uri.replaceAll("q=loc:", "q=");

        // https://www.google.com/maps/@52.1,9.2,14z"
        int dataStart = contentIndexBehind(uri, "/@");
        String[] parts = getParts(uri, dataStart, "[,?&(]", 2);
        if (parts != null) {
            String zoom = (parts.length <= 2) ? null : parts[2];
            if ((zoom != null) && (zoom.toLowerCase().endsWith("z"))) {
                setLatLonZoom(parseResult, null, null, zoom.substring(0, zoom.length()-1));
                // parseResult.setZoomMin(GeoFormatter.parseZoom(zoom.substring(0, zoom.length()-1)));
            } else {
                zoom = null;
            }
            setLatLonZoom(parseResult, parts[0], parts[1], zoom);
        }
        return uriParamParse(uri, parseResult);
    }

    private <TGeo extends GeoPointDto> TGeo uriParamParse(String uri, TGeo parseResult) {
        int queryOffset = uri.indexOf("?");

        if (queryOffset >= 0) {
            String query = uri.substring(queryOffset + 1);
            uri = uri.substring(0, queryOffset);
            HashMap<String, String> parmLookup = new HashMap<String, String>();
            String[] params = query.split("&");
            for (String param : params) {
                parseAddQueryParamToMap(parmLookup, param);
            }
            parseResult.setDescription(getParam(parmLookup, GeoUriDef.DESCRIPTION, parseResult.getDescription()));
            parseResult.setLink(getParam(parmLookup, GeoUriDef.LINK, parseResult.getLink()));
            parseResult.setSymbol(getParam(parmLookup, GeoUriDef.SYMBOL, parseResult.getSymbol()));
            parseResult.setId(getParam(parmLookup, GeoUriDef.ID, parseResult.getId()));

            if (parseResult.getZoomMin() == GeoPointDto.NO_ZOOM) {
                setLatLonZoom(parseResult, null, null, getParam(parmLookup, GeoUriDef.ZOOM, null));
            }
            if (parseResult.getZoomMax() == GeoPointDto.NO_ZOOM) {
                parseResult.setZoomMax(GeoFormatter.parseZoom(getParam(parmLookup, GeoUriDef.ZOOM_MAX, null)));
            }

            // parameters from standard value and/or infered
            List<String> whereToSearch = new ArrayList<String>();
            whereToSearch.add(getParam(parmLookup, GeoUriDef.QUERY, null)); // lat lon from q have precedence over url-path
            whereToSearch.add(uri);
            whereToSearch.add(getParam(parmLookup, GeoUriDef.LAT_LON, null));

            final boolean inferMissing = isSet(GeoUri.OPT_PARSE_INFER_MISSING);
            if (inferMissing) {
                whereToSearch.add(parseResult.getDescription());
                whereToSearch.addAll(parmLookup.values());
            }

            parseResult.setName(parseFindFromPattern(patternName, parseResult.getName(), whereToSearch));
            parseResult.setTimeOfMeasurement(parseTimeFromPattern(parseResult.getTimeOfMeasurement(), getParam(parmLookup, GeoUriDef.TIME, null), whereToSearch));

            parseLatOrLon(parseResult, whereToSearch);

            if (parseResult.getName() == null) {
                parseResult.setName(getParam(parmLookup, GeoUriDef.NAME, null));
            }
            if (inferMissing) {
                parseResult.setLink(parseFindFromPattern(patternHref, parseResult.getLink(), whereToSearch));
                parseResult.setSymbol(parseFindFromPattern(patternSrc, parseResult.getSymbol(), whereToSearch));
            }
        } else {
            // no query parameter
            List<String> whereToSearch = new ArrayList<String>();
            whereToSearch.add(uri);
            parseLatOrLon(parseResult, whereToSearch);
        }
        return parseResult;
    }

    private String getParam(HashMap<String, String> parmLookup, String paramId, String currentValue) {
        if ((currentValue == null) || (currentValue.length() == 0)) {
            return parmLookup.get(paramId);
        }
        return currentValue;
    }

    /** Load {@link GeoPointDto}[2] from area-uri-{@link String} into parseResult. */
    public <TGeo extends GeoPointDto>  TGeo[] fromUri(String uri, TGeo[] parseResult) {
        if ((uri == null) || (parseResult == null) || (parseResult.length < 2)) return null;
        if (!uri.startsWith(AREA_SCHEME)) return null;

        Matcher m = parseFindWithPattern(patternLatLonLatLon, uri);

        if (m != null) {
            int nextCoord = 1;
            try {
                parseResult[0].setLatitude(GeoFormatter.parseLatOrLon(m.group(nextCoord++))).setLongitude(GeoFormatter.parseLatOrLon(m.group(nextCoord++)));
                parseResult[1].setLatitude(GeoFormatter.parseLatOrLon(m.group(nextCoord++))).setLongitude(GeoFormatter.parseLatOrLon(m.group(nextCoord++)));
                return parseResult;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** Infer name,time,link,symbol from textToBeAnalysed if the fields are not already filled. */
    public static GeoPointDto inferMissing(GeoPointDto parseResult, String textToBeAnalysed) {

        if (textToBeAnalysed != null) {
            List<String> whereToSearch = new ArrayList<String>();
            whereToSearch.add(textToBeAnalysed);

            parseResult.setName(parseFindFromPattern(patternName, parseResult.getName(), whereToSearch));
            parseResult.setTimeOfMeasurement(parseTimeFromPattern(parseResult.getTimeOfMeasurement(), null, whereToSearch));
            parseResult.setLink(parseFindFromPattern(patternHref, parseResult.getLink(), whereToSearch));
            parseResult.setSymbol(parseFindFromPattern(patternSrc, parseResult.getSymbol(), whereToSearch));
        }
        return parseResult;
    }

    /** Parsing helper: Convert array to list */
    private static List<String> toStringArray(String... whereToSearch) {
        return Arrays.asList(whereToSearch);
    }

    /** Parsing helper: Set first finding of lat and lon to parseResult */
    public static void parseLatOrLon(GeoPointDto parseResult, String... whereToSearch) {
        parseLatOrLon(parseResult, toStringArray(whereToSearch));
    }

    /** Parsing helper: Set first finding of lat and lon to parseResult */
    private static void parseLatOrLon(GeoPointDto parseResult, List<String> whereToSearch) {
        Matcher m = parseFindWithPattern(patternLatLonAlt, whereToSearch);

        if (m != null) {
           setLatLonZoom(parseResult, m.group(1), m.group(2), null);
        }
    }

    /** Parsing helper: Get the first finding of pattern in whereToSearch if currentValue is not set yet.
     * Returns currentValue or content of first matching group of pattern. */
    private static String parseFindFromPattern(Pattern pattern, String currentValue, List<String> whereToSearch) {
        if ((currentValue == null) || (currentValue.length() == 0)) {
            Matcher m = parseFindWithPattern(pattern, whereToSearch);
            String found = (m != null) ? m.group(1) : null;
            if (found != null) {
                return found;
            }
        }
        return currentValue;
    }

    /** Parsing helper: Get the first datetime finding in whereToSearch if currentValue is not set yet.
     * Returns currentValue or finding as Date . */
    private static Date parseTimeFromPattern(Date currentValue, String stringValue, List<String> whereToSearch) {
        String match = parseFindFromPattern(IsoDateTimeParser.ISO8601_FRACTIONAL_PATTERN, stringValue, whereToSearch);

        if (match != null) {
            return IsoDateTimeParser.parse(match);
        }
        return currentValue;
    }

    /** Parsing helper: Returns the match of the first finding of pattern in whereToSearch. */
    private static Matcher parseFindWithPattern(Pattern pattern, List<String> whereToSearch) {
        if (whereToSearch != null) {
            for (String candidate : whereToSearch) {
                Matcher m = parseFindWithPattern(pattern, candidate);
                if (m != null) return m;
            }
        }
        return null;
    }

    private static Matcher parseFindWithPattern(Pattern pattern, String candidate) {
        if (candidate != null) {
            Matcher m = pattern.matcher(candidate);
            while (m.find() && (m.groupCount() > 0)) {
                return m;
            }
        }
        return null;
    }

    /** Parsing helper: Add a found query-parameter to a map for fast lookup */
    private void parseAddQueryParamToMap(HashMap<String, String> parmLookup, String param) {
        if (param != null) {
            String[] keyValue = param.split("=");
            if ((keyValue != null) && (keyValue.length == 2)) {
                try {
                    parmLookup.put(keyValue[0], URLDecoder.decode(keyValue[1], DEFAULT_ENCODING));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Converts a {@link IGeoPointInfo} into uri {@link String} representatino.<br/>
     * <br/>
     * Format
     *
     * geo:{lat{,lon{,hight_ignore}}}{?q={lat}{,lon}{,hight_ignore}{(name)}}{&uri=uri}{&id=id}{&d=description}{&z=zmin{&z2=zmax}}{&t=timeOfMeasurement}
     *
     * ![GeoUri-toUriString](GeoUri-toUriString.png)
     *
	 * For details see [supported geo uri formats](https://github.com/k3b/k3b-geoHelper/wiki/data#geo)
     *
     * @startuml GeoUri-toUriString.png
     * title Convert geo-point to uri string
     * interface IGeoPointInfo
     * class GeoPointDto
     *
     * class GeoUri
     * GeoUri : toUriString
     * IGeoPointInfo <|-- GeoPointDto
     *
     * IGeoPointInfo -> GeoUri
     * GeoUri -> String : "geo:52.1,9.2?..."
     * @enduml
     *
     */
    public String toUriString(IGeoPointInfo geoPoint) {
        StringBuffer result = new StringBuffer();
        result.append(GEO_SCHEME);
        formatLatLon(result, geoPoint);

        delim = "?";
        appendQueryParameter(result, GeoUriDef.QUERY, formatQuery(geoPoint), false);
        appendQueryParameter(result, GeoUriDef.ZOOM, GeoFormatter.formatZoom(geoPoint.getZoomMin()), false);
        appendQueryParameter(result, GeoUriDef.ZOOM_MAX, GeoFormatter.formatZoom(geoPoint.getZoomMax()), false);
        appendQueryParameter(result, GeoUriDef.LINK, geoPoint.getLink(), true);
        appendQueryParameter(result, GeoUriDef.SYMBOL, geoPoint.getSymbol(), true);
        appendQueryParameter(result, GeoUriDef.DESCRIPTION, geoPoint.getDescription(), true);
        appendQueryParameter(result, GeoUriDef.ID, geoPoint.getId(), true);
        if (geoPoint.getTimeOfMeasurement() != null) {
            appendQueryParameter(result, GeoUriDef.TIME, GeoFormatter.formatDate(geoPoint.getTimeOfMeasurement()), false);
        }

        return result.toString();
    }


    /** Creates area-uri-{@link String} from two bounding {@link IGeoPointInfo}-s  */
    public String toUriString(IGeoPointInfo northEast, IGeoPointInfo southWest) {
        StringBuffer result = new StringBuffer();
        result.append(AREA_SCHEME);
        result.append(GeoFormatter.formatLatLon(northEast.getLatitude())).append(",");
        result.append(GeoFormatter.formatLatLon(northEast.getLongitude())).append(",");
        result.append(GeoFormatter.formatLatLon(southWest.getLatitude())).append(",");
        result.append(GeoFormatter.formatLatLon(southWest.getLongitude()));

        return result.toString();
    }

    /** Formatting helper: Adds name value to result with optional encoding. */
    private void appendQueryParameter(StringBuffer result, String paramName, String paramValue, boolean urlEncode) {
        if ((paramValue != null) && (paramValue.length() > 0)) {
            try {
                result.append(delim).append(paramName).append("=");
                if (urlEncode) {
                    paramValue = encode(paramValue);
                }
                result.append(paramValue);
                delim = "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /** Formatting helper: Adds lat/lon to result. */
    private void formatLatLon(StringBuffer result, IGeoPointInfo geoPoint) {
        if (geoPoint != null) {
            result.append(GeoFormatter.formatLatLon(geoPoint.getLatitude()));

            if (geoPoint.getLongitude() != IGeoPointInfo.NO_LAT_LON) {
                result
                        .append(",")
                        .append(GeoFormatter.formatLatLon(geoPoint.getLongitude()));
            }
        }
    }

    /** Formatting helper: Adds {@link IGeoPointInfo} fields to result. */
    private String formatQuery(IGeoPointInfo geoPoint) {
        // {lat{,lon{,hight_ignore}}}{(name)}{|uri{|id}|}{description}
        StringBuffer result = new StringBuffer();

        if (isSet(OPT_FORMAT_REDUNDANT_LAT_LON)) {
            formatLatLon(result, geoPoint);
        }

        if (geoPoint.getName() != null) {
            try {
                result.append("(").append(encode(geoPoint.getName())).append(")");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (result.length() == 0) return null;

        return result.toString();
    }

    /** Formatting helper: Executes url-encoding. */
    private String encode(String raw) throws UnsupportedEncodingException {
        return URLEncoder.encode(raw, DEFAULT_ENCODING);
    }

    /** Return true, if opt is set */
    private boolean isSet(int opt) {
        return ((options & opt) != 0);
    }
}
