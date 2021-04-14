/*
 * IntersectionListener.java
 */

package ogkg38;

/**
 *
 * @author taras
 */
public interface IntersectionListener {
    public void startUpdate();
    public void say(Segment a, Segment b);
    public void stopUpdate();
}
