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

import java.util.List;

/**
 * Abstract Repository to load/save List<{@link de.k3b.geo.api.IGeoPointInfo} > .
 *
 * It can be used for {@link de.k3b.geo.api.GeoPointDto} or
 * any custom {@link de.k3b.geo.api.IGeoPointInfo} implementation.
 *
 * Created by k3b on 17.03.2015.
 */
public interface IGeoRepository<R extends IGeoPointInfo> {

    /** Cached: Load from repository.
     *
     * @return data loaded
     */
    List<R> load();

    /** Uncached: Fresh load from repository.
     *
     * @return data loaded
     */
    List<R> reload();

    /** Save back in memory-data back to repository
     *
     * @return false: error.
     */
    boolean save();

    /** Generate a new id for {@link IGeoPointInfo#getId()}. */
    String createId();

    /** Removes item from repository.
     *
     * @return true if successful */
    boolean delete(R item);
}
