/*
 * SolverBalaban.java
 */

package ogkg38;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author taras
 */
public class SolverBalaban {
    Segments segments;
    EndPoint[] endPoints;
    IntersectionListener listener;

    public static Vector<Segment> sort(final double x, Vector<Segment> s) {
        Collections.sort(s, new Comparator<Segment>() {
            public int compare(Segment o1, Segment o2) {
                double y1=o1.getY(x),
                        y2=o2.getY(x);
                if (y1==y2) {
                    return 0;
                }
                if (y1<y2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return s;
    }

    /**
     * implementation of procedure Split([1] page 2)
     * O(l.size())
     * @param b - begin of strip
     * @param e - ent of strip
     * @param l - Vector to split. l is ordered by coordinate y of segments in abscisssae b
     * @param q - returned value. q is complete relative to l1
     * @param l1 - returned value.
     */
    public static void split(double b, double e,
            Collection<Segment> l,
            Collection<Segment> q,
            Collection<Segment> l1) {
       q.clear();
       Segment lastQ=null;
       l1.clear();
        for (Segment segment : l) {
            if (!segment.isSpanning(b, e)) {
                l1.add(segment);
            } else if (lastQ==null) {
                q.add(segment);
                lastQ=segment;
            } else if (lastQ.isIntersecting(b, e, segment)) {
                l1.add(segment);
            } else {
                q.add(segment);
                lastQ=segment;
            }
        }
    }


    /**
     * Find all intersections of segment seg with staircase(work, (b, e)),
     * it knows that seg has points between i-1 and i-th stairs
     * O(k+1), k is number of intersections
     * @param b
     * @param e
     * @param staircase
     * @param i
     * @param seg
     */
    private void findIntersectionSegmentStaircase(double b, double e, TreeSet<Segment> work, int i, Segment seg) {
        for (Segment segment : work.tailSet(seg, false)) {
            if (!segment.isIntersecting(b, e, seg)) {
                break;
            }
            listener.say(segment, seg);
        }
        for (Segment segment : work.headSet(seg, false).descendingSet()) {
            if (!segment.isIntersecting(b, e, seg)) {
                break;
            }
            listener.say(segment, seg);
        }

    }

    /**
     * Find all intersections of segment seg with staircase(work, (b, e)),
     * it knows that seg has points between i-1 and i-th stairs
     * O(k+1), k is number of intersections
     * @param b
     * @param e
     * @param staircase
     * @param i
     * @param seg
     */
    private int findIntersectionSegmentStaircase(double b, double e, Vector<Segment> work, int i, Segment seg) {
        int cnt=0;
        int h=i;
        while (h<work.size() && work.elementAt(h).isIntersecting(b, e, seg)) {
            cnt++;
            listener.say(work.elementAt(h), seg);
            h++;
        }
        int l=i-1;
        while (l>=0 && work.elementAt(l).isIntersecting(b, e, seg)) {
            cnt++;
            listener.say(work.elementAt(l), seg);
            l--;
        }
        return cnt;
    }

    /**
     * return point of segment in (b, e). segment must cross (b, e)
     * @param b
     * @param e
     * @param segment
     * @return
     */
    private double getPoint(double b, double e, Segment segment) {        
        return segment.from.x>b?segment.from.x:b;
    }

    /**
     * find number i of stair, where seg has a point between i-1 and i-th stairs of staircase(work, (b, e))
     * @param b
     * @param e
     * @param staircase
     * @param seg
     * @return i
     */
    private int loc(double b, double e, Vector<Segment> staircase, Segment seg) {
        double x=getPoint(b, e, seg);
        int l=0, h=staircase.size();
        while (h!=l) {
            int c=(l+h)/2;
            if (seg.getY(x) < staircase.elementAt(c).getY(x)) {
                h=c;
            } else {
                l=c+1;
            }
        }
        return h;
    }


    /**
     * O(s*log(d) + int(d, s))
     * @param b begin of strip D=(d, (b,e))
     * @param e end of strip D=(d, (b,e))
     * @param d segments of strip D
     * @param s segments
     */
    public void findIntersectionUnsorted(double b, double e, Vector<Segment> d, Collection<Segment> s) {
        for (Segment segment : s) {
            int i=loc(b, e, d, segment);
            findIntersectionSegmentStaircase(b, e, d, i, segment);
        }
    }

    private static final int METHOD=1;
    /**
     * O(d + s + int(d, s))
     * @param b begin of strip D=(d, (b,e))
     * @param e end of strip D=(d, (b,e))
     * @param d segments of strip D
     * @param s segments, sorted by < in x, x in [b, e]
     */
    public int findIntersectionSorted(double b, double e, double x, Vector<Segment> d, Vector<Segment> s) {
        if (s.isEmpty()) {
            return 0;
        }
        int cnt=0;
        Iterator<Segment> itS=s.iterator();
        Segment last=itS.next();
        for (int i = 0; i < d.size(); i++) {
            Segment segment =d.elementAt(i);
            while(segment.getY(x)>=last.getY(x)) {
                cnt+=findIntersectionSegmentStaircase(b, e, d, i, last);
                if (!itS.hasNext()) {
                    if (METHOD==0) {
                        listener.stopUpdate();
                    }
                    return cnt;
                }
                last=itS.next();
            }
        }
        while (true) {
            cnt+=findIntersectionSegmentStaircase(b, e, d, d.size(), last);
            if (!itS.hasNext()) {
                return cnt;
            }
            last=itS.next();
        }
    }
    /**
     *
     * @param x abscissae to sort by y
     * @param s1 sorted in point x Vector
     * @param s2 sorted in point x Vector
     * @return s1 union s2 in point x Vector
     */
    public static Vector<Segment> merge(double x, Vector<Segment> s1, Vector<Segment> s2) {
        if (s1.isEmpty()) {
            return s2;
        }
        if (s2.isEmpty()) {
            return s1;
        }
        Vector<Segment> ret=new Vector<Segment>();
        Iterator<Segment> it1=s1.iterator(), it2=s2.iterator();
        Segment seg1=it1.next(), seg2=it2.next();
        while (seg1!=null || seg2!=null) {
            if (seg1==null) {
                ret.add(seg2);
                while (it2.hasNext()) {
                    ret.add(it2.next());
                }
                seg2=null;
            } else if (seg2==null) {
                ret.add(seg1);
                while (it1.hasNext()) {
                    ret.add(it1.next());
                }
                seg1=null;
            } else if (seg1.getY(x)<seg2.getY(x)) {
                ret.add(seg1);
                if (it1.hasNext()) {
                    seg1=it1.next();
                } else {
                    seg1=null;
                }
            } else {
                ret.add(seg2);
                if (it2.hasNext()) {
                    seg2=it2.next();
                } else {
                    seg2=null;
                }
            }
        }
        return ret;
    }

    /**
     * every x from l spans (b, e), without this algorithm hangs up,
     * because elements, that doesn't span (e, b) always stay in l1,
     * and l1 never become empty
     * works O(n+k), n=l.size, k=count of intersections
     *
     * @param b begin of strip
     * @param e end of strip
     * @param l strip to find intersections, sorted by ordinate in abscissae b
     * @return - r resulting right strip
     */
    public Vector<Segment> searchInStrip(double b, double e, Collection<Segment> l) {
        Vector<Segment> q=new Vector<Segment>(), l1=new Vector<Segment>();
        split(b, e, l, q, l1);
        if (l1.isEmpty()) {
            return q;
        }
        findIntersectionSorted(b, e, b, q, l1);
        Vector<Segment> r1=searchInStrip(b, e, l1);
        return merge(e, q, r1); // q is sorted by b and not intersecting,
                    //so sorted by e. r1 is sorted by e as result of merge(x)
    }

    public static class EndPoint implements Comparable<EndPoint>{
        Segment segment;
        double x;
        public boolean isLeft() {
            return x==segment.from.x;
        }

        public EndPoint(Segment segment, Point endPoint) {
            this.segment = segment;
            this.x = endPoint.x;
        }

        public int compareTo(EndPoint o) {
            if (x < o.x) {
                return -1;
            } else if (x == o.x) {
                return 0;
            } else {
                return 1;
            }
        }
    }


    public static EndPoint[] segmentsToEnds(Collection<Segment> s) {
        EndPoint[] ret=new EndPoint[s.size()*2];
        int i=0;
        for (Segment segment : s) {
            ret[i]=new EndPoint(segment, segment.from);
            i++;
            ret[i]=new EndPoint(segment, segment.to);
            i++;
        }
        Arrays.sort(ret);
        return ret;
    }


    /**
     *
     * @param Lv
     * @param Iv
     * @param b
     * @param e
     * @return Rv
     */
    static Vector<Segment> Rrrs;
    public boolean makeOptimization=false;
    double coef=1;
    public Vector<Segment> treeSearch(Collection<Segment> Lv, Collection<Segment> Iv, int b, int e) {
        double bd=endPoints[b].x,
                ed=endPoints[e].x;
        if (e - b == 1) {                               //1
            return searchInStrip(bd, ed, Lv);
        }
        Vector<Segment> Qv=new Vector<Segment>(),
                Lls=new Vector<Segment>(),
                Ils=new Vector<Segment>(), Rls,
                Lrs, Irs=new Vector<Segment>(), Rrs;
        split(bd, ed, Lv, Qv, Lls);                     //2
        int cnt=findIntersectionSorted(bd, ed, bd, Qv, Lls);    //3
        findIntersectionUnsorted(bd, ed, Qv, Iv);       //10-11
        if (makeOptimization && cnt>Lls.size()*0.6) {
            Vector<Segment> s=treeSearch(Lls, Iv, b, e);
            findIntersectionSorted(bd, ed, ed, Qv, Rrrs);
            return merge(ed, s, Qv);
        }
        int c=(b+e)/2;                                  //4
        double cd=endPoints[c].x;
        for (Segment segment : Iv) {                    //5
            if (segment.to.x<cd) {
                Ils.add(segment);
            }
            if (segment.from.x>cd){
                Irs.add(segment);
            }
        }
        Rls = treeSearch(Lls, Ils, b, c);               //6
        Lrs = Rls;                                      //7
        if (endPoints[c].isLeft()) {
            int po=loc(cd, ed, Lrs, endPoints[c].segment);
            Lrs.add(po, endPoints[c].segment);
        } else {
            Lrs.remove(endPoints[c].segment);
        }
        Rrs=treeSearch(Lrs, Irs, c, e);                 //8
        findIntersectionSorted(bd, ed, ed, Qv, Rrs);    //9     //addFilter
        if (METHOD==1) {
            listener.stopUpdate();
        }
        Rrrs=Rrs;
        return merge(ed, Qv, Rrs);                      //12
    }
    
    public void intersectingPairs(Collection<Segment> segments) {
        listener.startUpdate();
        endPoints = segmentsToEnds(segments);
        Vector<Segment> Lr=new Vector<Segment>(),
                Ir=new Vector<Segment>();
        Lr.add(endPoints[0].segment);
        Ir.addAll(segments);
        Ir.remove(endPoints[0].segment);
        Ir.remove(endPoints[endPoints.length-1].segment);
        treeSearch(Lr, Ir, 0, endPoints.length-1);
    }

    public SolverBalaban(IntersectionListener listener) {
        this.listener = listener;
    }
    
}
