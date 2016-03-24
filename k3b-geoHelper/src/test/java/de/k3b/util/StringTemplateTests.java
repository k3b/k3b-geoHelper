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

package de.k3b.util;

import org.junit.*;

/**
 * Created by k3b on 01.04.2015.
 */
public class StringTemplateTests {
    StringTemplateEngine sut = null;

    @Before
    public void setup() {
        sut = new StringTemplateEngine(new StringTemplateEngine.IValueResolver() {
            @Override
            public String get(String className, String propertyName, String templateParameter) {
                if ((0==className.compareTo("first")) && (0==propertyName.compareTo("name"))) {
                    return "world";
                }
                return "???";
            }
        });
    }

    @Test
    public void shouldFormat() {
        String result = sut.format("hello ${first.name}!");

        Assert.assertEquals("hello world!", result);
    }

    @Test
    public void shouldFormatMultiple() {
        String result = sut.format("hello ${first.name} ${something.else.where}!");

        Assert.assertEquals("hello world ???!", result);
    }

    @Test
    public void shouldNotMatch() {
        String result = sut.format("hello ${first} ${something else}!");

        Assert.assertEquals("hello ${first} ${something else}!", result);
    }
}
