package micycle.balaban;

/**
 *
 * @author taras
 * @author Michael Carleton
 */
public class Point implements Comparable<Point> {

	public double x;
	public double y;

	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public Point(final Point point) {
		this(point.x, point.y);
	}

	/**
	 * Compares two points based on x position. If there's a tie, use y position
	 * instead.
	 */
	@Override
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

	@Override
	public String toString() {
		return "(" + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ")";
	}

	@Override
	public int hashCode() {
		return (int) (x * 92821 + y);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point o = (Point) obj;
			return x == o.x && y == o.y;
		}
		return this == obj;
	}
}
