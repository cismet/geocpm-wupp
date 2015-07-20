/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.zip.ZipFile;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.transform.GeoCPMImportTransformer;
import de.cismet.geocpm.api.transform.TransformException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class OAB_ZipGeoCPMImportTransformer implements GeoCPMImportTransformer {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean accept(final Object obj) {
        boolean accept = false;

        if (obj instanceof File) {
            final File file = (File)obj;
            if (file.canRead()) {
                try {
                    // test if valid zip file
                    final ZipFile zip = new ZipFile(file);
                    zip.close();
                    accept = true;
                } catch (final IOException ex) {
                    ex.printStackTrace();
                    // noop
                }
            }
        }

        return accept;
    }

    @Override
    public Collection<GeoCPMProject> transform(final Object obj) {
        // we assume the framework uses accept
        try(final ZipFile zip = new ZipFile((File)obj)) {
            final File extractFolder = new File(System.getProperty("java.io.tmpdir"),
                    "geocpm_extract_"
                            + System.currentTimeMillis());
            if (!extractFolder.mkdir()) {
                throw new TransformException("cannot create extract folder: " + extractFolder);
            }

            Tools.unzip(zip, extractFolder);

            final OAB_FolderGeoCPMImportTransformer t = new OAB_FolderGeoCPMImportTransformer();
            if (t.accept(extractFolder)) {
                return t.transform(extractFolder);
            } else {
                throw new TransformException("unsupported folder format: " + extractFolder); // NOI18N
            }
        } catch (IOException ex) {
            throw new TransformException("cannot read from input file", ex);                 // NOI18N
        }
    }
}