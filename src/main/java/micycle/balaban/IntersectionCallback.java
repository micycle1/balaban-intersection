package micycle.balaban;

/**
 * Called by the solver when an intersection is found between two segments.
 * 
 * @author taras
 */
public interface IntersectionCallback {

	/**
	 * Called by the solver when an intersection is found between two segments.
	 * 
	 * @param a segment a
	 * @param b segment b
	 */
	public void intersects(Segment a, Segment b);

}
