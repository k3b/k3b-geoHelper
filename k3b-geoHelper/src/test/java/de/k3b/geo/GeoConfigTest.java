/*
 * Copyright (c) 2021 by k3b.
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

package de.k3b.geo;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class GeoConfigTest {
    @Test
    public void isOneOf() {
        assertTrue(GeoConfig.isOneOf("Hello.Kml.ziP", GeoConfig.EXT_ALL_KML));
        assertTrue(GeoConfig.isOneOf("Hello.Kml.ziP", GeoConfig.EXT_ALL_ZIP));
        assertFalse(GeoConfig.isOneOf("Hello.Kml.ziP", GeoConfig.EXT_ALL_GPX));
    }

}