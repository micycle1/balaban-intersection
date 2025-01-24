package micycle.balaban;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SegmentTests {

	/**
	 * Test that potentially degenerate cases (intersections involving
	 * vertical/horizontal/parallel segments) are intersected correctly.
	 */
	@Test
	void testIntersection() {
		// Standard intersection
		Segment s1 = new Segment(0, 0, 10, 10);
		Segment s2 = new Segment(0, 10, 10, 0);
		Point intersection = s1.getIntersection(s2);
		assertEquals(5.0, intersection.x);
		assertEquals(5.0, intersection.y);

		// Vertical line intersection
		Segment vertical = new Segment(5, 0, 5, 10);
		Segment diagonal = new Segment(0, 5, 10, 5);
		intersection = vertical.getIntersection(diagonal);
		assertEquals(5.0, intersection.x);
		assertEquals(5.0, intersection.y);

		// Parallel vertical lines with endpoint touch
		Segment v1 = new Segment(5, 0, 5, 5);
		Segment v2 = new Segment(5, 5, 5, 10);
		intersection = v1.getIntersection(v2);
		assertEquals(5.0, intersection.x);
		assertEquals(5.0, intersection.y);

		// Parallel horizontal lines with endpoint touch
		Segment h1 = new Segment(0, 0, 5, 0);
		Segment h2 = new Segment(5, 0, 10, 0);
		intersection = h1.getIntersection(h2);
		assertEquals(5.0, intersection.x);
		assertEquals(0, intersection.y);

		// Diagonal and horizontal intersection
		Segment diagonal2 = new Segment(0, 0, 10, 10);
		Segment horizontal = new Segment(0, 5, 10, 5);
		intersection = diagonal2.getIntersection(horizontal);
		assertEquals(5.0, intersection.x);
		assertEquals(5.0, intersection.y);

		Segment verticalT = new Segment(5, 0, 5, 10);
		Segment horizontalT = new Segment(0, 0, 10, 0);
		intersection = verticalT.getIntersection(horizontalT);
		assertEquals(5.0, intersection.x);
		assertEquals(0, intersection.y);
	}

}
