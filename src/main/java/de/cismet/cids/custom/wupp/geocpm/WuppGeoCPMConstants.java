/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class WuppGeoCPMConstants {

    //~ Static fields/initializers ---------------------------------------------

    public static final String IMPORT_OUT_DIR = "import_out";                                        // NOI18N
    public static final String IMPORT_INFO_FILENAME = "projekt.info";                                // NOI18N
    public static final String IMPORT_INFO_CATCHMENT_NAME = "geocpm.projekt.gep.name";               // NOI18N
    public static final String IMPORT_INFO_NAME = "geocpm.projekt.name";                             // NOI18N
    public static final String IMPORT_INFO_DESC = "geocpm.projekt.beschreibung";                     // NOI18N
    public static final String IMPORT_INFO_WMS_BASE_URL = "geocpm.projekt.wms.baseurl";              // NOI18N
    public static final String IMPORT_INFO_WMS_CAP = "geocpm.projekt.wms.capabiliesurl";             // NOI18N
    public static final String IMPORT_INFO_CONTRACTOR = "geocpm.projekt.auftragnehmer";              // NOI18N
    public static final String IMPORT_INFO_STATE_DEM = "geocpm.projekt.standDGM";                    // NOI18N
    public static final String IMPORT_INFO_STATE_ALKIS = "geocpm.projekt.standAlkis";                // NOI18N
    public static final String IMPORT_INFO_STATE_VERDIS = "geocpm.projekt.standVerdis";              // NOI18N
    public static final String IMPORT_INFO_CALCULATION_MODE = "geocpm.projekt.berechnungsverfahren"; // NOI18N

    public static final String PROJECT_INFO_FILENAME = "zm.info";                    // NOI18N
    public static final String PROJECT_INFO_NAME = "geocpm.projekt.zm.name";         // NOI18N
    public static final String PROJECT_INFO_TYPE = "geocpm.projekt.zm.typ";          // NOI18N
    public static final String PROJECT_INFO_DESC = "geocpm.projekt.zm.beschreibung"; // NOI18N

    public static final String CALC_INFO_FILENAME = "berechnung.info";                                      // NOI18N
    public static final String CALC_INFO_TS_STARTTIME = "geocpm.projekt.zm.berechnung.zr.startzeit";        // NOI18N
    public static final String CALC_INFO_TS_ENDTIME = "geocpm.projekt.zm.berechnung.zr.endzeit";            // NOI18N
    public static final String CALC_INFO_TS_NO_OF_STEPS = "geocpm.projekt.zm.berechnung.zr.anzahlSchritte"; // NOI18N

    public static final String GEOCPM_EIN_FILENAME = "GeoCPM.ein";                   // NOI18N
    public static final String GEOCPM_SUBINFO_AUS_FILENAME = "GeoCPMSubInfo.aus";    // NOI18N
    public static final String GEOCPM_INFO_AUS_FILENAME = "GeoCPMInfo.aus";          // NOI18N
    public static final String GEOCPM_MAX_AUS_FILENAME = "GeoCPMMax.aus";            // NOI18N
    public static final String RESULTSELEMENTS_AUS_FILENAME = "ResultsElements.aus"; // NOI18N

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WuppGeoCPMConstants object.
     */
    private WuppGeoCPMConstants() {
    }
}
