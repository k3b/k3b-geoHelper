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

package de.k3b.geo.api;

import java.util.List;

/**
 * A class that consumes or transforms Lists of {@link de.k3b.geo.api.IGeoPointInfo}.
 *
 * Created by EVE on 15.05.2021.
 */
public interface IGeoInfoConverter<R extends IGeoPointInfo> {
    List<R> convert(List<R> geoInfo);
}
