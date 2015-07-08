/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.transform.GeoCPMProjectTransformer;
import de.cismet.geocpm.api.transform.TransformException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class SQLScriptLineByLineExecutor implements GeoCPMProjectTransformer {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean accept(final GeoCPMProject obj) {
        return (obj instanceof WuppGeoCPMProject)
                    && (((WuppGeoCPMProject)obj).getZustandMassnahmeSqlFile() != null)
                    && ((WuppGeoCPMProject)obj).getZustandMassnahmeSqlFile().canRead()
                    && (((WuppGeoCPMProject)obj).getDbDriver() != null)
                    && (((WuppGeoCPMProject)obj).getDbConn() != null)
                    && (((WuppGeoCPMProject)obj).getDbUser() != null)
                    && (((WuppGeoCPMProject)obj).getDbPass() != null);
    }

    @Override
    public GeoCPMProject transform(final GeoCPMProject obj) {
        // we rely on the framework to call accept
        final WuppGeoCPMProject proj = (WuppGeoCPMProject)obj;

        try {
            Class.forName(proj.getDbDriver());
        } catch (final ClassNotFoundException ex) {
            throw new TransformException("cannot load jdbc driver", ex); // NOI18N
        }

        try(final Connection con = DriverManager.getConnection(proj.getDbConn(), proj.getDbUser(), proj.getDbPass());
                    final BufferedReader br = new BufferedReader(new FileReader(proj.getZustandMassnahmeSqlFile()));
            ) {
            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    try(final Statement stmt = con.createStatement()) {
                        stmt.executeUpdate(line);
                    }
                }
            }

            con.commit();
        } catch (final SQLException | IOException ex) {
            throw new TransformException("cannot process sql", ex); // NOI18N
        }

        return obj;
    }
}
