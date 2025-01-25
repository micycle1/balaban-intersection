package micycle.balaban;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Balaban's <i>Intermediate Algorithm</i> for efficiently finding intersections
 * amongst line segments. Specified in "An optimal algorithm for finding
 * segments intersections" (1995).
 *
 * <p>
 * This algorithm solves the problem of finding all pairs of intersecting line
 * segments in a collection of segments. It is designed to be efficient, with a
 * time complexity of O(n log² n + k), where n is the number of segments and k
 * is the number of intersections.
 * </p>
 * <p>
 * This implementation correctly handles some commonly degenerate cases –
 * vertical segments and axis-aligned segments that share an endpoint.
 * <p>
 * The algorithm works by dividing the plane into vertical strips and processing
 * segments incrementally within these strips. It uses a divide-and-conquer
 * approach to recursively split the problem into smaller subproblems, ensuring
 * that intersections are detected efficiently and without redundant checks.
 * </p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 * <li><b>Vertical Strips</b>: The plane is divided into vertical strips defined
 * by the x-coordinates of the segment endpoints. Each strip is processed
 * independently, and intersections are only reported if they lie within the
 * current strip.</li>
 * <li><b>Divide and Conquer</b>: The algorithm recursively splits the problem
 * into smaller subproblems by dividing the segments into left and right subsets
 * based on their x-coordinates. This ensures that intersections are detected
 * efficiently.</li>
 * <li><b>Staircase Structure</b>: Within each strip, segments are organized
 * into a "staircase" structure, which allows for efficient intersection checks.
 * The staircase is updated dynamically as segments are added or removed.</li>
 * </ul>
 *
 * <h2>Limitations</h2>
 * <ul>
 * <li>The algorithm assumes that no two segments are overlapping (undefined
 * behaviour).</li>
 * <li>The algorithm assumes that no more than two segments share an endpoint
 * (in this case, duplicate intersections are often reported).</li>
 * </ul>
 *
 * @author Initial algorithm implementation by taras
 * @author Improvements and degeneracy handling by Michael Carleton
 */
public class BalabanSolver {

	private final IntersectionCallback listener;
	private EndPoint[] endPoints;

	public BalabanSolver(IntersectionCallback listener) {
		this.listener = listener;
	}

	/**
	 * Computes intersecting segment pairs for the given collection of line
	 * segments.
	 *
	 * <p>
	 * When a pair of intersecting segments is found, the callback provided during
	 * construction is called.
	 *
	 * @param segments collection of segments
	 */
	public void computeIntersections(Collection<Segment> segments) {
		endPoints = segmentsToEnds(segments);
		final ArrayList<Segment> Lr = new ArrayList<>();
		final ArrayList<Segment> Ir = new ArrayList<>();
		Lr.add(endPoints[0].segment);
		Ir.addAll(segments);
		Ir.remove(endPoints[0].segment);
		Ir.remove(endPoints[endPoints.length - 1].segment);
		treeSearch(Lr, Ir, 0, endPoints.length - 1);
	}

	private List<Segment> Rrrs;

	private List<Segment> treeSearch(Collection<Segment> Lv, Collection<Segment> Iv, int b, int e) {
		double bd = endPoints[b].point.x, ed = endPoints[e].point.x;
		if (e - b == 1) { // 1
			return searchInStrip(bd, ed, Lv);
		}
		List<Segment> Qv = new ArrayList<>(), Lls = new ArrayList<>(), Ils = new ArrayList<>(), Rls, Lrs, Irs = new ArrayList<>(),
				Rrs;
		split(bd, ed, Lv, Qv, Lls); // 2
		int cnt = findIntersectionSorted(bd, ed, bd, Qv, Lls); // 3
		findIntersectionUnsorted(bd, ed, Qv, Iv); // 10-11
		if (cnt > 0) {
			List<Segment> s = treeSearch(Lls, Iv, b, e);
			findIntersectionSorted(bd, ed, ed, Qv, Rrrs);
			return merge(ed, s, Qv);
		}
		int c = (b + e) / 2; // 4
		double cd = endPoints[c].point.x;
		for (Segment segment : Iv) { // 5
			if (segment.to.x < cd) {
				Ils.add(segment);
			}
			if (segment.from.x > cd) {
				Irs.add(segment);
			}
		}
		Rls = treeSearch(Lls, Ils, b, c); // 6
		Lrs = Rls; // 7
		if (endPoints[c].isLeft()) {
			int po = loc(cd, ed, Lrs, endPoints[c].segment);
			Lrs.add(po, endPoints[c].segment);
		} else {
			Lrs.remove(endPoints[c].segment);
		}
		Rrs = treeSearch(Lrs, Irs, c, e); // 8
		findIntersectionSorted(bd, ed, ed, Qv, Rrs); // 9 //addFilter

		Rrrs = Rrs;
		return merge(ed, Qv, Rrs); // 12
	}

	/**
	 * implementation of procedure Split([1] page 2) O(l.size())
	 *
	 * @param b  x position of strip start (inclusive)
	 * @param e  x position of strip end (inclusive)
	 * @param l  ArrayList to split. l is ordered by coordinate y of segments in
	 *           abscisssae b
	 * @param q  returned value. q is complete relative to l1
	 * @param l1 returned value.
	 */
	private static void split(double b, double e, Collection<Segment> l, Collection<Segment> q, Collection<Segment> l1) {
		q.clear();
		Segment lastQ = null;
		l1.clear();
		for (Segment segment : l) {
			if (!segment.isSpanning(b, e)) {
				l1.add(segment);
			} else if (lastQ == null) {
				q.add(segment);
				lastQ = segment;
			} else if (lastQ.isIntersecting(b, e, segment)) {
				l1.add(segment);
			} else {
				q.add(segment);
				lastQ = segment;
			}
		}
	}

	/**
	 * Find all intersections of segment seg with staircase(work, (b, e)), it knows
	 * that seg has points between i-1 and i-th stairs O(k+1), where k is number of
	 * intersections.
	 *
	 * @param b         x position of strip start (inclusive)
	 * @param e         x position of strip end (inclusive)
	 * @param staircase
	 * @param i
	 * @param seg
	 */
	private int findIntersectionSegmentStaircase(double b, double e, List<Segment> work, int i, Segment seg) {
		int cnt = 0;
		int h = i;
		while (h < work.size() && work.get(h).isIntersecting(b, e, seg)) {
			cnt++;
			listener.intersects(work.get(h), seg); // intersection callback
			h++;
		}
		int l = i - 1;
		while (l >= 0 && work.get(l).isIntersecting(b, e, seg)) {
			cnt++;
			listener.intersects(work.get(l), seg); // intersection callback
			l--;
		}
		return cnt;
	}

	/**
	 * return point of segment in (b, e). segment must cross (b, e)
	 *
	 * @param b       x position of strip start (inclusive)
	 * @param e       x position of strip end (inclusive)
	 * @param segment
	 * @return
	 */
	private static double getPoint(double b, double e, Segment segment) {
		return segment.from.x > b ? segment.from.x : b;
	}

	/**
	 * find number i of stair, where seg has a point between i-1 and i-th stairs of
	 * staircase(work, (b, e))
	 *
	 * @param b         x position of strip start (inclusive)
	 * @param e         x position of strip end (inclusive)
	 * @param staircase
	 * @param seg
	 * @return i
	 */
	private static int loc(double b, double e, List<Segment> staircase, Segment seg) {
		double x = getPoint(b, e, seg);
		int l = 0, h = staircase.size();
		while (h != l) {
			int c = (l + h) / 2;
			Segment stairSeg = staircase.get(c);
			double ySeg = seg.getY(x);
			double yStair = stairSeg.getY(x);
			if (ySeg < yStair) {
				h = c;
			} else {
				l = c + 1;
			}
		}
		return h;
	}

	/**
	 * O(s*log(d) + int(d, s))
	 *
	 * @param b begin of strip D=(d, (b,e))
	 * @param e end of strip D=(d, (b,e))
	 * @param d segments of strip D
	 * @param s segments
	 */
	private void findIntersectionUnsorted(double b, double e, List<Segment> d, Collection<Segment> s) {
		for (Segment segment : s) {
			int i = loc(b, e, d, segment);
			findIntersectionSegmentStaircase(b, e, d, i, segment);
		}
	}

	/**
	 * O(d + s + int(d, s))
	 *
	 * @param b begin of strip D=(d, (b,e))
	 * @param e end of strip D=(d, (b,e))
	 * @param d segments of strip D
	 * @param s segments, sorted by < in x, x in [b, e]
	 * @return number of itnersections?
	 */
	private int findIntersectionSorted(double b, double e, double x, List<Segment> d, List<Segment> s) {
		if (s.isEmpty()) {
			return 0;
		}
		int cnt = 0;
		Iterator<Segment> itS = s.iterator();
		Segment last = itS.next();
		for (int i = 0; i < d.size(); i++) {
			Segment segment = d.get(i);
			while (segment.getY(x) >= last.getY(x)) {
				cnt += findIntersectionSegmentStaircase(b, e, d, i, last);
				if (!itS.hasNext()) {
					return cnt;
				}
				last = itS.next();
			}
		}
		while (true) {
			cnt += findIntersectionSegmentStaircase(b, e, d, d.size(), last);
			if (!itS.hasNext()) {
				return cnt;
			}
			last = itS.next();
		}
	}

	/**
	 *
	 * @param x  abscissae to sort by y
	 * @param s1 sorted in point x ArrayList
	 * @param s2 sorted in point x ArrayList
	 * @return s1 union s2 in point x ArrayList
	 */
	private static List<Segment> merge(double x, List<Segment> s1, List<Segment> s2) {
		if (s1.isEmpty()) {
			return s2;
		}
		if (s2.isEmpty()) {
			return s1;
		}

		List<Segment> ret = new ArrayList<>();
		Iterator<Segment> it1 = s1.iterator(), it2 = s2.iterator();
		Segment seg1 = it1.next(), seg2 = it2.next();

		while (seg1 != null || seg2 != null) {
			if (seg1 == null) {
				ret.add(seg2);
				while (it2.hasNext()) {
					ret.add(it2.next());
				}
				seg2 = null;
			} else if (seg2 == null) {
				ret.add(seg1);
				while (it1.hasNext()) {
					ret.add(it1.next());
				}
				seg1 = null;
			} else if (seg1.getY(x) < seg2.getY(x)) {
				ret.add(seg1);
				if (it1.hasNext()) {
					seg1 = it1.next();
				} else {
					seg1 = null;
				}
			} else {
				ret.add(seg2);
				if (it2.hasNext()) {
					seg2 = it2.next();
				} else {
					seg2 = null;
				}
			}
		}
		return ret;
	}

	/**
	 * Every intersection from l spans (b, e), without this algorithm hangs up,
	 * because elements, that doesn't span (e, b) always stay in l1, and l1 never
	 * become empty works O(n+k), n=l.size, k=count of intersections
	 *
	 * @param b x position of strip start (inclusive)
	 * @param e x position of strip end (inclusive)
	 * @param l strip to find intersections, sorted by ordinate in abscissae b
	 * @return - r resulting right strip
	 */
	private List<Segment> searchInStrip(double b, double e, Collection<Segment> l) {
		List<Segment> q = new ArrayList<>(), l1 = new ArrayList<>();
		split(b, e, l, q, l1);
		if (l1.isEmpty()) {
			return q;
		}
		findIntersectionSorted(b, e, b, q, l1);
		List<Segment> r1 = searchInStrip(b, e, l1);
		return merge(e, q, r1); // q is sorted by b and not intersecting,
		// so sorted by e. r1 is sorted by e as result of merge(x)
	}

	private static EndPoint[] segmentsToEnds(Collection<Segment> s) {
		EndPoint[] ret = new EndPoint[s.size() * 2];
		int i = 0;
		for (Segment segment : s) {
			ret[i++] = new EndPoint(segment, segment.from);
			ret[i++] = new EndPoint(segment, segment.to);
		}
		Arrays.sort(ret); // Ensure endpoints are sorted
		return ret;
	}

	/**
	 * Identifies degenerate segments in the given segment collection, returning a
	 * set of those identified. The input collection is not mutated.
	 *
	 * @param segments
	 * @return a set of degenerate segments found in the input collection
	 */
	public Set<Segment> findDegenerateSegments(Collection<Segment> segments) {
		Set<Double> seen = new HashSet<>();
		Set<Segment> degenerate = new HashSet<>();

		for (Segment segment : segments) {
			if (Math.abs(segment.from.x - segment.to.x) < 0.00000000001) {
				degenerate.add(segment);
			}
			if (!seen.add(segment.from.x) | !seen.add(segment.to.x)) {
				degenerate.add(segment);
			}
		}
		return degenerate;
	}

	private static class EndPoint implements Comparable<EndPoint> {
		final Segment segment;
		final Point point;

		EndPoint(Segment segment, Point endPoint) {
			this.segment = segment;
			this.point = endPoint;
		}

		boolean isLeft() {
			if (segment.from.x < segment.to.x) {
				return point.equals(segment.from);
			} else if (segment.from.x > segment.to.x) {
				return point.equals(segment.to);
			} else {
				// Vertical segment: left endpoint is the lower one
				return (segment.from.y <= segment.to.y) ? point.equals(segment.from) : point.equals(segment.to);
			}
		}

		@Override
		public int compareTo(EndPoint o) {
			// Compare by x first
			if (this.point.x < o.point.x) {
				return -1;
			}
			if (this.point.x > o.point.x) {
				return 1;
			}

			// Same x: left endpoints come before right endpoints
			if (this.isLeft() && !o.isLeft()) {
				return -1;
			}
			if (!this.isLeft() && o.isLeft()) {
				return 1;
			}

			// Both left or both right, compare y
			if (this.isLeft()) {
				// Left endpoints: order by increasing y
				return Double.compare(this.point.y, o.point.y);
			} else {
				// Right endpoints: order by decreasing y
				return Double.compare(o.point.y, this.point.y);
			}
		}
	}

}
