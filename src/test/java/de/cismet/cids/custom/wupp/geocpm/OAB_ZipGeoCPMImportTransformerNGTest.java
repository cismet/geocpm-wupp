package de.cismet.cids.custom.wupp.geocpm;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
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
public class OAB_ZipGeoCPMImportTransformerNGTest {

    public OAB_ZipGeoCPMImportTransformerNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private File testFolder;
    
    @BeforeMethod
    public void setUpMethod() throws Exception {
        testFolder = new File(System.getProperty("java.io.tmpdir"), "oab_folder_test_" + System.currentTimeMillis());
        testFolder.mkdir();
        
        BufferedInputStream r = new BufferedInputStream(getClass().getResourceAsStream("oab_folder_test.zip"));
        File zip = new File(testFolder, "oab_folder_test.zip");
        
        int c;
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(zip));
        while((c = r.read()) >= 0) {
            o.write(c);
        }
        o.close();
        r.close();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        testFolder.delete();
    }

    public void printCurrentTestName() {
        System.out.println("TEST " + new Throwable().getStackTrace()[1].getMethodName());
    }

    /**
     * Test of accept method, of class OAB_ZipGeoCPMImportTransformer.
     */
    @Test
    public void testAccept() {
        printCurrentTestName();
        
        final OAB_ZipGeoCPMImportTransformer t = new OAB_ZipGeoCPMImportTransformer();
        final File zip = new File(testFolder, "oab_folder_test.zip"); 
        assertTrue(t.accept(zip));
    }

    /**
     * Test of transform method, of class OAB_ZipGeoCPMImportTransformer.
     */
    @Test
    public void testTransform() {
        printCurrentTestName();
        
        final OAB_ZipGeoCPMImportTransformer t = new OAB_ZipGeoCPMImportTransformer();
        final File zip = new File(testFolder, "oab_folder_test.zip"); 
        final File tmpFolder = new File(System.getProperty("java.io.tmpdir"));
        
        final Collection<GeoCPMProject> c = t.transform(zip);
        
        final File[] extracts = tmpFolder.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("geocpm_extract_");
            }
        });
        
        File extractDir = null;
        for(final File f : extracts) {
            if(extractDir == null || extractDir.lastModified() < f.lastModified()) {
                extractDir = f;
            }
        }
        
        assertNotNull(extractDir);
        
        final File a = new File(extractDir, "a");
        final File b = new File(extractDir, "b");
        final File at20 = new File(a, "T20");
        final File at30 = new File(a, "T30");
        final File bt30 = new File(b, "T30");
        final File bt100 = new File(b, "T100");
        final File at20Ein = new File(at20, "GeoCPM.ein");
        final File bt100Ein = new File(bt100, "GeoCPM.ein");
        final File at20Sub = new File(at20, "GeoCPMSubInfo.aus");
        final File at30Sub = new File(at30, "GeoCPMSubInfo.aus");
        final File bt30Sub = new File(bt30, "GeoCPMSubInfo.aus");
        final File bt100Sub = new File(bt100, "GeoCPMSubInfo.aus");
        final File at20O = new File(at20, "o");
        final File at30O = new File(at30, "o");
        final File bt30O = new File(bt30, "o");
        final File bt100O = new File(bt100, "o");
        final File at20OInfo = new File(at20O, "GeoCPMInfo.aus");
        final File at30OInfo = new File(at30O, "GeoCPMInfo.aus");
        final File bt30OInfo = new File(bt30O, "GeoCPMInfo.aus");
        final File bt100OInfo = new File(bt100O, "GeoCPMInfo.aus");
        final File at20OMax = new File(at20O, "GeoCPMMax.aus");
        final File at30OMax = new File(at30O, "GeoCPMMax.aus");
        final File bt30OMax = new File(bt30O, "GeoCPMMax.aus");
        final File bt100OMax = new File(bt100O, "GeoCPMMax.aus");
        final File at20ORE = new File(at20O, "ResultsElements.aus");
        final File at30ORE = new File(at30O, "ResultsElements.aus");
        final File bt30ORE = new File(bt30O, "ResultsElements.aus");
        final File bt100ORE = new File(bt100O, "ResultsElements.aus");
        
        assertTrue(c.size() == 2);
        final Iterator<GeoCPMProject> it = c.iterator();
        GeoCPMProject g = it.next();
        assertEquals(g.getDescription(), "d1");
        assertEquals(g.getGeocpmEin().getAbsolutePath(), at20Ein.getAbsolutePath());
        assertEquals(g.getName(), "n1");
        
        assertTrue(g.getResults().size() == 2);
        Iterator<GeoCPMResult> itr = g.getResults().iterator();
        GeoCPMResult r = itr.next();
        assertEquals(r.getAnnuality(), 20);
        assertEquals(r.getGeocpmInfo().getAbsolutePath(), at20OInfo.getAbsolutePath());
        assertEquals(r.getGeocpmSubinfo().getAbsolutePath(), at20Sub.getAbsolutePath());
        assertEquals(r.getGeocpmMax().getAbsolutePath(), at20OMax.getAbsolutePath());
        assertEquals(r.getGeocpmResultElements().getAbsolutePath(), at20ORE.getAbsolutePath());
        r = itr.next();
        assertEquals(r.getAnnuality(), 30);
        assertEquals(r.getGeocpmInfo().getAbsolutePath(), at30OInfo.getAbsolutePath());
        assertEquals(r.getGeocpmSubinfo().getAbsolutePath(), at30Sub.getAbsolutePath());
        assertEquals(r.getGeocpmMax().getAbsolutePath(), at30OMax.getAbsolutePath());
        assertEquals(r.getGeocpmResultElements().getAbsolutePath(), at30ORE.getAbsolutePath());
            
        assertTrue(g instanceof WuppGeoCPMProject);
        WuppGeoCPMProject w = (WuppGeoCPMProject)g;
        assertEquals(w.getCatchmentName(), "cm1");
        assertEquals(w.getContractor(), "con1");
        assertEquals(w.getOutputFolder().getAbsolutePath(), 
                new File(extractDir, WuppGeoCPMConstants.IMPORT_OUT_DIR).getAbsolutePath());
        assertEquals(w.getProjectDescription(), "desc1");
        assertEquals(w.getProjectName(), "name1");
        
        g = it.next();
        assertEquals(g.getDescription(), "d2");
        assertEquals(g.getGeocpmEin().getAbsolutePath(), bt100Ein.getAbsolutePath());
        assertEquals(g.getName(), "n2");
        
        assertTrue(g.getResults().size() == 2);
        itr = g.getResults().iterator();
        r = itr.next();
        assertEquals(r.getAnnuality(), 100);
        assertEquals(r.getGeocpmInfo().getAbsolutePath(), bt100OInfo.getAbsolutePath());
        assertEquals(r.getGeocpmSubinfo().getAbsolutePath(), bt100Sub.getAbsolutePath());
        assertEquals(r.getGeocpmMax().getAbsolutePath(), bt100OMax.getAbsolutePath());
        assertEquals(r.getGeocpmResultElements().getAbsolutePath(), bt100ORE.getAbsolutePath());
        r = itr.next();
        assertEquals(r.getAnnuality(), 30);
        assertEquals(r.getGeocpmInfo().getAbsolutePath(), bt30OInfo.getAbsolutePath());
        assertEquals(r.getGeocpmSubinfo().getAbsolutePath(), bt30Sub.getAbsolutePath());
        assertEquals(r.getGeocpmMax().getAbsolutePath(), bt30OMax.getAbsolutePath());
        assertEquals(r.getGeocpmResultElements().getAbsolutePath(), bt30ORE.getAbsolutePath());
            
        assertTrue(g instanceof WuppGeoCPMProject);
        w = (WuppGeoCPMProject)g;
        assertEquals(w.getCatchmentName(), "cm1");
        assertEquals(w.getContractor(), "con1");
        assertEquals(w.getOutputFolder().getAbsolutePath(), 
                new File(extractDir, WuppGeoCPMConstants.IMPORT_OUT_DIR).getAbsolutePath());
        assertEquals(w.getProjectDescription(), "desc1");
        assertEquals(w.getProjectName(), "name1");
    }

}