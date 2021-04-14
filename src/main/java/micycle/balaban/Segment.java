package micycle.balaban;

/**
 * A segment is a line between two coordinates.
 * 
 * @author taras
 */
public class Segment {

	public final Point from, to;
	private int bs; // [optional] current value of SBv'(Loc(Dv', s))
	boolean group = false;

	public Segment(Point from, Point to) {
		if (from.compareTo(to) > 0) {
			this.from = to;
			this.to = from;
		} else {
			this.from = from;
			this.to = to;
		}
	}

	/**
	 * Compute the point of intersection between this and another segment.
	 * 
	 * @param s other segment
	 * @return
	 */
	public Point getIntersection(Segment s) {
		double x = getXofIntersection(s);
		return new Point(x, getY(x));
	}

	/**
	 * Computes the x coordinate of the intersection between this segement and the
	 * given segment. This method assumes the segments are intersecting.
	 * 
	 * @param s other intersecting segment
	 * @return
	 */
	public double getXofIntersection(Segment s) {
		double b = (to.x * from.y - from.x * to.y) / (to.x - from.x), k = (to.y - from.y) / (to.x - from.x);
		double bs = (s.to.x * s.from.y - s.from.x * s.to.y) / (s.to.x - s.from.x),
				ks = (s.to.y - s.from.y) / (s.to.x - s.from.x);
		return (b - bs) / (ks - k);
	}

	double getY(double x) {
//		if (x < from.x || x > to.x) {
//			throw new IllegalArgumentException("x=" + x + " not in segment" + toString());
//		}
		double b = (to.x * from.y - from.x * to.y) / (to.x - from.x), k = (to.y - from.y) / (to.x - from.x);
		return x * k + b;
	}

	boolean isIntersecting(double b, double e, Segment segment) {
		if (from.x > b) {
			b = from.x;
		}
		if (segment.from.x > b) {
			b = segment.from.x;
		}
		if (to.x < e) {
			e = to.x;
		}
		if (segment.to.x < e) {
			e = segment.to.x;
		}
		if (e < b) {
			return false;
		}
		double ybs = segment.getY(b), yb = getY(b), yes = segment.getY(e), ye = getY(e);
		return ybs <= yb && yes >= ye || ybs >= yb && yes <= ye;
	}

	boolean isSpanning(double b, double e) {
		return from.x <= b && e <= to.x;
	}

	@Override
	public String toString() {
		return "[" + from.toString() + ", " + to.toString() + "]";
	}

	/**
	 * Ignores coordinate order.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Segment) {
			Segment s = (Segment) obj;
			return ((s.from.equals(from) && s.to.equals(to)) || (s.from.equals(to) && s.to.equals(from)));
		}
		return this == obj;
	}

	/**
	 * Ignores coordinate order.
	 */
	@Override
	public int hashCode() {
		return from.hashCode() + to.hashCode(); // may reach int.max on large coord values? Xor instead?
	}
}