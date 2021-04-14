/*
 * SolverBalabanTest.java
 */

package ogkg38;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
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
public class SolverBalabanTest {

    public SolverBalabanTest() {
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
     * Test of split method, of class SolverBalaban.
     */
    /*@Test
    public void testSplit() {
        System.out.println("split");
        double b = 0.0;
        double e = 0.0;
        Collection<Segment> l = null;
        Collection<Segment> q = null;
        Collection<Segment> l1 = null;
        SolverBalaban instance = null;
        instance.split(b, e, l, q, l1);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of findIntersectionSorted method, of class SolverBalaban.
     */
   /* @Test
    public void testFindIntersectionSorted() {
        System.out.println("findIntersectionSorted");
        double b = 0.0;
        double e = 10;
        double x = 5;
        Collection<Segment> d = null;
        Collection<Segment> s = null;
        SolverBalaban instance = null;
        instance.findIntersectionSorted(b, e, x, d, s);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of merge method, of class SolverBalaban.
     */
    @Test
    public void testMerge() {
        System.out.println("merge");
        for (int i = 0; i < 100; i++) {
            double x=Math.random()*10;
            double d=15;
            int cnt=100;
            Vector<Segment> d1=new Vector<Segment>();
            Collection<Segment> s1 = Segments.generate(cnt, 0, 10, d);
            d1.addAll(s1);
            SolverBalaban.sort(x, d1);
            if (!Segments.isSorted(x, d1)) {
                fail("genereate 1 fail");
            }
            Vector<Segment> d2=new Vector<Segment>();
            Collection<Segment> s2 = Segments.generate(cnt, 0, 10, d);
            d2.addAll(s2);
            SolverBalaban.sort(x, d2);
            if (!Segments.isSorted(x, d2)) {
                fail("genereate 2 fail");
            }

            Collection<Segment> result = SolverBalaban.merge(x, d1, d2);
            if (!Segments.isSorted(x, result)) {
                fail("compare fail. it:"+i);
            }

        }
    }

    @Test
    public void testIntersectingPairsIntermediate() {
        System.out.println("intersectingPairsIntermediate");
        System.out.println("cnt\topt\tunopt");
        for (int i = 0; i < 5; i++) {
            final double d=0, b=0, e=100;
            int cnt=200;
            final TreeSet<SegmentsPair> balabanSolution=new TreeSet<SegmentsPair>();
            IntersectionListener forc=new IntersectionListener() {
                public void startUpdate() {

                }

                public void say(Segment a, Segment b) {
                    if (!a.isIntersecting(-100, 200, b)) {
                        System.out.println("!");
                    }
                    balabanSolution.add(new SegmentsPair(a, b));
                }

                public void stopUpdate() {

                }
            };
            Collection<Segment> s1 = Segments.generateLong(cnt);

            SolverBalaban instance=new SolverBalaban(forc);
            instance.makeOptimization=true;
            instance.intersectingPairs(s1);

            final TreeSet<SegmentsPair> trivialSolution=new TreeSet<SegmentsPair>();
            for (Segment segment : s1) {
                for (Segment segment1 : s1) {
                    if (segment.compareTo(segment1)==0) {
                        continue;
                    }
                    if (segment.isIntersecting(b-d, e+d, segment1)) {
                        trivialSolution.add(new SegmentsPair(segment, segment1));
                    }
                }
            }
            /*System.out.println(tr+" "+dd[0]) ;
            if (tr!=dd[0]) {
                dd[0]=0;
                instance.searchInStrip(b, e, s1);
            }*/
            if (balabanSolution.size() != trivialSolution.size()) {
                instance.intersectingPairs(s1);
            }
            assertEquals(trivialSolution.size(), balabanSolution.size());
        }
    }

    /**
     * Test of searchInStrip method, of class SolverBalaban.
     */
    @Test
    public void testSearchInStrip() {
        System.out.println("searchInStrip");
        for (int i = 0; i < 100; i++) {
            double d=0, b=0, e=10;

            int cnt=200;
            final long dd[]={0};
            IntersectionListener forc=new IntersectionListener() {
                long cnt=0;
                public void startUpdate() {
                    
                }

                public void say(Segment a, Segment b) {
                    cnt++;
                    dd[0]++;
                }

                public void stopUpdate() {
                    
                }
            };
            SolverBalaban instance=new SolverBalaban(forc);
            Collection<Segment> s1 = Segments.generate(cnt, b, e, d);
            Vector<Segment> d1=new Vector<Segment>();
            d1.addAll(s1);
            d1=SolverBalaban.sort(b, d1);
            s1=instance.searchInStrip(b, e, d1);
            int tr=0;
            for (Segment segment : s1) {
                for (Segment segment1 : s1) {
                    if (segment.compareTo(segment1)==0) {
                        continue;
                    }
                    if (segment.isIntersecting(b, e, segment1)) {
                        tr++;
                    }
                }
            }
            tr/=2;
            /*System.out.println(tr+" "+dd[0]) ;
            if (tr!=dd[0]) {
                dd[0]=0;
                instance.searchInStrip(b, e, s1);
            }*/
            assertEquals(tr, dd[0]);
            if (!Segments.isSorted(e, s1)) {
                fail("must be sorted by e");
            }
        }
    }

}