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

/** Implementation Detail: Define the string constants that make up geo-uri-parameter.
 *
 * They are part of the geo-uri schema geo:lat,lon?q=.... and the poi xml format */
public class GeoUriDef {

    public static final String DESCRIPTION = "d";
    public static final String LINK = "link";
    public static final String SYMBOL = "s";
    public static final String QUERY = "q";
    public static final String ZOOM = "z";
    public static final String ZOOM_MAX = "z2";
    public static final String ID = "id";
    public static final String TIME = "t";

    // n=name is an alternative to geo:...q=(name)
    public static final String NAME = "n";
    // ll=lat,lon is an alternative to geo:...q=lat,lon
    public static final String LAT_LON = "ll";

    // xml-only
    public static final String XML_ELEMENT_POI = "poi";
    public static final String XML_ATTR_GEO_URI = "geoUri";

    // "true" of "1" means infer missing parameters
    public static final String XML_ATTR_GEO_URI_INFER_MISSING = "infer";
}
