package ru.zoom4ikdan4ik.modified;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ArchiveLogs {
    public final static void zipAndClear(final String logsFolder) {
        final File folder = new File(logsFolder);

        if (folder.exists()) {
            final File[] files = folder.listFiles();
            final ZipFile zipFile = getZipFile(folder);

            for (final File file : files) {
                if (file.getName().endsWith(".zip")) continue;

                try {
                    zipFile.addFile(file);
                } catch (ZipException e) {
                    e.printStackTrace();
                }

                file.delete();
            }
        } else folder.mkdirs();
    }

    public final static ZipFile getZipFile(final File folder) {
        final Date date = new Date();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY");

        File zip = null;
        boolean exist = true;
        int index = 0;
        while (exist) {
            zip = new File(folder, String.format("%s_%d.zip", simpleDateFormat.format(date), index++));

            exist = zip.exists();
        }

        return new ZipFile(zip);
    }
}
