package de.k3b.geo.io.wikipedia;

import org.apache.commons.io.FilenameUtils;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.DownloadSymbolsToDirService;
import de.k3b.geo.io.gpx.GpxReader;

public class DownloadService {
    private static final int THUMBSIZE = 50;

    private final String serviceName;
    private final String userAgent;
    private final DownloadSymbolsToDirService downloadSymbolsService;

    public DownloadService(String serviceName, String userAgent) {
        this.serviceName = serviceName;
        this.userAgent = userAgent;
        downloadSymbolsService = new DownloadSymbolsToDirService(userAgent);

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

    public List<IGeoPointInfo> getGeoPointInfos(String lat, String lon, File file) throws IOException {
        List<IGeoPointInfo> points = getGeoPointInfos(lat, lon);
        String baseName = FilenameUtils.getBaseName(file.getPath());
        File dir = new File(file.getParentFile(), baseName);
        dir.mkdirs();

        points = downloadSymbolsService.dir(dir).convert(points);

        return points;
    }

    public List<IGeoPointInfo> getGeoPointInfos(String lat, String lon) throws IOException {
        int radius = 10000;
        int maxcount = 5;
        String urlString = this.getUrlString(lat, lon, radius, maxcount);
        InputStream inputStream = this.getInputStream(urlString);
        GpxReader<IGeoPointInfo> parser = new GpxReader<>();

        List<IGeoPointInfo> points = parser.getTracks(new InputSource(inputStream));
        return points;
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
}
