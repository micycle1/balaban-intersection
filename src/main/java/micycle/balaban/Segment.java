package micycle.balaban;

/**
 * A segment is a line between two coordinates.
 * 
 * @author taras
 * @author Michael Carleton
 */
public class Segment {

	/** The left-most point */
	public final Point from;
	/** The right-most point */
	public final Point to;

	final double b, k;

	/**
	 * Creates a segment from two points.
	 * 
	 * @param from
	 * @param to
	 */
	public Segment(final Point from, final Point to) {
		if (from.compareTo(to) > 0) {
			this.from = to;
			this.to = from;
		} else {
			this.from = from;
			this.to = to;
		}

		b = (to.x * from.y - from.x * to.y) / (to.x - from.x);
		k = (to.y - from.y) / (to.x - from.x);
	}

	/**
	 * Creates a segment from two points given by their coordinates.
	 * 
	 * @param x1 x coordinate of point 1
	 * @param y1 y coordinate of point 1
	 * @param x2 x coordinate of point 2
	 * @param y2 y coordinate of point 2
	 */
	public Segment(final double x1, final double y1, final double x2, final double y2) {
		this(new Point(x1, y1), new Point(x2, y2));
	}

	/**
	 * Computes the point of intersection between this segment and another segment.
	 * This method assumes the segments are intersecting.
	 * 
	 * @param s other segment
	 * @return point of intersection of the two segments
	 */
	public Point getIntersection(final Segment s) {
		final double x = (b - s.b) / (s.k - k);
		final double y = x * k + b;
		return new Point(x, y);
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
		final double ybs = segment.getY(b), yb = getY(b), yes = segment.getY(e), ye = getY(e);
		return ybs <= yb && yes >= ye || ybs >= yb && yes <= ye;
	}

	boolean isSpanning(double b, double e) {
		return from.x <= b && e <= to.x;
	}

	double getY(double x) {
		return x * k + b;
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
		return from.hashCode() ^ to.hashCode();
	}
}