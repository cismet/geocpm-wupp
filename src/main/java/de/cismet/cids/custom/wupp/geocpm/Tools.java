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

            if (ze.isDirectory()) {
                if (!outFile.mkdirs()) {
                    throw new IOException("cannot create directory structure: " + outFile); // NOI18N
                }
            } else {
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

    /**
     * Creates a string that only consists of the chars '[a-z0-9_]'. The rules are:<br>
     * <br>
     *
     * <ul>
     *   <li>input to lower case</li>
     *   <li>space to '_'</li>
     *   <li>German special chars 'äöüß' to 'aeoeuess'</li>
     *   <li>remove any remaining chars that are not in '[a-z0-9_]'</li>
     * </ul>
     *
     * @param   name  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("fallthrough")
    public static String convertString(@NonNull final String name) {
        final StringBuilder sb = new StringBuilder(name);
        for (int i = 0; i < sb.length(); ++i) {
            switch (sb.charAt(i)) {
                // äÄ
                case '\u00c4':
                case '\u00e4': {
                    sb.replace(i, i + 1, "ae");
                    ++i;
                    break;
                }
                // öÖ
                case '\u00d6':
                case '\u00f6': {
                    sb.replace(i, i + 1, "oe");
                    ++i;
                    break;
                }
                // üÜ
                case '\u00dc':
                case '\u00fc': {
                    sb.replace(i, i + 1, "ue");
                    ++i;
                    break;
                }
                // ß
                case '\u00df': {
                    sb.replace(i, i + 1, "ss");
                    break;
                }
                // <SPACE>
                case '\u0020': {
                    sb.setCharAt(i, '_');
                    break;
                }
            }
        }

        return sb.toString().toLowerCase().replaceAll("[^a-z0-9_]", ""); // NOI18N
    }
}
