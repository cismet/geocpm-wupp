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
import lombok.ToString;

import java.io.File;

import java.util.Date;

import de.cismet.geocpm.api.GeoCPMProject;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WuppGeoCPMProject extends GeoCPMProject {

    //~ Instance fields --------------------------------------------------------

    private File zustandMassnahmeSqlFile;
    private File projectSqlFile;
    private File outputFolder;
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
    private String contractor;
    private String stateDEM;
    private Date stateAlkis;
    private Date stateVerdis;
}
