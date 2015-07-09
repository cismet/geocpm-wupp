/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;

import de.cismet.geocpm.api.GeoCPMProject;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WuppGeoCPMProject extends GeoCPMProject {

    //~ Instance fields --------------------------------------------------------

    private File zustandMassnahmeSqlFile;
    private Type type;
    private String catchmentName;
    private String projectName;
    private String projectDescription;
    private String dbDriver;
    private String dbUser;
    private String dbPass;
    private String dbConn;
    private String wmsBaseUrl;
    private String wmsCapabilitiesUrl;
    private String wmsGetMapTemplateUrl;
}
