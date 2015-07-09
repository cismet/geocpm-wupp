package de.cismet.cids.custom.wupp.geocpm;

import de.cismet.geocpm.api.GeoCPMProject;
import de.cismet.geocpm.api.transform.GeoCPMEinPointToMemoryTransformer;
import de.cismet.geocpm.api.transform.GeoCPMEinTriangleToMemoryTransformer;
import de.cismet.geocpm.api.transform.GeoCPMMaxToMemoryTransformer;
import de.cismet.geocpm.api.transform.GeoCPMResultElementsToMemoryTransformer;
import java.io.File;
import java.util.Collection;
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
    //@Test
    public void testTransform() {
        printCurrentTestName();
    }

}