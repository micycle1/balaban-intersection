package ogkg38;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author taras
 */
public class Solver {

	Segment[] segments;
	int i = 0;
	double x;

	SortedSet<Segment> tree = new TreeSet<Segment>(new Comparator<Segment>() {
		@Override
		public int compare(Segment o1, Segment o2) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	});

	public Solver(Segments data) {
		segments = data.toArray(new Segment[data.size()]);
	}

	void check() {

	}

	void addToTree(Segment otr) {

	}

	public boolean makeStep() {
		tree.add(segments[i]);
		i++;
		return i < segments.length;
	}

}
