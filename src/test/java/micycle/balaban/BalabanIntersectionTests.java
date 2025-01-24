package micycle.balaban;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import valenpe7.bentley_ottmann.BentleyOttmann;

public class BalabanIntersectionTests {

	/**
	 * Test intersection where a segment is vertical. This is usually a degenerate
	 * case in other implementations.
	 */
	@Test
	void testVertical() {
		// cross arrangement
		Point p1 = new Point(0, 0);
		Point p2 = new Point(10, 0);
		Point p3 = new Point(5, 5);
		Point p4 = new Point(5, -5);

		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p3, p4);

		List<Segment> segments = Arrays.asList(s1, s2);
		List<Point> intersections = new ArrayList<>();
		BalabanSolver bs = new BalabanSolver((a, b) -> {
			intersections.add(a.getIntersection(b));
		});
		bs.computeIntersections(segments);

		assertEquals(1, intersections.size());

		Point intersection = intersections.get(0);
		assertEquals(5, intersection.x); // x-coordinate should be 5
		assertEquals(0, intersection.y); // y-coordinate should be 0
	}

	/**
	 * Test intersection where two line segment endpoints have the same
	 * x-coordinate. This is usually a degenerate case in other implementations.
	 */
	@Test
	void testVerticalSharedEndpoints() {
		// Create two parallel horizontal lines offset by y=5
		Point p1 = new Point(0, 0); // First horizontal line (y=0)
		Point p2 = new Point(10, 0);
		Point p3 = new Point(0, 5); // Second horizontal line (y=5)
		Point p4 = new Point(10, 5);

		// Create a vertical line at x=5
		Point p5 = new Point(5, -5); // Vertical line (x=5)
		Point p6 = new Point(5, 10);

		Segment s1 = new Segment(p1, p2); // First horizontal segment
		Segment s2 = new Segment(p3, p4); // Second horizontal segment
		Segment s3 = new Segment(p5, p6); // Vertical segment

		List<Segment> segments = Arrays.asList(s1, s2, s3);
		List<Point> intersections = new ArrayList<>();
		BalabanSolver bs = new BalabanSolver((a, b) -> {
			intersections.add(a.getIntersection(b));
		});
		bs.computeIntersections(segments);

		// Assert that there are 2 intersections
		assertEquals(2, intersections.size());
		assertTrue(intersections.contains(new Point(5, 0)));
		assertTrue(intersections.contains(new Point(5, 5)));
	}

	@Test
	@Disabled // undefined behaviour
	void testOverlap() {
		Point p1 = new Point(0, 0); // First horizontal line (y=0)
		Point p2 = new Point(10, 0);

		// Create a vertical line at x=5
		Point p5 = new Point(-50, -5); // Vertical line (x=-50)
		Point p6 = new Point(-50, 10);

		Segment h = new Segment(p1, p2); // First horizontal segment
		Segment v = new Segment(p5, p6); // Vertical segment

		List<Segment> segments = Arrays.asList(v, v, h, h);
		List<Point> intersections = new ArrayList<>();
		BalabanSolver bs = new BalabanSolver((a, b) -> {
			intersections.add(a.getIntersection(b));
		});
		bs.computeIntersections(segments);

		// Assert that there are 2 intersections
		assertEquals(2, intersections.size());
	}

	@Test
	void testLargeCollectionIntersection() {
		Random r = new Random(1337);

		Collection<Segment> segmentsBalaban = new ArrayList<>();
		List<valenpe7.bentley_ottmann.Segment> segmentsBo = new ArrayList<>();
		for (int i = 0; i < 1234; i++) {
			double a = r.nextDouble();
			double b = r.nextDouble();
			double c = r.nextDouble();
			double d = r.nextDouble();
			segmentsBalaban.add(new Segment(a, b, c, d));
			segmentsBo.add(new valenpe7.bentley_ottmann.Segment(new valenpe7.bentley_ottmann.Point(a, b), new valenpe7.bentley_ottmann.Point(c, d)));
		}

		List<Point> pointsBalaban = new ArrayList<>();
		BalabanSolver balabanSolver = new BalabanSolver((a, b) -> {
			// will be called by the solver for each intersecting pair
			pointsBalaban.add(a.getIntersection(b));
		});
		long startTimeBalaban = System.nanoTime();
		balabanSolver.computeIntersections(segmentsBalaban);
		long endTimeBalaban = System.nanoTime();
		long durationBalaban = endTimeBalaban - startTimeBalaban;
		System.out.println("BalabanSolver took: " + durationBalaban / 1_000_000 + " ms");

		long startTimeBO = System.nanoTime();
		BentleyOttmann bo = new BentleyOttmann(segmentsBo);
		bo.find_intersections();
		long endTimeBO = System.nanoTime();
		long durationBO = endTimeBO - startTimeBO;
		System.out.println("BentleyOttmann took: " + durationBO / 1_000_000 + " ms");
		List<valenpe7.bentley_ottmann.Point> pointsBo = bo.get_intersections();

		System.out.println("Found " + pointsBalaban.size() + " intersections.");
		assertEquals(pointsBo.size(), pointsBalaban.size());

		// now test intersection point equality
		pointsBalaban.sort((p1, p2) -> {
			int cmp = Double.compare(p1.x, p2.x);
			if (cmp == 0) {
				cmp = Double.compare(p1.y, p2.y);
			}
			return cmp;
		});

		pointsBo.sort((p1, p2) -> {
			int cmp = Double.compare(p1.get_x_coord(), p2.get_x_coord());
			if (cmp == 0) {
				cmp = Double.compare(p1.get_x_coord(), p2.get_x_coord());
			}
			return cmp;
		});

		for (int i = 0; i < pointsBalaban.size(); i++) {
			Point pBalaban = pointsBalaban.get(i);
			valenpe7.bentley_ottmann.Point pBo = pointsBo.get(i);

			assertEquals(pBalaban.x, pBo.get_x_coord(), 1e-9);
			assertEquals(pBalaban.y, pBo.get_y_coord(), 1e-9);
		}

	}

	/**
	 * Test whether triangle endpoint pairs that touch are counted as intersections.
	 */
	@Test
	void testTriangle() {
		Point p1 = new Point(0, 0);
		Point p2 = new Point(4, 0);
		Point p3 = new Point(2, 3);

		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p2, p3);
		Segment s3 = new Segment(p3, p1);

		List<Segment> segments = Arrays.asList(s1, s2, s3);

		int[] i = { 0 };
		BalabanSolver bs = new BalabanSolver((a, b) -> {
			assertTrue(a.getIntersection(b).equals(p1) || a.getIntersection(b).equals(p2) || a.getIntersection(b).equals(p3));
			i[0]++;
		});
		bs.computeIntersections(segments);

		assertEquals(3, i[0], "Triangle should have 3 intersections (at endpoints).");
	}

	@Test
	void testSquare() {
		// Define the four points of the square
		Point p1 = new Point(0, 0);
		Point p2 = new Point(4, 0);
		Point p3 = new Point(4, 4);
		Point p4 = new Point(0, 4);

		// Create the segments connecting the points
		Segment s1 = new Segment(p1, p2);
		Segment s2 = new Segment(p2, p3);
		Segment s3 = new Segment(p3, p4);
		Segment s4 = new Segment(p4, p1);

		// List of segments forming the square
		List<Segment> segments = Arrays.asList(s1, s2, s3, s4);

		// Counter to track the number of intersections
		int[] i = { 0 };

		// Create the BalabanSolver and define the intersection check
		BalabanSolver bs = new BalabanSolver((a, b) -> {
			assertTrue(
					a.getIntersection(b).equals(p1) || a.getIntersection(b).equals(p2) || a.getIntersection(b).equals(p3) || a.getIntersection(b).equals(p4));
			i[0]++;
		});

		// Compute the intersections
		bs.computeIntersections(segments);

		// Verify that there are 4 intersections (at the vertices of the square)
		assertEquals(4, i[0], "Square should have 4 intersections (at endpoints).");
	}

	/**
	 * >2 segments terminating at the same point. THIS IS AN UNHANDLED DEGENERATE
	 * CASE!
	 */
	@Test
	@Disabled
	void testEndpointTripletSameCoords() {
		// Create three segments that share a common endpoint at (5, 5)
		Point p1 = new Point(5, 5); // Common endpoint
		Point p2 = new Point(0, 0); // First segment endpoint
		Point p3 = new Point(10, 10); // Second segment endpoint
		Point p4 = new Point(5, 0); // Third segment endpoint

		// Create three segments sharing the common endpoint (5, 5)
		Segment s1 = new Segment(p1, p2); // Segment from (5, 5) to (0, 0)
		Segment s2 = new Segment(p1, p3); // Segment from (5, 5) to (10, 10)
		Segment s3 = new Segment(p1, p4); // Segment from (5, 5) to (5, 0)

		List<Segment> segments = Arrays.asList(s1, s2, s3);
		List<Point> intersections = new ArrayList<>();
		BalabanSolver bs = new BalabanSolver((a, b) -> {
			intersections.add(a.getIntersection(b));
		});
		bs.computeIntersections(segments);

		// Assert that there are no additional intersections besides the shared endpoint
		assertEquals(3, intersections.size()); // NOTE actually equals 4

		// Verify that the intersection point is the shared endpoint (5, 5)
		assertTrue(intersections.contains(new Point(5, 5)));
	}

	/**
	 * Test grid of axis-aligned segments.
	 */
	@Test
	void testGridIntersections() {
		int n = 31; // Number of vertical segments
		int m = 13; // Number of horizontal segments
		List<Segment> segments = createGridSegments(n, m);

		List<Point> intersections = new ArrayList<>();
		BalabanSolver bs = new BalabanSolver((a, b) -> {
			intersections.add(a.getIntersection(b));
		});
		bs.computeIntersections(segments);

		// Assert that the number of intersections is m * n
		assertEquals(m * n, intersections.size());
	}

	private static List<Segment> createGridSegments(int n, int m) {
		List<Segment> segments = new ArrayList<>();

		// Spacing between segments to avoid endpoint intersections
		double verticalSpacing = 1.0 / (n + 1);
		double horizontalSpacing = 1.0 / (m + 1);

		// Create vertical segments
		for (int i = 1; i <= n; i++) {
			double x = i * verticalSpacing;
			Point start = new Point(x, 0);
			Point end = new Point(x, 1); // Vertical segments span the full height
			segments.add(new Segment(start, end));
		}

		// Create horizontal segments
		for (int j = 1; j <= m; j++) {
			double y = j * horizontalSpacing;
			Point start = new Point(0, y);
			Point end = new Point(1, y); // Horizontal segments span the full width
			segments.add(new Segment(start, end));
		}

		return segments;
	}

}
