/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import de.cismet.geocpm.api.transform.GeoCPMImportTransformer;
import de.cismet.geocpm.api.transform.TransformException;

import de.cismet.tools.FileUtils;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class OAB_FolderGeoCPMImportTransformer implements GeoCPMImportTransformer {

    //~ Static fields/initializers ---------------------------------------------

    public static final String IMPORT_INFO_FILENAME = "projekt.info";                           // NOI18N
    public static final String IMPORT_INFO_CATCHMENT_NAME = "geocpm.projekt.gep.name";          // NOI18N
    public static final String IMPORT_INFO_NAME = "geocpm.projekt.name";                        // NOI18N
    public static final String IMPORT_INFO_DESC = "geocpm.projekt.beschreibung";                // NOI18N
    public static final String IMPORT_INFO_WMS_BASE_URL = "geocpm.projekt.wms.baseurl";         // NOI18N
    public static final String IMPORT_INFO_WMS_CAP = "geocpm.projekt.wms.capabiliesurl";        // NOI18N
    public static final String IMPORT_INFO_WMS_GETMAP = "geocpm.projekt.wms.getmaptemplateurl"; // NOI18N
    public static final String IMPORT_INFO_CONTRACTOR = "geocpm.projekt.auftragnehmer";         // NOI18N

    public static final String PROJECT_INFO_FILENAME = "zm.info";                    // NOI18N
    public static final String PROJECT_INFO_NAME = "geocpm.projekt.zm.name";         // NOI18N
    public static final String PROJECT_INFO_DESC = "geocpm.projekt.zm.beschreibung"; // NOI18N
    public static final String PROJECT_INFO_TYPE = "geocpm.projekt.zm.typ";          // NOI18N

    private static final String ANNUALITY_FOLDER_REGEX = "T\\d+"; // NOI18N
    public static final String IMPORT_OUT_DIR = "import_out";

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean accept(final Object obj) {
        final boolean accept = false;

        if (obj instanceof File) {
            final File file = (File)obj;
            if (file.isDirectory() && file.canRead()) {
                final File meta = new File(file, IMPORT_INFO_FILENAME);
                if (meta.exists() && meta.canRead()) {
                    return file.listFiles(new FileUtils.DirectoryFilter()).length > 0;
                }
            }
        }

        return accept;
    }

    @Override
    public Collection<GeoCPMProject> transform(final Object obj) {
        // we rely on the framework to call accept
        final File basedir = (File)obj;
        final File infoFile = new File(basedir, IMPORT_INFO_FILENAME);

        final File outputFolder = new File(basedir, IMPORT_OUT_DIR);
        if (!outputFolder.exists() && !outputFolder.mkdir()) {
            throw new TransformException("cannot create output folder: " + outputFolder); // NOI18N
        }

        final List<GeoCPMProject> projects = new ArrayList<>();

        for (final File projFile : basedir.listFiles(new FileFilter() {

                            @Override
                            public boolean accept(final File pathname) {
                                return pathname.isDirectory() && !pathname.getName().equals(IMPORT_OUT_DIR);
                            }
                        })
        ) {
            final WuppGeoCPMProject proj = new WuppGeoCPMProject();
            proj.setOutputFolder(outputFolder);

            setCommonInfo(proj, infoFile);
            setProjectInfo(proj, projFile);

            writeProjectSQL(proj);

            final File[] annualityFolders = projFile.listFiles(new FileFilter() {

                        @Override
                        public boolean accept(final File pathname) {
                            return pathname.isDirectory() && pathname.getName().matches(ANNUALITY_FOLDER_REGEX);
                        }
                    });

            if (annualityFolders.length == 0) {
                throw new TransformException("no annuality folders found: " + projFile); // NOI18N
            }

            boolean first = true;
            final List<GeoCPMResult> results = new ArrayList<>(annualityFolders.length);
            for (final File annualityFolder : annualityFolders) {
                final File geocpmFile = new File(annualityFolder, "GeoCPM.ein");           // NOI18N
                final File geocpmSubinfo = new File(annualityFolder, "GeoCPMSubInfo.aus"); // NOI18N

                checkAccessible(geocpmFile);
                checkAccessible(geocpmSubinfo);

                final File[] resultDirs = annualityFolder.listFiles(new FileUtils.DirectoryFilter());
                if (resultDirs.length == 0) {
                    throw new TransformException("no result dir found: " + annualityFolder);   // NOI18N
                } else if (resultDirs.length > 1) {
                    throw new TransformException("too many result dirs, only single result supported: "
                                + annualityFolder);                                            // NOI18N
                } else {
                    final File geocpmInfo = new File(resultDirs[0], "GeoCPMInfo.aus");         // NOI18N
                    final File geocpmMMax = new File(resultDirs[0], "GeoCPMMax.aus");          // NOI18N
                    final File geocpmResults = new File(resultDirs[0], "ResultsElements.aus"); // NOI18N

                    checkAccessible(geocpmInfo);
                    checkAccessible(geocpmMMax);
                    checkAccessible(geocpmResults);

                    if (first) {
                        proj.setGeocpmEin(geocpmFile);
                        first = false;
                    }

                    final GeoCPMResult r = new GeoCPMResult(Integer.parseInt(annualityFolder.getName().substring(1)));
                    r.setGeocpmInfo(geocpmInfo);
                    r.setGeocpmSubinfo(geocpmSubinfo);
                    r.setGeocpmMax(geocpmMMax);
                    r.setGeocpmResultElements(geocpmResults);

                    results.add(r);
                }
            }

            proj.setResults(results);

            projects.add(proj);
        }

        return projects;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proj  DOCUMENT ME!
     *
     * @throws  TransformException  DOCUMENT ME!
     */
    private void writeProjectSQL(final WuppGeoCPMProject proj) {
        final File file = new File(proj.getOutputFolder(), "project.sql");       // NOI18N
        if (!file.exists()) {
            try(final BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("INSERT INTO oab_projekt ("                             // NOI18N
                            + "\"name\", "                                       // NOI18N
                            + "beschreibung, "                                   // NOI18N
                            + "auftragnehmer, "                                  // NOI18N
                            + "gewaessereinzugsgebiet"                           // NOI18N
                            + ") VALUES ("                                       // NOI18N
                            + "'" + proj.getProjectName() + "', "                // NOI18N
                            + "'" + proj.getProjectDescription() + "', "         // NOI18N
                            + "'" + proj.getContractor() + "', "                 // NOI18N
                            + "(SELECT id * -1 FROM oab_gewaessereinzugsgebiet WHERE \"name\" = '"
                            + proj.getCatchmentName() + "')"                     // NOI18N
                            + ");");                                             // NOI18N
            } catch (final IOException ex) {
                throw new TransformException("cannot write project sql: " + ex); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     *
     * @throws  TransformException  DOCUMENT ME!
     */
    private void checkAccessible(final File file) {
        if (!file.exists() || !file.canRead()) {
            throw new TransformException("cannot find or read file:" + file); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proj      DOCUMENT ME!
     * @param   infoFile  DOCUMENT ME!
     *
     * @throws  TransformException  DOCUMENT ME!
     */
    private void setCommonInfo(final WuppGeoCPMProject proj, final File infoFile) {
        final Properties projectProps = new Properties();

        try(final BufferedReader br = new BufferedReader(new FileReader(infoFile))) {
            projectProps.load(br);
        } catch (final IOException ex) {
            throw new TransformException("cannot read project info file", ex); // NOI18N
        }

        final String catchmentName = projectProps.getProperty(IMPORT_INFO_CATCHMENT_NAME);
        if ((catchmentName == null) || catchmentName.isEmpty()) {
            throw new TransformException("cannot find catchment name: " + IMPORT_INFO_CATCHMENT_NAME); // NOI18N
        }

        final String projectName = projectProps.getProperty(IMPORT_INFO_NAME);
        if ((projectName == null) || projectName.isEmpty()) {
            throw new TransformException("cannot find project name: " + IMPORT_INFO_NAME); // NOI18N
        }

        final String projectDesc = projectProps.getProperty(IMPORT_INFO_DESC);
        final String wmsBaseUrl = projectProps.getProperty(IMPORT_INFO_WMS_BASE_URL);
        final String wmsCapUrl = projectProps.getProperty(IMPORT_INFO_WMS_CAP);
        final String wmsGetmapUrl = projectProps.getProperty(IMPORT_INFO_WMS_GETMAP);
        final String contractor = projectProps.getProperty(IMPORT_INFO_CONTRACTOR);

        proj.setCatchmentName(catchmentName);
        proj.setProjectName(projectName);
        proj.setProjectDescription(projectDesc);
        proj.setWmsBaseUrl(wmsBaseUrl);
        proj.setWmsCapabilitiesUrl(wmsCapUrl);
        proj.setWmsGetMapTemplateUrl(wmsGetmapUrl);
        proj.setContractor(contractor);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proj        DOCUMENT ME!
     * @param   projFolder  DOCUMENT ME!
     *
     * @throws  TransformException  DOCUMENT ME!
     */
    private void setProjectInfo(final WuppGeoCPMProject proj, final File projFolder) {
        final File infoFile = new File(projFolder, PROJECT_INFO_FILENAME);
        String projectType = null;
        if (infoFile.exists()) {
            final Properties cmProperties = new Properties();

            try(final BufferedReader br = new BufferedReader(new FileReader(infoFile))) {
                cmProperties.load(br);
            } catch (final IOException ex) {
                throw new TransformException("cannot read zm info file", ex); // NOI18N
            }

            final String projectName = cmProperties.getProperty(PROJECT_INFO_NAME);
            if ((projectName == null) || projectName.isEmpty()) {
                throw new TransformException("cannot find project name: " + PROJECT_INFO_NAME); // NOI18N
            }

            final String projectDesc = cmProperties.getProperty(PROJECT_INFO_DESC);
            projectType = cmProperties.getProperty(PROJECT_INFO_TYPE);

            proj.setName(projectName);
            proj.setDescription(projectDesc);
        }

        setProjectType(proj, projectType, projFolder.getName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  proj        DOCUMENT ME!
     * @param  propType    DOCUMENT ME!
     * @param  folderName  DOCUMENT ME!
     */
    private void setProjectType(final WuppGeoCPMProject proj, final String propType, final String folderName) {
        String projectType = (propType == null) ? "" : propType;
        try {
            proj.setType(Type.valueOf(projectType));
        } catch (final IllegalArgumentException ex) {
            // improper value, try parsing from name
            final int index = folderName.indexOf('-');
            if (index < 0) {
                projectType = folderName.substring(0);
            } else {
                projectType = folderName.substring(0, index).trim();
            }

            try {
                proj.setType(Type.valueOf(projectType));
            } catch (final IllegalArgumentException e) {
                final TransformException t = new TransformException("cannot find proper type of project: "
                                + projectType,
                        e); // NOI18N
                t.addSuppressed(ex);

                throw t;
            }
        }
    }
}
