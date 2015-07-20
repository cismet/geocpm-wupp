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

    private static final String ANNUALITY_FOLDER_REGEX = "T\\d+"; // NOI18N

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean accept(final Object obj) {
        boolean accept = true;

        if (obj instanceof File) {
            final File file = (File)obj;
            if (file.isDirectory() && file.canRead()) {
                File meta = new File(file, WuppGeoCPMConstants.IMPORT_INFO_FILENAME);
                if (meta.exists() && meta.canRead()) {
                    final File[] projects = file.listFiles(new FileFilter() {

                                @Override
                                public boolean accept(final File pathname) {
                                    return pathname.isDirectory()
                                                && !pathname.getName().equals(WuppGeoCPMConstants.IMPORT_OUT_DIR);
                                }
                            });

                    if (projects.length > 0) {
                        for (final File projDir : projects) {
                            meta = new File(projDir, WuppGeoCPMConstants.PROJECT_INFO_FILENAME);
                            if (meta.exists() && meta.canRead()) {
                                for (final File calcDir : projDir.listFiles(new FileUtils.DirectoryFilter())) {
                                    meta = new File(calcDir, WuppGeoCPMConstants.CALC_INFO_FILENAME);

                                    if (meta.exists() && meta.canRead()) {
                                        accept = calcDir.listFiles(new FileUtils.DirectoryFilter()).length == 1;
                                    } else {
                                        accept = false;
                                    }
                                }
                            } else {
                                accept = false;
                            }
                        }
                    } else {
                        accept = false;
                    }
                } else {
                    accept = false;
                }
            } else {
                accept = false;
            }
        }

        return accept;
    }

    @Override
    public Collection<GeoCPMProject> transform(final Object obj) {
        // we rely on the framework to call accept
        final File basedir = (File)obj;
        final File infoFile = new File(basedir, WuppGeoCPMConstants.IMPORT_INFO_FILENAME);

        final File outputFolder = new File(basedir, WuppGeoCPMConstants.IMPORT_OUT_DIR);
        if (!outputFolder.exists() && !outputFolder.mkdir()) {
            throw new TransformException("cannot create output folder: " + outputFolder); // NOI18N
        }

        final List<GeoCPMProject> projects = new ArrayList<>();

        for (final File projFile : basedir.listFiles(new FileFilter() {

                            @Override
                            public boolean accept(final File pathname) {
                                return pathname.isDirectory()
                                    && !pathname.getName().equals(WuppGeoCPMConstants.IMPORT_OUT_DIR);
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
                final File geocpmFile = new File(annualityFolder, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
                final File geocpmSubinfo = new File(annualityFolder, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
                final File calcinfo = new File(annualityFolder, WuppGeoCPMConstants.CALC_INFO_FILENAME);

                checkAccessible(geocpmFile);
                checkAccessible(geocpmSubinfo);
                checkAccessible(calcinfo);

                final File[] resultDirs = annualityFolder.listFiles(new FileUtils.DirectoryFilter());
                if (resultDirs.length == 0) {
                    throw new TransformException("no result dir found: " + annualityFolder);            // NOI18N
                } else if (resultDirs.length > 1) {
                    throw new TransformException("too many result dirs, only single result supported: " // NOI18N
                                + annualityFolder);
                } else {
                    final File geocpmInfo = new File(resultDirs[0], WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
                    final File geocpmMMax = new File(resultDirs[0], WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
                    final File geocpmResults = new File(
                            resultDirs[0],
                            WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);

                    checkAccessible(geocpmInfo);
                    checkAccessible(geocpmMMax);
                    checkAccessible(geocpmResults);

                    if (first) {
                        proj.setGeocpmEin(geocpmFile);
                        first = false;
                    }

                    final WuppGeoCPMResult r = new WuppGeoCPMResult(Integer.parseInt(
                                annualityFolder.getName().substring(1)));
                    r.setGeocpmInfo(geocpmInfo);
                    r.setGeocpmSubinfo(geocpmSubinfo);
                    r.setGeocpmMax(geocpmMMax);
                    r.setGeocpmResultElements(geocpmResults);

                    setCalcInfo(r, calcinfo);

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
     * @param   result    DOCUMENT ME!
     * @param   calcInfo  DOCUMENT ME!
     *
     * @throws  TransformException  DOCUMENT ME!
     */
    private void setCalcInfo(final WuppGeoCPMResult result, final File calcInfo) {
        final Properties calcProps = new Properties();

        try(final BufferedReader br = new BufferedReader(new FileReader(calcInfo))) {
            calcProps.load(br);
        } catch (final IOException ex) {
            throw new TransformException("cannot read calculation info file", ex); // NOI18N
        }

        final String tsStartTimeProp = calcProps.getProperty(WuppGeoCPMConstants.CALC_INFO_TS_STARTTIME);
        if ((tsStartTimeProp == null) || tsStartTimeProp.isEmpty()) {
            throw new TransformException("cannot find ts start time: " + WuppGeoCPMConstants.CALC_INFO_TS_STARTTIME); // NOI18N
        }

        final String tsEndTimeProp = calcProps.getProperty(WuppGeoCPMConstants.CALC_INFO_TS_ENDTIME);
        if ((tsEndTimeProp == null) || tsEndTimeProp.isEmpty()) {
            throw new TransformException("cannot find ts end time: " + WuppGeoCPMConstants.CALC_INFO_TS_ENDTIME); // NOI18N
        }

        final String tsNoOfStepsProp = calcProps.getProperty(WuppGeoCPMConstants.CALC_INFO_TS_NO_OF_STEPS);
        if ((tsNoOfStepsProp == null) || tsNoOfStepsProp.isEmpty()) {
            throw new TransformException("cannot find ts noOfSteps: " + WuppGeoCPMConstants.CALC_INFO_TS_NO_OF_STEPS); // NOI18N
        }

        final int tsStartTime;
        final int tsEndTime;
        final int tsNoOfSteps;
        try {
            tsStartTime = Integer.parseInt(tsStartTimeProp);
            tsEndTime = Integer.parseInt(tsEndTimeProp);
            tsNoOfSteps = Integer.parseInt(tsNoOfStepsProp);
        } catch (final NumberFormatException nfe) {
            throw new TransformException("invalid ts property value", nfe); // NOI18N
        }

        if (tsEndTime <= tsStartTime) {
            throw new TransformException("ts start time must be lower than ts end time: [starttime=" // NOI18N
                        + tsStartTime                                       // NOI18N
                        + "|endtime=" + tsEndTime + "]");                   // NOI18N
        }

        if (tsNoOfSteps < 2) {
            throw new TransformException("ts noOfSteps must be at least two: " + tsNoOfSteps); // NOI18N
        }

        result.setTsStartTime(tsStartTime);
        result.setTsEndTime(tsEndTime);
        result.setNoOSteps(tsNoOfSteps);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proj  DOCUMENT ME!
     *
     * @throws  TransformException  DOCUMENT ME!
     */
    private void writeProjectSQL(final WuppGeoCPMProject proj) {
        final File file = new File(proj.getOutputFolder(), "project.sql");                    // NOI18N
        if (!file.exists()) {
            try(final BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("INSERT INTO oab_projekt ("                                          // NOI18N
                            + "\"name\", "                                                    // NOI18N
                            + "beschreibung, "                                                // NOI18N
                            + "auftragnehmer, "                                               // NOI18N
                            + "gewaessereinzugsgebiet"                                        // NOI18N
                            + ") VALUES ("                                                    // NOI18N
                            + "'" + proj.getProjectName() + "', "                             // NOI18N
                            + "'" + proj.getProjectDescription() + "', "                      // NOI18N
                            + "'" + proj.getContractor() + "', "                              // NOI18N
                            + "(SELECT id FROM oab_gewaessereinzugsgebiet WHERE \"name\" = '" // NOI18N
                            + proj.getCatchmentName() + "')"                                  // NOI18N
                            + ");");                                                          // NOI18N
            } catch (final IOException ex) {
                throw new TransformException("cannot write project sql: " + ex);              // NOI18N
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

        final String catchmentName = projectProps.getProperty(WuppGeoCPMConstants.IMPORT_INFO_CATCHMENT_NAME);
        if ((catchmentName == null) || catchmentName.isEmpty()) {
            throw new TransformException("cannot find catchment name: " // NOI18N
                        + WuppGeoCPMConstants.IMPORT_INFO_CATCHMENT_NAME);
        }

        final String projectName = projectProps.getProperty(WuppGeoCPMConstants.IMPORT_INFO_NAME);
        if ((projectName == null) || projectName.isEmpty()) {
            throw new TransformException("cannot find project name: " + WuppGeoCPMConstants.IMPORT_INFO_NAME); // NOI18N
        }

        final String projectDesc = projectProps.getProperty(WuppGeoCPMConstants.IMPORT_INFO_DESC);
        final String wmsBaseUrl = projectProps.getProperty(WuppGeoCPMConstants.IMPORT_INFO_WMS_BASE_URL);
        final String wmsCapUrl = projectProps.getProperty(WuppGeoCPMConstants.IMPORT_INFO_WMS_CAP);
        final String contractor = projectProps.getProperty(WuppGeoCPMConstants.IMPORT_INFO_CONTRACTOR);

        proj.setCatchmentName(catchmentName);
        proj.setProjectName(projectName);
        proj.setProjectDescription(projectDesc);
        proj.setWmsBaseUrl(wmsBaseUrl);
        proj.setWmsCapabilitiesUrl(wmsCapUrl);
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
        final File infoFile = new File(projFolder, WuppGeoCPMConstants.PROJECT_INFO_FILENAME);
        String projectType = null;
        if (infoFile.exists()) {
            final Properties cmProperties = new Properties();

            try(final BufferedReader br = new BufferedReader(new FileReader(infoFile))) {
                cmProperties.load(br);
            } catch (final IOException ex) {
                throw new TransformException("cannot read zm info file", ex); // NOI18N
            }

            final String projectName = cmProperties.getProperty(WuppGeoCPMConstants.PROJECT_INFO_NAME);
            if ((projectName == null) || projectName.isEmpty()) {
                throw new TransformException("cannot find project name: " + WuppGeoCPMConstants.PROJECT_INFO_NAME); // NOI18N
            }

            final String projectDesc = cmProperties.getProperty(WuppGeoCPMConstants.PROJECT_INFO_DESC);
            projectType = cmProperties.getProperty(WuppGeoCPMConstants.PROJECT_INFO_TYPE);

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
                final TransformException t = new TransformException("cannot find proper type of project: " // NOI18N
                                + projectType,
                        e);
                t.addSuppressed(ex);

                throw t;
            }
        }
    }
}
