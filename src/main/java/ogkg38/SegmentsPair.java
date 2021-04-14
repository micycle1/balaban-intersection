package ogkg38;

/**
 *
 * @author taras
 */
class SegmentsPair implements Comparable<SegmentsPair> {

	Segment a, b;

	public SegmentsPair(Segment a, Segment b) {
		if (a.compareTo(b) > 0) {
			this.a = a;
			this.b = b;
		} else {
			this.a = b;
			this.b = a;
		}
	}

	@Override
	public int compareTo(SegmentsPair o) {
		int d = a.compareTo(o.a);
		if (d == 0) {
			return b.compareTo(o.b);
		} else {
			return d;
		}
	}

}
