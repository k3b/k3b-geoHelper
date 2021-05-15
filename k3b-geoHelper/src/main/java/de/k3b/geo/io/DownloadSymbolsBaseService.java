package de.k3b.geo.io;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    public DownloadSymbolsBaseService(String userAgent) {
        this.userAgent = userAgent;
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

    public void downloadSymbols(List<IGeoPointInfo> points) throws IOException {
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
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
        }
    }

    public InputStream getInputStream(String urlString) throws IOException {
        return getInputStream(new URL(urlString));
    }

    public InputStream getInputStream(URL url) throws IOException {
        URLConnection hc = url.openConnection();

        // see https://meta.wikimedia.org/wiki/Special:MyLanguage/User-Agent_policy
        hc.setRequestProperty("User-Agent",userAgent);

        return hc.getInputStream();
    }

    protected abstract String createSymbolUri(String iconName);

    protected abstract OutputStream createOutputStream(String iconName) throws FileNotFoundException;
}
