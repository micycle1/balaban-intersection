/*
 * SegmentTest.java
 */

package ogkg38;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author taras
 */
public class SegmentTest {

    public SegmentTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of compareTo method, of class Segment.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Segment o = null;
        Segment instance = null;
        int expResult = 0;
        int result = instance.compareTo(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getY method, of class Segment.
     */
    @Test
    public void testGetY() {
        System.out.println("getY");        
        Segment instance = new Segment(new Point(0, 0), new Point(10, 5));
        assertEquals(2.5, instance.getY(5), 0.1);
        assertEquals(0, instance.getY(0), 0.1);
        assertEquals(5, instance.getY(10), 0.1);
    }

    /**
     * Test of isIntersecting method, of class Segment.
     */
    @Test
    public void testIsIntersecting() {
        System.out.println("isIntersecting");
        double b = 0.0;
        double e = 0.0;
        Segment segment = null;
        Segment instance = null;
        boolean expResult = false;
        boolean result = instance.isIntersecting(b, e, segment);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSpanning method, of class Segment.
     */
    @Test
    public void testIsSpanning() {
        System.out.println("isSpanning");
        double b = 0.0;
        double e = 0.0;
        Segment instance = null;
        boolean expResult = false;
        boolean result = instance.isSpanning(b, e);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}