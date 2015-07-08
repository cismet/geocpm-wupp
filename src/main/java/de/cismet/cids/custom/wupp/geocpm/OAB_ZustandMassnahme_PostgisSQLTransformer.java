/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import de.cismet.geocpm.api.entity.Point;
import de.cismet.geocpm.api.entity.Result;
import de.cismet.geocpm.api.entity.Triangle;
import de.cismet.geocpm.api.transform.GeoCPMProjectTransformer;
import de.cismet.geocpm.api.transform.TransformException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Slf4j
public class OAB_ZustandMassnahme_PostgisSQLTransformer implements GeoCPMProjectTransformer {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean accept(final GeoCPMProject obj) {
        return (obj instanceof WuppGeoCPMProject)
                    && (obj.getTriangles() != null)
                    && !obj.getTriangles().isEmpty()
                    && (obj.getResults() != null)
                    && !obj.getResults().isEmpty()
                    && (((WuppGeoCPMProject)obj).getProjectName() != null)
                    && !((WuppGeoCPMProject)obj).getProjectName().isEmpty();
    }

    @Override
    public GeoCPMProject transform(final GeoCPMProject obj) {
        // we rely on the framework to call accept
        final WuppGeoCPMProject proj = (WuppGeoCPMProject)obj;

        final File sqlFile;
        try {
            sqlFile = File.createTempFile("oab_zustandmassnahme_" + obj.getName() + "_", ".sql");
        } catch (final IOException ex) {
            final String message = "cannot create sql output file"; // NOI18N
            if (log.isErrorEnabled()) {
                log.error(message, ex);
            }
            throw new TransformException(message, ex);
        }

        try(final BufferedWriter bw = new BufferedWriter(new FileWriter(sqlFile))) {
            bw.write("BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;"); // NOI18N
            bw.newLine();
            bw.newLine();

            writeZustandMassnahme(bw, proj);
            bw.newLine();

            writeBerechnung(bw, proj);
            bw.newLine();

            writeData(bw, proj);
            bw.newLine();

            writeBoundingGeometry(bw);
            bw.newLine();

            bw.newLine();
            bw.write("COMMIT;");

            proj.setZustandMassnahmeSqlFile(sqlFile);
        } catch (final IOException ex) {
            final String message = "cannot write sql output file"; // NOI18N
            if (log.isErrorEnabled()) {
                log.error(message, ex);
            }
            throw new TransformException(message, ex);
        }

        return proj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw    DOCUMENT ME!
     * @param   proj  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeZustandMassnahme(final BufferedWriter bw, final WuppGeoCPMProject proj) throws IOException {
        // the geometry cannot be set yet
        bw.write("INSERT INTO oab_zustand_massnahme ("
                    + "projekt, "
                    + "typ, "
                    + "beschreibung, "
                    + "name"
                    + ") VALUES ("
                    + "(SELECT id FROM oab_projekt WHERE name = '" + proj.getProjectName() + "'), "
                    + "(SELECT id FROM oab_zm_typ WHERE name = '" + proj.getType() + "'), "
                    + "'" + proj.getDescription() + "', "
                    + "'" + proj.getName() + "'"
                    + ");");
        bw.newLine();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw    DOCUMENT ME!
     * @param   proj  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeBerechnung(final BufferedWriter bw, final WuppGeoCPMProject proj) throws IOException {
        for (final GeoCPMResult result : proj.getResults()) {
            bw.write("INSERT INTO oab_berechnung (jaehrlichkeit) VALUES (" + result.getAnnuality() + ");"); // NOI18N
            bw.newLine();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw    DOCUMENT ME!
     * @param   proj  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeData(final BufferedWriter bw, final WuppGeoCPMProject proj) throws IOException {
        final List<BELine> breakingEdges = new ArrayList<>();
        for (final Triangle t : proj.getTriangles()) {
            writeTriangle(bw, t);

            bufferBreakingEdge(bw, t, breakingEdges);

            writeWaterResults(bw, proj.getResults());
        }

        writeBreakingEdgeBuffer(bw, breakingEdges);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw  DOCUMENT ME!
     * @param   t   DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeTriangle(final BufferedWriter bw, final Triangle t) throws IOException {
        bw.write("INSERT INTO oab_daten_tin ("
                    + "fk_oab_zustand_massnahme, "
                    + "dreieck"
                    + ") VALUES ("
                    + "(SELECT max(id) FROM oab_zustand_massnahme), "
                    + "st_geomfromtext('" + triangleToWKT(t) + "', 25832)"
                    + ");");
        bw.newLine();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw     DOCUMENT ME!
     * @param   t      DOCUMENT ME!
     * @param   lines  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void bufferBreakingEdge(final BufferedWriter bw, final Triangle t, final List<BELine> lines)
            throws IOException {
        if (t.hasBreakingEdge()) {
            final BELine line = new BELine();
            if (t.getA().getZ() != t.getBreakingEdgeA()) {
                line.a = t.getA();
            }

            if (t.getB().getZ() != t.getBreakingEdgeB()) {
                if (line.a == null) {
                    line.a = t.getB();
                } else {
                    line.b = t.getB();
                }
            }

            if (t.getC().getZ() != t.getBreakingEdgeC()) {
                if (line.a == null) {
                    line.a = t.getC();
                } else if (line.b == null) {
                    line.b = t.getC();
                } else {
                    // this case will supposedly be very rare
                    line.c = t.getC();
                }
            }

            // breaking edge is only present if at least two points are overridden, thus at least b is not null
            // the special equals of BELines takes care that there are no lines on top of each other
            if ((line.b != null) && !lines.contains(line)) {
                lines.add(line);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw     DOCUMENT ME!
     * @param   lines  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeBreakingEdgeBuffer(final BufferedWriter bw, final List<BELine> lines) throws IOException {
        // NOTE: we currently don't process the lines to string them together if they share points. if this is needed
        // some day this would be the right place

        for (final BELine line : lines) {
            bw.write("INSERT INTO oab_daten_bruchkante ("
                        + "fk_oab_zustand_massnahme, "
                        + "bruchkante"
                        + ") VALUES ("
                        + "(SELECT max(id) FROM oab_zustand_massnahme), "
                        + "st_geomfromtext('" + breakingEdgeToWKT(line) + "', 25832)"
                        + ");");
            bw.newLine();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw       DOCUMENT ME!
     * @param   results  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeWaterResults(final BufferedWriter bw, final Collection<GeoCPMResult> results) throws IOException {
        for (final GeoCPMResult gr : results) {
            for (final Result r : gr.getResults()) {
                writeWaterMax(bw, gr.getAnnuality(), r);
                writeWaterTime(bw, gr.getAnnuality(), r);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw         DOCUMENT ME!
     * @param   annuality  DOCUMENT ME!
     * @param   r          DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeWaterMax(final BufferedWriter bw, final int annuality, final Result r) throws IOException {
        bw.write("INSERT INTO oab_daten_wasserstand_max ("
                    + "fk_oab_daten_tin, "
                    + "fk_oab_berechnung, "
                    + "max_wasser"
                    + " ) VALUES ("
                    + "(SELECT max(id) FROM oab_daten_tin), "
                    + "(SELECT max(id) FROM oab_berechnung WHERE jaehrlichkeit = " + annuality + "), "
                    + r.getMaxWaterlevel()
                    + ");");
        bw.newLine();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw         DOCUMENT ME!
     * @param   annuality  DOCUMENT ME!
     * @param   r          DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeWaterTime(final BufferedWriter bw, final int annuality, final Result r) throws IOException {
        for (final Entry<Double, Double> levels : r.getWaterlevels().entrySet()) {
            bw.write("INSERT INTO oab_daten_wasserstand_zeit ("
                        + "fk_oab_daten_tin, "
                        + "fk_oab_berechnung, "
                        + "zeitstempel, "
                        + "wasserstand"
                        + " ) VALUES ("
                        + "(SELECT max(id) FROM oab_daten_tin), "
                        + "(SELECT max(id) FROM oab_berechnung WHERE jaehrlichkeit = " + annuality + "), "
                        + levels.getKey() + ", "
                        + levels.getValue()
                        + ");");
            bw.newLine();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeBoundingGeometry(final BufferedWriter bw) throws IOException {
        //J-
        bw.write("INSERT INTO geom (geo_field) VALUES ("
                    + "(SELECT st_force2d(st_setsrid(st_convexhull(st_collect(dreieck)), 25832)) "
                        + "FROM oab_daten_tin WHERE fk_oab_zustand_massnahme = ("
                    + "SELECT max(id) FROM oab_zustand_massnahme"
                    + "))"
                    + ");");
        //J+
        bw.newLine();

        bw.write("UPDATE oab_zustand_massnahme "
                    + "SET umschreibende_geometrie = (SELECT max(id) FROM geom) "
                    + "WHERE id = (SELECT max(id) FROM oab_zustand_massnahme);");
        bw.newLine();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   t  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String triangleToWKT(final Triangle t) {
        // the z property might be overridden by the breaking edge section
        final double zA = (t.getBreakingEdgeA() <= 0) ? t.getA().getZ() : t.getBreakingEdgeA();
        final double zB = (t.getBreakingEdgeB() <= 0) ? t.getB().getZ() : t.getBreakingEdgeB();
        final double zC = (t.getBreakingEdgeC() <= 0) ? t.getC().getZ() : t.getBreakingEdgeC();

        return "POLYGON(("
                    + t.getA().getX() + " " + t.getA().getY() + " " + zA + ", "
                    + t.getB().getX() + " " + t.getB().getY() + " " + zB + ", "
                    + t.getC().getX() + " " + t.getC().getY() + " " + zC + ", "
                    + t.getA().getX() + " " + t.getA().getY() + " " + zA + "))";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   line  t DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String breakingEdgeToWKT(final BELine line) {
        return "LINESTRING("
                    + line.a.getX() + " " + line.a.getY() + ", "
                    + line.b.getX() + " " + line.b.getY()
                    + ((line.c == null) ? "" : (", " + line.c.getX() + " " + line.c.getY()))
                    + ")";
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @SuppressWarnings(value = { "EqualsAndHashcode", "null" })
    private static final class BELine {

        //~ Instance fields ----------------------------------------------------

        Point a = null;
        Point b = null;
        Point c = null;

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof BELine) {
                final BELine other = (BELine)obj;

                final boolean forwardEqual = ((pointEqual(a, other.a))
                                && (pointEqual(b, other.b))
                                && (pointEqual(c, other.c)));

                final boolean backwardEqual;
                if ((this.c == null) && (other.c == null)) {
                    backwardEqual = pointEqual(this.a, other.b) && pointEqual(this.b, other.a);
                } else {
                    backwardEqual = (pointEqual(a, other.c)) && (pointEqual(b, other.b)) && (pointEqual(c, other.a));
                }

                return forwardEqual || backwardEqual;
            } else {
                return false;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param   a  DOCUMENT ME!
         * @param   b  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private boolean pointEqual(final Point a, final Point b) {
            if ((a == null) && (b == null)) {
                return true;
            } else if ((a == null) && (b != null)) {
                return false;
            } else if ((a != null) && (b == null)) {
                return false;
            } else {
                return (a.getX() == b.getX()) && (a.getY() == b.getY());
            }
        }
    }
}
