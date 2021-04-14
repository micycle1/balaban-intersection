/*
 * Data.java
 */

package ogkg38;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author taras
 */
public class Segments extends TreeSet<Segment>{
    public static Collection<Segment> generate() {
        for (int i = 0; i < 100; i++) {
            double d=40, b=0, e=10;

            int cnt=4;
            final TreeSet<SegmentsPair> balabanSolution=new TreeSet<SegmentsPair>();
            IntersectionListener forc=new IntersectionListener() {
                public void startUpdate() {

                }

                public void say(Segment a, Segment b) {
                    if (!a.isIntersecting(-10, 20, b)) {
                        System.out.println("!");
                    }
                    balabanSolution.add(new SegmentsPair(a, b));
                }

                public void stopUpdate() {

                }
            };
            Collection<Segment> s1 = Segments.generate(cnt, b, e, d);

            SolverBalaban instance=new SolverBalaban(forc);
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
                return s1;
            }            
        }
        return null;
    }
    public static Segments generate(int count, double d) {
        Segments s=new Segments();
        for (int i = 0; i < count; i++) {
            Point from=new Point(Math.random()*95, Math.random()*95),
                    to=new Point(Math.random()*d+1+from.x, (Math.random()*d*2-d)+from.y);

            s.add(new Segment(from, to));
        }
        return s;
    }
    public static Segments generateLong(int count) {
        Segments s=new Segments();
        for (int i = 0; i < count; i++) {
            Point from=new Point(Math.random()*100, Math.random()*100),
                    to=new Point(Math.random()*95, Math.random()*95);
            s.add(new Segment(from, to));
        }
        return s;
    }
    public static Segments generate(int count, double b, double e, double d) {
        Segments s=new Segments();
        for (int i = 0; i < count; i++) {
            Point from=new Point(b-Math.random()*d, Math.random()*95),
                    to=new Point(e+Math.random()*d, Math.random()*95);

            s.add(new Segment(from, to));
        }
        return s;
    }
    public static boolean isSorted(double x, Collection<Segment> collection)  {
        Segment[] l=collection.toArray(new Segment[collection.size()]);
        for (int i = 1; i < l.length; i++) {
            if (l[i]==null) {
                System.out.println("bad");
            }
            if (l[i-1].getY(x)>l[i].getY(x)) {
                return false;
            }
        }
        return true;
    }
}
