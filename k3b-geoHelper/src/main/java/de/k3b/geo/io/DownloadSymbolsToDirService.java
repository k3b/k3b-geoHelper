package de.k3b.geo.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DownloadSymbolsToDirService extends DownloadSymbolsBaseService {
    private File dir = null;

    public DownloadSymbolsToDirService(String userAgent) {
        super(userAgent);
    }

    public DownloadSymbolsToDirService dir(File dir) {
        this.dir = dir;
        return this;
    }

    protected String createSymbolUri(String iconName) {
        return dir.getParentFile().getName() + "/" + iconName;
    }

    protected OutputStream createOutputStream(String iconName) throws FileNotFoundException {
        return new FileOutputStream(new File(dir, iconName));
    }
}
