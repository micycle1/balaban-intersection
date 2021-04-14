/*
 * Time.java
 */

package ogkg38;

import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

/**
 *
 * @author taras
 */
public class Time {
    public static void main(String argv[]) {
        System.out.println("cnt\topt\tunopt");
        for (int i = 0; i < 30; i++) {
            int cnt=100*i+100;
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
            instance.coef=0.6;
            long time=new Date().getTime();
            instance.makeOptimization=true;
            instance.intersectingPairs(s1);
            long time2=new Date().getTime();
            instance.makeOptimization=false;
            instance.intersectingPairs(s1);
            long time3=new Date().getTime();
            double pr=(double)(time3-time2)/(time2-time);
            System.out.println(cnt+"\t"+(time2-time)+"\t"+(time3-time2)+
                    "\t"+pr+"\t"+balabanSolution.size()) ;

        }
    }
}
