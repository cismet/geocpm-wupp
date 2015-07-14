package de.cismet.cids.custom.wupp.geocpm;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
public class OAB_FolderGeoCPMImportTransformerNGTest {

    public OAB_FolderGeoCPMImportTransformerNGTest() {
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
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        testFolder.delete();
    }

    public void printCurrentTestName() {
        System.out.println("TEST " + new Throwable().getStackTrace()[1].getMethodName());
    }

    /**
     * Test of accept method, of class OAB_FolderGeoCPMImportTransformer.
     */
    @Test
    public void testAccept() throws Exception {
        printCurrentTestName();
        
        OAB_FolderGeoCPMImportTransformer t = new OAB_FolderGeoCPMImportTransformer();
        
        assertTrue(new File(testFolder, "a").mkdir());
        assertTrue(new File(testFolder, "b").mkdir());
        assertTrue(new File(testFolder, "projekt.info").createNewFile());
        
        assertTrue(t.accept(testFolder));
    }

    /**
     * Test of transform method, of class OAB_FolderGeoCPMImportTransformer.
     */
    @Test
    public void testTransform() throws Exception {
        printCurrentTestName();
        
        final File f = new File("/Users/mscholl/projects/Wupp/SUDPLAN-GeoCPM-WuNDa/GeoCPM Struktur v1");
        
        final OAB_FolderGeoCPMImportTransformer t = new OAB_FolderGeoCPMImportTransformer();
        
        final File a = new File(testFolder, "a");
        final File b = new File(testFolder, "b");
        final File proj = new File(testFolder, "projekt.info");
        final File z1 = new File(a, "zm.info");
        final File z2 = new File(b, "zm.info");
        final File at20 = new File(a, "T20");
        final File at30 = new File(a, "T30");
        final File bt30 = new File(b, "T30");
        final File bt100 = new File(b, "T100");
        final File at20Ein = new File(at20, "GeoCPM.ein");
        final File at30Ein = new File(at30, "GeoCPM.ein");
        final File bt30Ein = new File(bt30, "GeoCPM.ein");
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
        
        assertTrue(a.mkdir());
        assertTrue(b.mkdir());
        assertTrue(proj.createNewFile());
        assertTrue(z1.createNewFile());
        assertTrue(z2.createNewFile());
        assertTrue(at20.mkdir());
        assertTrue(at30.mkdir());
        assertTrue(bt30.mkdir());
        assertTrue(bt100.mkdir());
        assertTrue(at20Ein.createNewFile());
        assertTrue(at30Ein.createNewFile());
        assertTrue(bt30Ein.createNewFile());
        assertTrue(bt100Ein.createNewFile());
        assertTrue(at20Sub.createNewFile());
        assertTrue(at30Sub.createNewFile());
        assertTrue(bt30Sub.createNewFile());
        assertTrue(bt100Sub.createNewFile());
        assertTrue(at20O.mkdir());
        assertTrue(at30O.mkdir());
        assertTrue(bt30O.mkdir());
        assertTrue(bt100O.mkdir());
        assertTrue(at20OInfo.createNewFile());
        assertTrue(at30OInfo.createNewFile());
        assertTrue(bt30OInfo.createNewFile());
        assertTrue(bt100OInfo.createNewFile());
        assertTrue(at20OMax.createNewFile());
        assertTrue(at30OMax.createNewFile());
        assertTrue(bt30OMax.createNewFile());
        assertTrue(bt100OMax.createNewFile());
        assertTrue(at20ORE.createNewFile());
        assertTrue(at30ORE.createNewFile());
        assertTrue(bt30ORE.createNewFile());
        assertTrue(bt100ORE.createNewFile());
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(proj));
        bw.write(OAB_FolderGeoCPMImportTransformer.IMPORT_INFO_CATCHMENT_NAME + "=cm1");
        bw.newLine();
        bw.write(OAB_FolderGeoCPMImportTransformer.IMPORT_INFO_CONTRACTOR + "=con1");
        bw.newLine();
        bw.write(OAB_FolderGeoCPMImportTransformer.IMPORT_INFO_DESC + "=desc1");
        bw.newLine();
        bw.write(OAB_FolderGeoCPMImportTransformer.IMPORT_INFO_NAME + "=name1");
        bw.newLine();
        bw.write(OAB_FolderGeoCPMImportTransformer.IMPORT_INFO_WMS_BASE_URL + "=bu1");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(z1));
        bw.write(OAB_FolderGeoCPMImportTransformer.PROJECT_INFO_DESC + "=d1");
        bw.newLine();
        bw.write(OAB_FolderGeoCPMImportTransformer.PROJECT_INFO_NAME + "=n1");
        bw.newLine();
        bw.write(OAB_FolderGeoCPMImportTransformer.PROJECT_INFO_TYPE + "=Ist");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(z2));
        bw.write(OAB_FolderGeoCPMImportTransformer.PROJECT_INFO_DESC + "=d2");
        bw.newLine();
        bw.write(OAB_FolderGeoCPMImportTransformer.PROJECT_INFO_NAME + "=n2");
        bw.newLine();
        bw.write(OAB_FolderGeoCPMImportTransformer.PROJECT_INFO_TYPE + "=Sanierung");
        bw.close();
        
        final Collection<GeoCPMProject> c = t.transform(testFolder);
        
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
                new File(testFolder, OAB_FolderGeoCPMImportTransformer.IMPORT_OUT_DIR).getAbsolutePath());
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
                new File(testFolder, OAB_FolderGeoCPMImportTransformer.IMPORT_OUT_DIR).getAbsolutePath());
        assertEquals(w.getProjectDescription(), "desc1");
        assertEquals(w.getProjectName(), "name1");
    }

}