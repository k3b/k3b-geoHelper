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

package de.k3b.geo.api;

import java.util.Date;

/**
 * Represents a geographic location: latitude(in degrees north), longitude(in degrees east)
 * Interface to make the lib independant from Android and other location sources.<br/>
 *  <br/>
 * Created by k3b on 11.05.2014.
 */
public interface ILocation {
    /** Get the latitude, in degrees north. */
    double getLatitude();
    /** Get the longitude, in degrees east. */
    double getLongitude();
    /** Get the date when the measurement was taken. Null if unknown. */
    Date getTimeOfMeasurement();
}
