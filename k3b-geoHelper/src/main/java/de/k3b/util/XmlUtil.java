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

public class XmlUtil {
    private XmlUtil(){}

    public static String escapeXMLElement(String input) {
        if (input == null) {
            return null;
        }

        return input
                .replace("&","&amp;")
                .replace("<", "&lt;")
                .replace( ">","&gt;");
    }
    /** q&d: replace chars that are illegal for xml attributes */
    public static String escapeXmlAttribute(String value) {
        if (value != null) {
            return XmlUtil.escapeXMLElement(value)
                    .replace('\n', ' ')
                    .replace('\r', ' ')
                    .replace("\"", "&quot;")
                    .replace("\'","&apos;").replace("  ", " ");
        }
        return null;
    }

}
