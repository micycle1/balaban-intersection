package micycle.balaban;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author taras
 */
public class Time {

	public static void main(String argv[]) {

		int x = 0;

		System.out.println("cnt\topt\tunopt");
		for (int i = 29; i < 30; i++) {
			int cnt = 100 * i * 2 + 100;
			final Set<Segment> balabanSolution = new HashSet<Segment>();
			IntersectionCallback forc = new IntersectionCallback() {
				@Override
				public void intersects(Segment a, Segment b) {
//					if (!a.isIntersecting(-100, 200, b)) {
//						System.out.println("!");
//					}
//					balabanSolution.add(a);
//					x++;
				}
			};
			Collection<Segment> s1 = generateLong(cnt);

			BalabanSolver instance = new BalabanSolver(forc);
			long time = new Date().getTime();
			instance.coef = 0.2;
			instance.useOptimization = true;
			instance.intersectingPairs(s1);
			long time2 = new Date().getTime();
			instance.useOptimization = false;
			instance.intersectingPairs(s1);
			long time3 = new Date().getTime();
			double pr = (double) (time3 - time2) / (time2 - time);
			System.out.println(
					cnt + "\t" + (time2 - time) + "\t" + (time3 - time2) + "\t" + pr + "\t" + balabanSolution.size());

		}
	}

	/**
	 * Generate random "long" segments.
	 * 
	 * @param count
	 * @return
	 */
	static List<Segment> generateLong(int count) {
		ArrayList<Segment> s = new ArrayList();
		for (int i = 0; i < count; i++) {
			Point from = new Point(Math.random() * 100, Math.random() * 100),
					to = new Point(Math.random() * 95, Math.random() * 95);
			s.add(new Segment(from, to));
		}
		return s;
	}

}
