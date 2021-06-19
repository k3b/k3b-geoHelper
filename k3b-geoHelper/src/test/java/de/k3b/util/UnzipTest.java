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

package de.k3b.util;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.k3b.geo.io.GeoFileRepositoryTests;

import static org.junit.Assert.*;

public class UnzipTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoFileRepositoryTests.class);
    private static final File OUTDIR = new File("./build/testresults/UnzipTests");
    private static final String ZIP_EXAMPLE = "/de/k3b/geo/util/testDir.zip";
    private static final String NON_ZIP_EXAMPLE = "/de/k3b/geo/io/regressionTests/wikimedia.poi";

    @BeforeClass
    public static void initDirectories() {
        Unzip.deleteRecursive(OUTDIR);
        OUTDIR.mkdirs();
        Assert.assertEquals(0,OUTDIR.listFiles().length);
    }

    @Test
    public void unzip() throws IOException {
        InputStream in = this.getClass().getResourceAsStream(ZIP_EXAMPLE);
        Unzip.unzip(ZIP_EXAMPLE, in, OUTDIR);
        assertTrue(new File(OUTDIR,"testDir/t2.txt").exists());
    }

    @Test
    public void isZipStream_true()  {
        InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(ZIP_EXAMPLE));
        Unzip.isZipStream(in);
        Unzip.isZipStream(in);
        assertTrue("can be called repeatably while InputStream is not used yet", Unzip.isZipStream(in));
    }
    @Test
    public void isZipStream_false() {
        InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(NON_ZIP_EXAMPLE));
        assertFalse("can be called repeatably while InputStream is not used yet", Unzip.isZipStream(in));
    }
}