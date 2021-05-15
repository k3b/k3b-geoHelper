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

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadSymbolsToZipService extends DownloadSymbolsBaseService {
    private ZipOutputStream zipOutputStream = null;
    private String imageDirName = "";

    public DownloadSymbolsToZipService(String userAgent) {
        super(userAgent);
    }

    public DownloadSymbolsToZipService zipOutputStream(ZipOutputStream zipOutputStream, String imageDirName) {
        this.zipOutputStream = zipOutputStream;

        if (imageDirName != null) {
            this.imageDirName = imageDirName + "/";
        } else {
            this.imageDirName = "";
        }
        return this;
    }

    @Override
    protected String createSymbolUri(String iconName) {
        return this.imageDirName + iconName;
    }

    @Override
    protected OutputStream createOutputStream(String iconName) throws IOException {
        String renamedFile = imageDirName + iconName;
        String comment = "from " + iconName;
        long lastModified = (lastConnection != null) ? lastConnection.getLastModified() : 0;

        return createOutputStream(renamedFile, comment, lastModified);
    }

    public OutputStream createOutputStream(String renamedFile, String comment, long lastModified) throws IOException {
        ZipEntry zipEntry = new ZipEntry(renamedFile);
        if (comment != null) zipEntry.setComment(comment);

        if (lastModified != 0) zipEntry.setTime(lastModified);

        zipOutputStream.putNextEntry(zipEntry);

        return this.zipOutputStream;
    }

    @Override
    protected void closeSymbolOutputStream(OutputStream outputStream) throws IOException {
        // do nothing. Close ZipEntryOutstream is Handled by ZipOutputStream
    }

}
