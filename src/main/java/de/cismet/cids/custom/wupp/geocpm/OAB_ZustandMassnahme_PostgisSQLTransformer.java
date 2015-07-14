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

import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import de.cismet.geocpm.api.GeoCPMUtilities;
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

    //~ Instance fields --------------------------------------------------------

    private final MessageFormat zustandMassnahmeFormat;
    private final MessageFormat tinViewFormat;
    private final MessageFormat beViewFormat;
    private final MessageFormat maxViewFormat;
    private final MessageFormat berechnungFormat;
    private final MessageFormat triangleFormat;
    private final MessageFormat beFormat;
    private final MessageFormat maxWaterFormat;
    private final MessageFormat timeWaterFormat;
    private final MessageFormat triangleWktFormat;
    private final MessageFormat beWkt2Format;
    private final MessageFormat beWkt3Format;

    private final String boundingGeomInsert;
    private final String boundingGeomUpdate;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OAB_ZustandMassnahme_PostgisSQLTransformer object.
     */
    public OAB_ZustandMassnahme_PostgisSQLTransformer() {
        zustandMassnahmeFormat = new MessageFormat(
                "INSERT INTO oab_zustand_massnahme (projekt, typ, beschreibung, name, tin_cap, tin_layer_name, tin_simple_getmap, bruchkanten_cap, bruchkanten_layer_name, bruchkanten_simple_getmap) VALUES ((SELECT id * -1 FROM oab_projekt WHERE \"name\" = ''{0}'' AND abs(gewaessereinzugsgebiet) = (SELECT id FROM oab_gewaessereinzugsgebiet WHERE \"name\" = ''{1}'')), (SELECT id FROM oab_zm_typ WHERE name = ''{2}''), ''{3}'', ''{4}'', ''{5}'', ''{6}'', ''{7}'', ''{8}'', ''{9}'', ''{10}'');");

        tinViewFormat = new MessageFormat(
                "CREATE VIEW {0} AS SELECT id, dreieck AS geometrie FROM oab_daten_tin WHERE fk_oab_zustand_massnahme = (SELECT id FROM oab_zustand_massnahme WHERE \"name\" = ''{1}'' AND abs(projekt) = (SELECT id FROM oab_projekt WHERE \"name\" = ''{2}'' AND abs(gewaessereinzugsgebiet) = (SELECT id FROM oab_gewaessereinzugsgebiet WHERE \"name\" = ''{3}'')));");

        beViewFormat = new MessageFormat(
                "CREATE VIEW {0} AS SELECT id, bruchkante AS geometrie FROM oab_daten_bruchkante WHERE fk_oab_zustand_massnahme = (SELECT id FROM oab_zustand_massnahme WHERE \"name\" = ''{1}'' AND abs(projekt) = (SELECT id FROM oab_projekt WHERE \"name\" = ''{2}'' AND abs(gewaessereinzugsgebiet) = (SELECT id FROM oab_gewaessereinzugsgebiet WHERE \"name\" = ''{3}'')));");

        maxViewFormat = new MessageFormat(
                "CREATE VIEW {0} AS SELECT t.id, t.dreieck AS geometrie, w.max_wasser AS hoehe FROM oab_daten_tin t LEFT JOIN oab_daten_wasserstand_max w ON t.id = w.fk_oab_daten_tin WHERE w.fk_oab_berechnung = (SELECT id FROM oab_berechnung WHERE jaehrlichkeit = {1} AND abs(zustand_massnahme) = (SELECT id FROM oab_zustand_massnahme WHERE \"name\" = ''{2}'' AND abs(projekt) = (SELECT id FROM oab_projekt WHERE \"name\" = ''{3}'' AND abs(gewaessereinzugsgebiet) = (SELECT id FROM oab_gewaessereinzugsgebiet WHERE \"name\" = ''{4}'')))) AND t.fk_oab_zustand_massnahme = (SELECT id FROM oab_zustand_massnahme WHERE \"name\" = ''{2}'' AND abs(projekt) = (SELECT id FROM oab_projekt WHERE \"name\" = ''{3}'' AND abs(gewaessereinzugsgebiet) = (SELECT id FROM oab_gewaessereinzugsgebiet WHERE \"name\" = ''{4}'')));");

        berechnungFormat = new MessageFormat(
                "INSERT INTO oab_berechnung (jaehrlichkeit, zustand_massnahme, max_wasser_cap, max_wasser_layer_name, max_wasser_simple_getmap) VALUES ({0}, (SELECT max(id) * -1 FROM oab_zustand_massnahme), ''{1}'', ''{2}'', ''{3}'');");

        triangleFormat = new MessageFormat(
                "INSERT INTO oab_daten_tin (fk_oab_zustand_massnahme, dreieck) VALUES ((SELECT max(id) FROM oab_zustand_massnahme), st_geomfromtext(''{0}'', 25832));");

        beFormat = new MessageFormat(
                "INSERT INTO oab_daten_bruchkante (fk_oab_zustand_massnahme, bruchkante) VALUES ((SELECT max(id) FROM oab_zustand_massnahme), st_geomfromtext(''{0}'', 25832));");

        maxWaterFormat = new MessageFormat(
                "INSERT INTO oab_daten_wasserstand_max (fk_oab_daten_tin, fk_oab_berechnung, max_wasser) VALUES ((SELECT max(id) FROM oab_daten_tin), (SELECT max(id) FROM oab_berechnung WHERE jaehrlichkeit = {0,number,integer}), {1,number,#.##########});");
        for (final Format format : maxWaterFormat.getFormats()) {
            if (format instanceof NumberFormat) {
                ((NumberFormat)format).setGroupingUsed(false);
            }
            if (format instanceof DecimalFormat) {
                ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(false);
            }
        }

        timeWaterFormat = new MessageFormat(
                "INSERT INTO oab_daten_wasserstand_zeit (fk_oab_daten_tin, fk_oab_berechnung, zeitstempel, wasserstand) VALUES ((SELECT max(id) FROM oab_daten_tin), (SELECT max(id) FROM oab_berechnung WHERE jaehrlichkeit = {0,number,integer}), {1,number,#.##}, {2,number,#.###});");
        for (final Format format : timeWaterFormat.getFormats()) {
            if (format instanceof NumberFormat) {
                ((NumberFormat)format).setGroupingUsed(false);
            }
            if (format instanceof DecimalFormat) {
                ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(false);
            }
        }

        triangleWktFormat = new MessageFormat(
                "POLYGON(({0,number,#.##########} {1,number,#.##########} {2,number,#.##########}, {3,number,#.##########} {4,number,#.##########} {5,number,#.##########}, {6,number,#.##########} {7,number,#.##########} {8,number,#.##########}, {9,number,#.##########} {10,number,#.##########} {11,number,#.##########}))");
        for (final Format format : triangleWktFormat.getFormats()) {
            if (format instanceof NumberFormat) {
                ((NumberFormat)format).setGroupingUsed(false);
            }
            if (format instanceof DecimalFormat) {
                ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(false);
            }
        }

        beWkt2Format = new MessageFormat(
                "LINESTRING({0,number,#.##########} {1,number,#.##########}, {2,number,#.##########} {3,number,#.##########})");
        for (final Format format : beWkt2Format.getFormats()) {
            if (format instanceof NumberFormat) {
                ((NumberFormat)format).setGroupingUsed(false);
            }
            if (format instanceof DecimalFormat) {
                ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(false);
            }
        }
        beWkt3Format = new MessageFormat(
                "LINESTRING({0,number,#.##########} {1,number,#.##########}, {2,number,#.##########} {3,number,#.##########}, {4,number,#.##########} {5,number,#.##########})");
        for (final Format format : beWkt3Format.getFormats()) {
            if (format instanceof NumberFormat) {
                ((NumberFormat)format).setGroupingUsed(false);
            }
            if (format instanceof DecimalFormat) {
                ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(false);
            }
        }

        boundingGeomInsert =
            "INSERT INTO geom (geo_field) VALUES ((SELECT st_force2d(st_setsrid(st_convexhull(st_collect(dreieck)), 25832)) FROM oab_daten_tin WHERE fk_oab_zustand_massnahme = (SELECT max(id) FROM oab_zustand_massnahme)));";

        boundingGeomUpdate =
            "UPDATE oab_zustand_massnahme SET umschreibende_geometrie = (SELECT max(id) FROM geom) WHERE id = (SELECT max(id) FROM oab_zustand_massnahme);";
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean accept(final GeoCPMProject obj) {
        boolean accept = false;

        if (obj instanceof WuppGeoCPMProject) {
            accept = (obj.getTriangles() != null)
                        && !obj.getTriangles().isEmpty()
                        && (obj.getResults() != null)
                        && !obj.getResults().isEmpty();

            if (accept) {
                final WuppGeoCPMProject proj = (WuppGeoCPMProject)obj;
                accept = (proj.getProjectName() != null)
                            && !proj.getProjectName().isEmpty()
                            && (proj.getCatchmentName() != null)
                            && !proj.getCatchmentName().isEmpty();
            }
        }

        return accept;
    }

    @Override
    public GeoCPMProject transform(final GeoCPMProject obj) {
        // we rely on the framework to call accept
        final WuppGeoCPMProject proj = (WuppGeoCPMProject)obj;

        final File sqlFile = new File(proj.getOutputFolder(), "oab_zustandmassnahme_" + obj.getName() + ".sql"); // NOI18N;

        // ensure that the result index matches the triangle index/id
        GeoCPMUtilities.sortTriangles(obj);
        GeoCPMUtilities.sortResults(obj);

        try(final BufferedWriter bw = new BufferedWriter(new FileWriter(sqlFile))) {
            bw.write("BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;"); // NOI18N
            bw.newLine();
            bw.newLine();

            writeZustandMassnahme(bw, proj);
            bw.newLine();
            bw.newLine();

            writeBerechnung(bw, proj);
            bw.newLine();
            bw.newLine();

            writeData(bw, proj);
            bw.newLine();
            bw.newLine();

            writeBoundingGeometry(bw);
            bw.newLine();
            bw.newLine();

            writeViews(bw, proj);
            bw.newLine();
            bw.newLine();

            bw.newLine();
            bw.write("COMMIT;"); // NOI18N

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
        final String tinLayername = createTinName(proj);
        final String beLayername = createBEName(proj);
        final String capUrl = createWMSCapabilitiesUrl(proj);

        // the geometry cannot be set yet
        final Object[] params = new Object[] {
                proj.getProjectName(),
                proj.getCatchmentName(),
                proj.getType(),
                proj.getDescription(),
                proj.getName(),
                capUrl,
                tinLayername,
                createWMSGetMapUrl(tinLayername, proj),
                capUrl,
                beLayername,
                createWMSGetMapUrl(beLayername, proj)
            };

        bw.write(zustandMassnahmeFormat.format(params));
        bw.newLine();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createWMSCapabilitiesUrl(final WuppGeoCPMProject proj) {
        final String url;
        if (proj.getWmsCapabilitiesUrl() != null) {
            url = proj.getWmsCapabilitiesUrl();
        } else if (proj.getWmsBaseUrl() != null) {
            url = proj.getWmsBaseUrl() + "?service=wms&version=1.1.1&request=GetCapabilities"; // NOI18N
        } else {
            url = "<n/a>";                                                                     // NOI18N
        }

        return url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   layername  DOCUMENT ME!
     * @param   proj       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createWMSGetMapUrl(final String layername, final WuppGeoCPMProject proj) {
        final String url;
        if (proj.getWmsGetMapTemplateUrl() == null) {
            if (proj.getWmsBaseUrl() == null) {
                url = "<n/a>";                                                                                // NOI18N
            } else {
                url = proj.getWmsBaseUrl() + "?version=1.1.1&request=GetMap&bbox=<cismap:boundingBox>"        // NOI18N
                            + "&width=<cismap:width>&height=<cismap:height>&srs=cismap:srs>&format=image/png" // NOI18N
                            + "&transparent=true&layers=" + layername;                                        // NOI18N
            }
        } else {
            url = proj.getWmsGetMapTemplateUrl().replaceAll("<layername>", layername);                        // NOI18N
        }

        return url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw    DOCUMENT ME!
     * @param   proj  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeViews(final BufferedWriter bw, final WuppGeoCPMProject proj) throws IOException {
        writeTinView(bw, proj);
        writeBEView(bw, proj);
        writeMaxViews(bw, proj);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw    DOCUMENT ME!
     * @param   proj  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeTinView(final BufferedWriter bw, final WuppGeoCPMProject proj) throws IOException {
        final Object[] params = new Object[] {
                createTinName(proj),
                proj.getName(),
                proj.getProjectName(),
                proj.getCatchmentName()
            };

        bw.write(tinViewFormat.format(params));
        bw.newLine();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createTinName(final WuppGeoCPMProject proj) {
        final StringBuilder sb = new StringBuilder("oab_tin_"); // NOI18N
        sb.append(convert(proj.getProjectName()));
        sb.append('_');
        sb.append(convert(proj.getName()));

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createBEName(final WuppGeoCPMProject proj) {
        final StringBuilder sb = new StringBuilder("oab_bruchkanten_"); // NOI18N
        sb.append(convert(proj.getProjectName()));
        sb.append('_');
        sb.append(convert(proj.getName()));

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proj       DOCUMENT ME!
     * @param   annuality  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createMaxName(final WuppGeoCPMProject proj, final int annuality) {
        final StringBuilder sb = new StringBuilder("oab_max_wasser_"); // NOI18N
        sb.append(convert(proj.getProjectName()));
        sb.append('_');
        sb.append(convert(proj.getName()));
        sb.append("_t");                                               // NOI18N
        sb.append(annuality);

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw    DOCUMENT ME!
     * @param   proj  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeBEView(final BufferedWriter bw, final WuppGeoCPMProject proj) throws IOException {
        final Object[] params = new Object[] {
                createBEName(proj),
                proj.getName(),
                proj.getProjectName(),
                proj.getCatchmentName()
            };

        bw.write(beViewFormat.format(params));
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
    private void writeMaxViews(final BufferedWriter bw, final WuppGeoCPMProject proj) throws IOException {
        for (final GeoCPMResult result : proj.getResults()) {
            final Object[] params = new Object[] {
                    createMaxName(proj, result.getAnnuality()),
                    result.getAnnuality(),
                    proj.getName(),
                    proj.getProjectName(),
                    proj.getCatchmentName()
                };

            bw.write(maxViewFormat.format(params));
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
    private void writeTimeViews(final BufferedWriter bw, final WuppGeoCPMProject proj) throws IOException {
        // TODO: discuss the way to go
    }

    /**
     * DOCUMENT ME!
     *
     * @param   name  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("fallthrough")
    private String convert(final String name) {
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

        return sb.toString().toLowerCase().replaceAll("[^a-z0-9_]", "");
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
        final String capUrl = createWMSCapabilitiesUrl(proj);
        for (final GeoCPMResult result : proj.getResults()) {
            final String maxLayername = createMaxName(proj, result.getAnnuality());

            final Object[] params = new Object[] {
                    result.getAnnuality(),
                    capUrl,
                    maxLayername,
                    createWMSGetMapUrl(maxLayername, proj)
                };

            bw.write(berechnungFormat.format(params));

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

            writeWaterResults(bw, t, proj.getResults());
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
        bw.write(triangleFormat.format(new Object[] { triangleToWKT(t) }));
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
            bw.write(beFormat.format(new Object[] { breakingEdgeToWKT(line) }));
            bw.newLine();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bw       DOCUMENT ME!
     * @param   t        DOCUMENT ME!
     * @param   results  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeWaterResults(final BufferedWriter bw, final Triangle t, final Collection<GeoCPMResult> results)
            throws IOException {
        for (final GeoCPMResult gr : results) {
            // the result index matches the triangle id, sorting is ensured by parent operation
            final Result r = ((List<Result>)gr.getResults()).get(t.getId());

            writeWaterMax(bw, gr.getAnnuality(), r);
            writeWaterTime(bw, gr.getAnnuality(), r);
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
        final Object[] params = new Object[] {
                annuality,
                r.getMaxWaterlevel()
            };

        bw.write(maxWaterFormat.format(params));
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
            final Object[] params = new Object[] {
                    annuality,
                    levels.getKey(),
                    levels.getValue()
                };

            bw.write(timeWaterFormat.format(params));
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
        bw.write(boundingGeomInsert);
        bw.newLine();

        bw.write(boundingGeomUpdate);
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

        final Object[] params = new Object[] {
                t.getA().getX(),
                t.getA().getY(),
                zA,
                t.getB().getX(),
                t.getB().getY(),
                zB,
                t.getC().getX(),
                t.getC().getY(),
                zC,
                t.getA().getX(),
                t.getA().getY(),
                zA
            };

        return triangleWktFormat.format(params);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   line  t DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String breakingEdgeToWKT(final BELine line) {
        final Object[] params;
        final MessageFormat format;
        if (line.c == null) {
            params = new Object[] {
                    line.a.getX(),
                    line.a.getY(),
                    line.b.getX(),
                    line.b.getY()
                };
            format = beWkt2Format;
        } else {
            params = new Object[] {
                    line.a.getX(),
                    line.a.getY(),
                    line.b.getX(),
                    line.b.getY(),
                    line.c.getX(),
                    line.c.getY()
                };
            format = beWkt3Format;
        }

        return format.format(params);
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
