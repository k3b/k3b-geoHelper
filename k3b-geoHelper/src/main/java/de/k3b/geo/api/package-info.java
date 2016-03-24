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

/**
 * This Package defines Android independant api as interfaces to handle geo infos.
 *
 * <ul>
 *     <li>{@link de.k3b.geo.api.GeoPointDto}:
 *          a location or trackpoint that can be represented in a gpx file.</li>
 *     <li>{@link de.k3b.geo.io.gpx.GpxFormatter}:
 *          Formats {@link de.k3b.geo.api.GeoPointDto}-s or {@link de.k3b.geo.api.ILocation}-s as geo-xml.</li>
 *     <li>{@link de.k3b.geo.io.gpx.GpxReader}:
 *          reads {@link de.k3b.geo.api.GeoPointDto} from file or stream.</li>
 * </ul>
 *
 **/
package de.k3b.geo.api;
