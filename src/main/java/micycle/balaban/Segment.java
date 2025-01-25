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

	final double yIntercept, slope;

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

		yIntercept = (to.x * from.y - from.x * to.y) / (to.x - from.x); // infinity if vertical
		slope = (to.y - from.y) / (to.x - from.x); // infinity if vertical
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
	 * This method assumes the segments are intersecting. This method is only called
	 * when the segments are known to certainly intersect at a single point. Handles
	 * collinear vertical/horizontal segments
	 *
	 * @param s other segment
	 * @return point of intersection of the two segments
	 */
	public Point getIntersection(final Segment s) {
		if (isHorizontal() && s.isHorizontal()) {
			return from.x <= s.from.x ? s.from : from;
		}

		// Handle case where this segment is vertical
		if (isVertical()) {
			// If other segment is also vertical and shares x-coordinate
			if (s.isVertical() && from.x == s.from.x) {
				// Check if endpoints match exactly
				// can only report 1 intersection, even if segments fully overlap
				if (from.y == s.to.y || to.y == s.from.y) {
					return from.y <= s.from.y ? s.from : from;
				}
			}

			// Compute intersection using other segment's equation
			final double x = from.x;
			final double y = s.slope * x + s.yIntercept;
			return new Point(x, y);
		}

		// Handle case where other segment is vertical
		if (s.isVertical()) {
			final double x = s.from.x;
			final double y = slope * x + yIntercept;
			return new Point(x, y);
		}

		// Standard case for non-vertical segments
		final double x = (s.yIntercept - yIntercept) / (slope - s.slope);
		final double y = x * slope + yIntercept;
		return new Point(x, y);
	}

	public boolean isVertical() {
		return from.x == to.x;
	}

	public boolean isHorizontal() {
		return from.y == to.y;
	}

	public boolean isIntersecting(double b, double e, Segment other) {
		if (this.maxX() < other.minX() || other.maxX() < this.minX() || this.maxY() < other.minY() || other.maxY() < this.minY()) {
			return false;
		}

		// Handle vertical-vertical, horizontal-horizontal cases
		if (this.isVertical() && other.isVertical()) {
			return this.from.x == other.from.x && !(this.maxY() < other.minY() || other.maxY() < this.minY());
		}
		if (this.isHorizontal() && other.isHorizontal()) {
			return this.from.y == other.from.y && !(this.maxX() < other.minX() || other.maxX() < this.minX());
		}

		// Check line intersection
		if (this.isVertical() || other.isVertical()) {
			return handleVerticalIntersection(other);
		}
		if (this.isHorizontal() || other.isHorizontal()) {
			return handleHorizontalIntersection(other);
		}

		if (from.x > b) {
			b = from.x;
		}
		if (other.from.x > b) {
			b = other.from.x;
		}
		if (to.x < e) {
			e = to.x;
		}
		if (other.to.x < e) {
			e = other.to.x;
		}
		if (e < b) {
			return false;
		}
		final double ybs = other.getY(b), yb = getY(b), yes = other.getY(e), ye = getY(e);
		return ybs <= yb && yes >= ye || ybs >= yb && yes <= ye;
	}

	private boolean handleVerticalIntersection(Segment other) {
		Segment vertical = this.isVertical() ? this : other;
		Segment nonVertical = this.isVertical() ? other : this;

		double x = vertical.from.x;
		double y = nonVertical.from.y + (nonVertical.to.y - nonVertical.from.y) * (x - nonVertical.from.x) / (nonVertical.to.x - nonVertical.from.x);

		// Check if y is within both segments and x is within the non-vertical segment's
		// x-range
		return y >= vertical.minY() && y <= vertical.maxY() && x >= nonVertical.minX() && x <= nonVertical.maxX() && y >= nonVertical.minY()
				&& y <= nonVertical.maxY();
	}

	private boolean handleHorizontalIntersection(Segment other) {
		Segment horizontal = this.isHorizontal() ? this : other;
		Segment nonHorizontal = this.isHorizontal() ? other : this;

		double y = horizontal.from.y;
		double x = nonHorizontal.from.x
				+ (nonHorizontal.to.x - nonHorizontal.from.x) * (y - nonHorizontal.from.y) / (nonHorizontal.to.y - nonHorizontal.from.y);

		// Check if x is within both segments and y is within the non-horizontal
		// segment's y-range
		return x >= horizontal.minX() && x <= horizontal.maxX() && y >= nonHorizontal.minY() && y <= nonHorizontal.maxY() && x >= nonHorizontal.minX()
				&& x <= nonHorizontal.maxX();
	}

	boolean isSpanning(double b, double e) {
		return from.x <= b && e <= to.x;
	}

	public double getY(double x) {
		if (from.x == to.x) {
			// Vertical segment: return the lower y-coordinate
			return Math.min(from.y, to.y);
		}
		double t = (x - from.x) / (to.x - from.x);
		return from.y + t * (to.y - from.y);
	}

	public double minX() {
		return from.x; // given by sort on initialisation
//		return Math.min(from.x, to.x);
	}

	public double maxX() {
		return to.x; // given by sort on initialisation
//		return Math.max(from.x, to.x);
	}

	public double minY() {
		return Math.min(from.y, to.y);
	}

	public double maxY() {
		return Math.max(from.y, to.y);
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