package de.k3b.geo.io.wikipedia;

import org.apache.commons.io.FilenameUtils;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.gpx.GpxReader;

public class DownloadService {
    private static final int THUMBSIZE = 50;

    private final String serviceName;
    private final String userAgent;

    public DownloadService(String serviceName, String userAgent) {
        this.serviceName = serviceName;
        this.userAgent = userAgent;
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

    public String getUrlString(String lat, String lon, int radius, int maxcount) {
        // see https://www.mediawiki.org/wiki/Special:MyLanguage/API:Main_page
        String urlString = "https://" +
                serviceName +
                "/w/api.php" +
                "?action=query" +
                "&format=xml" +
                "&prop=coordinates|info|pageimages|extracts" +
                "&inprop=url" +
                "&piprop=thumbnail" +
                "&generator=geosearch" +
                "&ggscoord=" +
                lat +
                "|" +
                lon +
                "&ggsradius=" +
                radius +
                "&ggslimit=" +
                maxcount +

                "&pithumbsize=" +
                THUMBSIZE +
                "&pilimit=" +
                 maxcount+

                // prop extracts: 2Sentenses in non-html before TOC
                "&exsentences=2&explaintext&exintro";
        return urlString;
    }

    public List<IGeoPointInfo> getGeoPointInfos(String lat, String lon, File file) throws IOException {
        List<IGeoPointInfo> points = getiGeoPointInfos(lat, lon);
        String baseName = FilenameUtils.getBaseName(file.getPath());
        File dir = new File(file.getParentFile(), baseName);
        dir.mkdirs();
        for (IGeoPointInfo geo : points) {
            String icon = geo.getSymbol();
            if (icon != null && icon.contains(".") && icon.toLowerCase().startsWith("http")) {
                ((GeoPointDto) geo).setSymbol(saveIcon(dir, icon, geo.getName()));
            }
        }

        return points;
    }

    public List<IGeoPointInfo> getiGeoPointInfos(String lat, String lon) throws IOException {
        int radius = 10000;
        int maxcount = 5;
        String urlString = this.getUrlString(lat, lon, radius, maxcount);
        InputStream inputStream = this.getInputStream(urlString);
        GpxReader<IGeoPointInfo> parser = new GpxReader<IGeoPointInfo>();
        List<IGeoPointInfo> points = parser.getTracks(new InputSource(inputStream));
        return points;
    }

    protected String saveIcon(File dir, String icon, String name) throws IOException {
        String iconName = FilenameUtils.getName(icon);
        try (InputStream inputStream = getInputStream(icon);
             FileOutputStream outputStream = new FileOutputStream(new File(dir, iconName))) {

            byte[] buffer = new byte[1024];
            for (int read = inputStream.read(buffer); read > -1; read = inputStream
                    .read(buffer)) {
                outputStream.write(buffer, 0, read);
            }
            return dir.getParentFile().getName() + "/" + iconName;
        } catch (Exception e) {
            return icon;
        }
    }
}
