/*
 * Points.java
 */
package ogkg38;

import java.util.Vector;

/**
 *
 * @author taras
 */
public class Point implements Comparable<Point>, Cloneable {

    @Override
    public String toString() {
        return "("+(int)x+","+(int)y+")";
    }

    double x;
    double y;

    public Point(Point point) {
        x = point.x;
        y = point.y;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int compareTo(Point o) {
        if (x != o.x) {
            if (x < o.x) {
                return -1;
            } else {
                return 1;
            }
        }
        if (y != o.y) {
            if (y < o.y) {
                return -1;
            } else {
                return 1;
            }
        }
        return 0;
    }
}
