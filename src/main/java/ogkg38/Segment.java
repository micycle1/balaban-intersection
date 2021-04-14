/*
 * Segment.java
 */

package ogkg38;

/**
 *
 * @author taras
 */
public class Segment implements Comparable<Segment>{
        Point from, to;
        int bs; //[optional] current value of SBv'(Loc(Dv', s))

        public Segment(Point from, Point to) {
            if (from.compareTo(to)>0) {
                this.from = to;
                this.to = from;
            } else {
                this.from = from;
                this.to = to;
            }
        }
        public int compareTo(Segment o) {
            if (from.compareTo(o.from)!=0) {
                return from.compareTo(o.from);
            }
            return to.compareTo(o.to);
        }
        public double getY(double x) {
            if (x < from.x || x > to.x) {
                throw new IllegalArgumentException("x="+x+" not in segment"+toString());
            }
            double b=(to.x*from.y-from.x*to.y)/(to.x-from.x),
                    k=(to.y-from.y)/(to.x-from.x);
            return x*k+b;
        }
        public double getXofIntersection(Segment s) {
            double b=(to.x*from.y-from.x*to.y)/(to.x-from.x),
                    k=(to.y-from.y)/(to.x-from.x);
            double bs=(s.to.x*s.from.y-s.from.x*s.to.y)/(s.to.x-s.from.x),
                    ks=(s.to.y-s.from.y)/(s.to.x-s.from.x);
            return (b-bs)/(ks-k);
        }
        public Point getIntersection(Segment s) {
            double x=getXofIntersection(s);
            return new Point(x, getY(x));
        }
        public boolean isIntersecting(double b, double e, Segment segment) {
            if (from.x>b)  {
                b=from.x;
            }
            if (segment.from.x>b) {
                b=segment.from.x;
            }
            if (to.x<e) {
              e=to.x;
            }
            if (segment.to.x<e) {
                e=segment.to.x;
            }
            if (e<b) {
                return false;
            }
            double ybs=segment.getY(b),
                   yb=getY(b),
                   yes=segment.getY(e),
                   ye=getY(e);
            return (ybs<=yb && yes>=ye) || (ybs>=yb && yes<=ye);
        }

        public boolean isSpanning(double b, double e) {
            return from.x<=b && e<=to.x;
        }

        @Override
        public String toString() {
            return "["+from.toString()+";"+to.toString()+"]";
        }

    }