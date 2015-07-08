package de.cismet.cids.custom.wupp.geocpm;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import de.cismet.geocpm.api.entity.Triangle;
import de.cismet.geocpm.api.transform.GeoCPMEinPointToMemoryTransformer;
import de.cismet.geocpm.api.transform.GeoCPMEinTriangleToMemoryTransformer;
import de.cismet.geocpm.api.transform.GeoCPMMaxToMemoryTransformer;
import de.cismet.geocpm.api.transform.GeoCPMResultElementsToMemoryTransformer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author martin.scholl@cismet.de
 * @version 1.0
 */
public class OAB_ZustandMassnahme_PostgisSQLTransformerNGTest {

    public OAB_ZustandMassnahme_PostgisSQLTransformerNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    public void printCurrentTestName() {
        System.out.println("TEST " + new Throwable().getStackTrace()[1].getMethodName());
    }

    /**
     * Test of accept method, of class OAB_ZustandMassnahme_PostgisSQLTransformer.
     */
    @Test
    public void testAccept() {
        printCurrentTestName();
        
        final OAB_ZustandMassnahme_PostgisSQLTransformer t = new OAB_ZustandMassnahme_PostgisSQLTransformer();
        
        assertFalse(t.accept(null));
        
        GeoCPMProject p = new GeoCPMProject();
        assertFalse(t.accept(p));
        
        p = new WuppGeoCPMProject();
        assertFalse(t.accept(p));
        
        p.setTriangles(new ArrayList<Triangle>(0));
        assertFalse(t.accept(p));
        
        p.setTriangles(Arrays.asList(new Triangle[] {new Triangle(0, null, null, null)}));
        assertFalse(t.accept(p));
        
        p.setResults(new ArrayList<GeoCPMResult>());
        assertFalse(t.accept(p));
        
        p.getResults().add(new GeoCPMResult(0));
        assertFalse(t.accept(p));

        ((WuppGeoCPMProject)p).setProjectName("test");
        assertTrue(t.accept(p));
    }

    /**
     * Test of transform method, of class OAB_ZustandMassnahme_PostgisSQLTransformer.
     */
    //@Test
    public void testTransform() throws Exception {
        printCurrentTestName();
        
        OAB_ZustandMassnahme_PostgisSQLTransformer t = new OAB_ZustandMassnahme_PostgisSQLTransformer();
        WuppGeoCPMProject p = new WuppGeoCPMProject();
        p.setType(Type.Ist);
        
        p.setName("zustname1");
        p.setDescription("zustdescription1");
        p.setProjectName("projectname1");
        p.setDescription("projectdescription1");
        
        InputStreamReader r = new InputStreamReader(
                getClass().getResourceAsStream("OAB_ZustandMassnahme_PostgisSQLTransformer_SimpleGeoCPM.ein"));
        File f1 = File.createTempFile("test", "geocpmtests");
        f1.deleteOnExit();
        
        int c;
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(f1));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        r = new InputStreamReader(
                getClass().getResourceAsStream("OAB_ZustandMassnahme_PostgisSQLTransformer_SimpleGeoCPMMax.aus"));
        File f2 = File.createTempFile("test", "geocpmtests");
        f2.deleteOnExit();
        
        o = new BufferedOutputStream(new FileOutputStream(f2));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        r = new InputStreamReader(
                getClass().getResourceAsStream("OAB_ZustandMassnahme_PostgisSQLTransformer_SimpleGeoCPMResultsElements.aus"));
        File f3 = File.createTempFile("test", "geocpmtests");
        f3.deleteOnExit();
        
        o = new BufferedOutputStream(new FileOutputStream(f3));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.flush();
        
        p.setGeocpmEin(f1);
        GeoCPMResult gr = new GeoCPMResult(1);
        gr.setGeocpmMax(f2);
        gr.setGeocpmResultElements(f3);
        p.setResults(Arrays.asList(gr));
        
        final GeoCPMEinPointToMemoryTransformer t1 = new GeoCPMEinPointToMemoryTransformer();
        final GeoCPMEinTriangleToMemoryTransformer t2 = new GeoCPMEinTriangleToMemoryTransformer();
        final GeoCPMMaxToMemoryTransformer t3 = new GeoCPMMaxToMemoryTransformer();
        final GeoCPMResultElementsToMemoryTransformer t4 = new GeoCPMResultElementsToMemoryTransformer();
        
        t1.transform(p);
        t2.transform(p);
        t3.transform(p);
        t4.transform(p);
        
        t.transform(p);
        assertNotNull(p.getZustandMassnahmeSqlFile());
        System.out.println(p.getZustandMassnahmeSqlFile());
    }

}