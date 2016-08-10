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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoRepository;

/**
 * Created by k3b on 13.01.2015.
 */
public class GeoFileRepositoryTests {
    // Obtain a logger instance
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoFileRepositoryTests.class);
    private static final File OUTDIR = new File("./build/testresults/GeoFileRepositoryTests");
    private File repositoryFile = null;

    @BeforeClass
    public static void initDirectories() {
        OUTDIR.mkdirs();
    }

    private GeoPointDto createItem(int id) {
        GeoPointDto result = new GeoPointDto()
                .setLatitude(0.1 + id)
                .setLongitude(0.2 + id)
                .setId("Id" + id)
                .setName("Name" + id)
                .setDescription("Description" + id)
                ;
        return result;
    }

    private IGeoRepository<GeoPointDto> createUnsavedRepo(String name, int numberOfItems) {
        this.repositoryFile = new File(OUTDIR, name + "-repo.txt");
        repositoryFile.delete();

        GeoFileRepository<GeoPointDto> result = new GeoFileRepository<GeoPointDto>(this.repositoryFile);

        List<GeoPointDto> items = result.load();
        for (int i=1; i <= numberOfItems; i++) {
            items.add(createItem(i));
        }
        return result;
    }

    /* load() reload() createId() delete(T item)  save() */
    @Test
    public void shouldLoadNonExistentIsEmpty() throws Exception {
        List<GeoPointDto> items = createUnsavedRepo("shouldLoadNonExistentIsEmpty", 0).load();

        Assert.assertEquals(0, items.size());
    }

    @Test
    public void shouldSaveLoad() throws Exception {
        createUnsavedRepo("shouldSaveLoad", 3).save();

        List<GeoPointDto> items = new GeoFileRepository<GeoPointDto>(this.repositoryFile).load();

        Assert.assertEquals(3, items.size());
    }

    @Test
    public void shouldDeleteExistingItem() throws Exception {
        List<GeoPointDto> items = createUnsavedRepo("shouldDeleteExistingItem", 3)
                .save()
                .delete(createItem(2))
                .reload();
        Assert.assertEquals(2, items.size());
    }

    @Test
    public void shouldNotDeleteNonExistingItem() throws Exception {
        List<GeoPointDto> items = createUnsavedRepo("shouldNotDeleteNonExistingItem", 3)
                .save()
                .delete(createItem(7))
                .reload();
        Assert.assertEquals(3, items.size());
    }

}
