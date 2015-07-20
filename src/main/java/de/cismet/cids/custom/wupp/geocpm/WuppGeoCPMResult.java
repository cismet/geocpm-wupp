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

import de.cismet.geocpm.api.GeoCPMResult;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WuppGeoCPMResult extends GeoCPMResult {

    //~ Instance fields --------------------------------------------------------

    private int tsStartTime;
    private int tsEndTime;
    private int noOSteps;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WuppGeoCPMResult object.
     *
     * @param  annuality  DOCUMENT ME!
     */
    public WuppGeoCPMResult(final int annuality) {
        super(annuality);
    }
}
