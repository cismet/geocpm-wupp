/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm;

import lombok.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class Tools {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Tools object.
     */
    private Tools() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   zipFile    DOCUMENT ME!
     * @param   targetDir  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static void unzip(@NonNull final ZipFile zipFile, @NonNull final File targetDir) throws IOException {
        final Enumeration<? extends ZipEntry> en = zipFile.entries();
        while (en.hasMoreElements()) {
            final ZipEntry ze = en.nextElement();
            final File outFile = new File(targetDir, ze.getName());

            if (!outFile.getParentFile().mkdirs()) {
                throw new IOException("cannot create directory structure: " + outFile); // NOI18N
            }

            try(final FileOutputStream fos = new FileOutputStream(outFile)) {
                try(final InputStream is = zipFile.getInputStream(ze)) {
                    final byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }
    }
}
