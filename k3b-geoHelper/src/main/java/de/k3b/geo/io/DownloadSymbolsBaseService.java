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
package de.k3b.geo.io;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoConverter;
import de.k3b.geo.api.IGeoPointInfo;

public abstract class DownloadSymbolsBaseService implements IGeoInfoConverter<IGeoPointInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadSymbolsBaseService.class);

    protected final String userAgent;
    protected URLConnection lastConnection = null;
    protected ITranslateSymbolUri translateSymbolUri = null;

    /**
     * @param userAgent a string identifying the calling app.
     *                  i.e. "MyHelloWikipediaApp/1.0 (https://github.com/MyName/MyHelloWikipediaApp)"
     *                  see https://meta.wikimedia.org/wiki/Special:MyLanguage/User-Agent_policy
     */
    public DownloadSymbolsBaseService(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * @param translateSymbolUri Under Android you can use this to translate File-Uris to Android-Content-uris
     */
    public DownloadSymbolsBaseService translateSymbolUri(ITranslateSymbolUri translateSymbolUri) {
        this.translateSymbolUri = translateSymbolUri;
        return this;
    }

    @Override
    public List<IGeoPointInfo> convert(List<IGeoPointInfo> points) {
        if (points != null) {
            try {
                downloadSymbols(points);
            } catch (IOException e) {
                LOGGER.error("Cannot Save Sybols:" + e.getMessage(), e);
                return null;
            }
        }
        return points;
    }

    protected void downloadSymbols(List<IGeoPointInfo> points) throws IOException {
        for (IGeoPointInfo geo : points) {
            String icon = geo.getSymbol();
            if (icon != null && icon.contains(".") && icon.toLowerCase().startsWith("http")) {
                ((GeoPointDto) geo).setSymbol(saveIcon(icon));
            }
        }
    }

    protected String saveIcon(String icon) throws IOException {
        String iconName = FilenameUtils.getName(icon);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getInputStream(icon);
            outputStream = createOutputStream(iconName);
            byte[] buffer = new byte[1024];
            for (int read = inputStream.read(buffer); read > -1; read = inputStream
                    .read(buffer)) {
                outputStream.write(buffer, 0, read);
            }
            return createSymbolUri(iconName);
        } catch (Exception e) {
            return icon;
        } finally {
            if (outputStream != null) closeSymbolOutputStream(outputStream);
            if (inputStream != null) inputStream.close();
        }
    }

    protected void closeSymbolOutputStream(OutputStream outputStream) throws IOException {
        outputStream.close();
    }

    public InputStream getInputStream(String urlString) throws IOException {
        return getInputStream(new URL(urlString));
    }

    public InputStream getInputStream(URL url) throws IOException {
        lastConnection = url.openConnection();

        // see https://meta.wikimedia.org/wiki/Special:MyLanguage/User-Agent_policy
        lastConnection.setRequestProperty("User-Agent",userAgent);

        return lastConnection.getInputStream();
    }

    protected String createSymbolUri(String iconName) {
        if (translateSymbolUri != null) {
            return translateSymbolUri.translate(iconName);
        }
        return iconName;
    }

    protected abstract OutputStream createOutputStream(String iconName) throws IOException;

    public interface ITranslateSymbolUri {
        String translate(String symbolUri);
    }
}
