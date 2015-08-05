package de.cismet.cids.custom.wupp.geocpm;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.GeoCPMResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
     * 
     * @throws Exception
     */
    @Test
    public void testAccept() throws Exception {
        printCurrentTestName();
        
        OAB_FolderGeoCPMImportTransformer t = new OAB_FolderGeoCPMImportTransformer();
        
        final File a = new File(testFolder, "a");
        final File b = new File(testFolder, "b");
        final File proj = new File(testFolder, WuppGeoCPMConstants.IMPORT_INFO_FILENAME);
        final File z1 = new File(a, WuppGeoCPMConstants.PROJECT_INFO_FILENAME);
        final File z2 = new File(b, WuppGeoCPMConstants.PROJECT_INFO_FILENAME);
        final File at20 = new File(a, "T20");
        final File at30 = new File(a, "T30");
        final File bt30 = new File(b, "T30");
        final File bt100 = new File(b, "T100");
        final File at20Ein = new File(at20, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
        final File at30Ein = new File(at30, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
        final File bt30Ein = new File(bt30, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
        final File bt100Ein = new File(bt100, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
        final File at20Sub = new File(at20, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
        final File at30Sub = new File(at30, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
        final File bt30Sub = new File(bt30, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
        final File bt100Sub = new File(bt100, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
        final File at20Calc = new File(at20, WuppGeoCPMConstants.CALC_INFO_FILENAME);
        final File at30Calc = new File(at30, WuppGeoCPMConstants.CALC_INFO_FILENAME);
        final File bt30Calc = new File(bt30, WuppGeoCPMConstants.CALC_INFO_FILENAME);
        final File bt100Calc = new File(bt100, WuppGeoCPMConstants.CALC_INFO_FILENAME);
        final File at20O = new File(at20, "o");
        final File at30O = new File(at30, "o");
        final File bt30O = new File(bt30, "o");
        final File bt100O = new File(bt100, "o");
        final File at20OInfo = new File(at20O, WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
        final File at30OInfo = new File(at30O, WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
        final File bt30OInfo = new File(bt30O, WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
        final File bt100OInfo = new File(bt100O, WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
        final File at20OMax = new File(at20O, WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
        final File at30OMax = new File(at30O, WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
        final File bt30OMax = new File(bt30O, WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
        final File bt100OMax = new File(bt100O, WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
        final File at20ORE = new File(at20O, WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);
        final File at30ORE = new File(at30O, WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);
        final File bt30ORE = new File(bt30O, WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);
        final File bt100ORE = new File(bt100O, WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);
        
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
        assertTrue(at20Calc.createNewFile());
        assertTrue(at30Calc.createNewFile());
        assertTrue(bt30Calc.createNewFile());
        assertTrue(bt100Calc.createNewFile());
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
        
        assertTrue(t.accept(testFolder));
    }

    /**
     * Test of transform method, of class OAB_FolderGeoCPMImportTransformer.
     * 
     * @throws Exception
     */
    @Test
    public void testTransform() throws Exception {
        printCurrentTestName();
        
        final OAB_FolderGeoCPMImportTransformer t = new OAB_FolderGeoCPMImportTransformer();
        
        final File a = new File(testFolder, "a");
        final File b = new File(testFolder, "b");
        final File proj = new File(testFolder, WuppGeoCPMConstants.IMPORT_INFO_FILENAME);
        final File z1 = new File(a, WuppGeoCPMConstants.PROJECT_INFO_FILENAME);
        final File z2 = new File(b, WuppGeoCPMConstants.PROJECT_INFO_FILENAME);
        final File at20 = new File(a, "T20");
        final File at30 = new File(a, "T30");
        final File bt30 = new File(b, "T30");
        final File bt100 = new File(b, "T100");
        final File at20Ein = new File(at20, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
        final File at30Ein = new File(at30, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
        final File bt30Ein = new File(bt30, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
        final File bt100Ein = new File(bt100, WuppGeoCPMConstants.GEOCPM_EIN_FILENAME);
        final File at20Sub = new File(at20, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
        final File at30Sub = new File(at30, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
        final File bt30Sub = new File(bt30, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
        final File bt100Sub = new File(bt100, WuppGeoCPMConstants.GEOCPM_SUBINFO_AUS_FILENAME);
        final File at20Calc = new File(at20, WuppGeoCPMConstants.CALC_INFO_FILENAME);
        final File at30Calc = new File(at30, WuppGeoCPMConstants.CALC_INFO_FILENAME);
        final File bt30Calc = new File(bt30, WuppGeoCPMConstants.CALC_INFO_FILENAME);
        final File bt100Calc = new File(bt100, WuppGeoCPMConstants.CALC_INFO_FILENAME);
        final File at20O = new File(at20, "o");
        final File at30O = new File(at30, "o");
        final File bt30O = new File(bt30, "o");
        final File bt100O = new File(bt100, "o");
        final File at20OInfo = new File(at20O, WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
        final File at30OInfo = new File(at30O, WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
        final File bt30OInfo = new File(bt30O, WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
        final File bt100OInfo = new File(bt100O, WuppGeoCPMConstants.GEOCPM_INFO_AUS_FILENAME);
        final File at20OMax = new File(at20O, WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
        final File at30OMax = new File(at30O, WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
        final File bt30OMax = new File(bt30O, WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
        final File bt100OMax = new File(bt100O, WuppGeoCPMConstants.GEOCPM_MAX_AUS_FILENAME);
        final File at20ORE = new File(at20O, WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);
        final File at30ORE = new File(at30O, WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);
        final File bt30ORE = new File(bt30O, WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);
        final File bt100ORE = new File(bt100O, WuppGeoCPMConstants.RESULTSELEMENTS_AUS_FILENAME);
        
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
        assertTrue(at20Calc.createNewFile());
        assertTrue(at30Calc.createNewFile());
        assertTrue(bt30Calc.createNewFile());
        assertTrue(bt100Calc.createNewFile());
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
        bw.write(WuppGeoCPMConstants.IMPORT_INFO_CATCHMENT_NAME + "=cm1");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.IMPORT_INFO_CONTRACTOR + "=con1");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.IMPORT_INFO_DESC + "=desc1");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.IMPORT_INFO_NAME + "=name1");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.IMPORT_INFO_WMS_BASE_URL + "=bu1");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.IMPORT_INFO_STATE_DEM + "=state1");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.IMPORT_INFO_STATE_ALKIS + "=01.01.1970");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.IMPORT_INFO_STATE_VERDIS + "=31.01.1970");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(z1));
        bw.write(WuppGeoCPMConstants.PROJECT_INFO_DESC + "=d1");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.PROJECT_INFO_NAME + "=n1");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.PROJECT_INFO_TYPE + "=Ist");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(z2));
        bw.write(WuppGeoCPMConstants.PROJECT_INFO_DESC + "=d2");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.PROJECT_INFO_NAME + "=n2");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.PROJECT_INFO_TYPE + "=Sanierung");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(at20Calc));
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_STARTTIME + "=100");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_ENDTIME + "=200");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_NO_OF_STEPS + "=10");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(at30Calc));
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_STARTTIME + "=200");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_ENDTIME + "=300");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_NO_OF_STEPS + "=10");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(bt30Calc));
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_STARTTIME + "=300");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_ENDTIME + "=400");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_NO_OF_STEPS + "=10");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(bt100Calc));
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_STARTTIME + "=400");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_ENDTIME + "=500");
        bw.newLine();
        bw.write(WuppGeoCPMConstants.CALC_INFO_TS_NO_OF_STEPS + "=10");
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
        GeoCPMResult gr = itr.next();
        assertTrue(gr instanceof WuppGeoCPMResult);
        WuppGeoCPMResult r = (WuppGeoCPMResult)gr;
        assertEquals(r.getAnnuality(), 20);
        assertEquals(r.getGeocpmInfo().getAbsolutePath(), at20OInfo.getAbsolutePath());
        assertEquals(r.getGeocpmSubinfo().getAbsolutePath(), at20Sub.getAbsolutePath());
        assertEquals(r.getGeocpmMax().getAbsolutePath(), at20OMax.getAbsolutePath());
        assertEquals(r.getGeocpmResultElements().getAbsolutePath(), at20ORE.getAbsolutePath());
        assertEquals(r.getTsStartTime(), 100);
        assertEquals(r.getTsEndTime(), 200);
        assertEquals(r.getNoOSteps(), 10);
        gr = itr.next();
        assertTrue(gr instanceof WuppGeoCPMResult);
        r = (WuppGeoCPMResult)gr;
        assertEquals(r.getAnnuality(), 30);
        assertEquals(r.getGeocpmInfo().getAbsolutePath(), at30OInfo.getAbsolutePath());
        assertEquals(r.getGeocpmSubinfo().getAbsolutePath(), at30Sub.getAbsolutePath());
        assertEquals(r.getGeocpmMax().getAbsolutePath(), at30OMax.getAbsolutePath());
        assertEquals(r.getGeocpmResultElements().getAbsolutePath(), at30ORE.getAbsolutePath());
        assertEquals(r.getTsStartTime(), 200);
        assertEquals(r.getTsEndTime(), 300);
        assertEquals(r.getNoOSteps(), 10);
            
        assertTrue(g instanceof WuppGeoCPMProject);
        WuppGeoCPMProject w = (WuppGeoCPMProject)g;
        assertEquals(w.getCatchmentName(), "cm1");
        assertEquals(w.getContractor(), "con1");
        assertEquals(w.getOutputFolder().getAbsolutePath(), 
                new File(testFolder, WuppGeoCPMConstants.IMPORT_OUT_DIR).getAbsolutePath());
        assertEquals(w.getProjectDescription(), "desc1");
        assertEquals(w.getProjectName(), "name1");
        
        g = it.next();
        assertEquals(g.getDescription(), "d2");
        assertEquals(g.getGeocpmEin().getAbsolutePath(), bt100Ein.getAbsolutePath());
        assertEquals(g.getName(), "n2");
        
        assertTrue(g.getResults().size() == 2);
        itr = g.getResults().iterator();
        gr = itr.next();
        assertTrue(gr instanceof WuppGeoCPMResult);
        r = (WuppGeoCPMResult)gr;
        assertEquals(r.getAnnuality(), 100);
        assertEquals(r.getGeocpmInfo().getAbsolutePath(), bt100OInfo.getAbsolutePath());
        assertEquals(r.getGeocpmSubinfo().getAbsolutePath(), bt100Sub.getAbsolutePath());
        assertEquals(r.getGeocpmMax().getAbsolutePath(), bt100OMax.getAbsolutePath());
        assertEquals(r.getGeocpmResultElements().getAbsolutePath(), bt100ORE.getAbsolutePath());
        assertEquals(r.getTsStartTime(), 400);
        assertEquals(r.getTsEndTime(), 500);
        assertEquals(r.getNoOSteps(), 10);
        gr = itr.next();
        assertTrue(gr instanceof WuppGeoCPMResult);
        r = (WuppGeoCPMResult)gr;
        assertEquals(r.getAnnuality(), 30);
        assertEquals(r.getGeocpmInfo().getAbsolutePath(), bt30OInfo.getAbsolutePath());
        assertEquals(r.getGeocpmSubinfo().getAbsolutePath(), bt30Sub.getAbsolutePath());
        assertEquals(r.getGeocpmMax().getAbsolutePath(), bt30OMax.getAbsolutePath());
        assertEquals(r.getGeocpmResultElements().getAbsolutePath(), bt30ORE.getAbsolutePath());
        assertEquals(r.getTsStartTime(), 300);
        assertEquals(r.getTsEndTime(), 400);
        assertEquals(r.getNoOSteps(), 10);
            
        assertTrue(g instanceof WuppGeoCPMProject);
        w = (WuppGeoCPMProject)g;
        assertEquals(w.getCatchmentName(), "cm1");
        assertEquals(w.getContractor(), "con1");
        final File outDir = new File(testFolder, WuppGeoCPMConstants.IMPORT_OUT_DIR);
        assertEquals(w.getOutputFolder().getAbsolutePath(), outDir.getAbsolutePath());
        assertEquals(w.getProjectDescription(), "desc1");
        assertEquals(w.getProjectName(), "name1");
        
        final File projectSql = new File(outDir, "project.sql");
        assertTrue(projectSql.exists());
        
        BufferedReader br = new BufferedReader(new FileReader(projectSql));
        final String insert = br.readLine();
        assertNotNull(insert);
        assertNull(br.readLine());
        br.close();
        
        assertEquals(insert, "INSERT INTO oab_projekt (\"name\", beschreibung, auftragnehmer, gewaessereinzugsgebiet, stand_dgm, stand_alkis, stand_verdis) VALUES ("
        + "'name1', 'desc1', (SELECT id FROM oab_projekt_auftragnehmer WHERE \"name\" = 'con1'), (SELECT id FROM oab_gewaessereinzugsgebiet WHERE \"name\" = 'cm1'), 'state1', '1970-01-01', '1970-01-31');");
        
    }

}