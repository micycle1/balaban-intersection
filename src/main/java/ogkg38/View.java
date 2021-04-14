/*
 * SegmentView.java
 */

package ogkg38;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author taras
 */
public class View extends Component{
    private Collection<Segment> segments=new Segments();
    Queue<Set<Point>> toAdd;
    Vector<Point> points=new Vector<Point>();
    Set<Point> recentlyAdded=new TreeSet<Point>();
    double xSpline=0;
    SolverBalaban solver=new SolverBalaban(new IntersectionListener() {
        TreeSet<Point> cur;
        synchronized public void startUpdate() {
            cur=new TreeSet();
        }

        synchronized public void say(Segment a, Segment b) {
            cur.add(a.getIntersection(b));            
        }

        synchronized public void stopUpdate() {
            if (cur.isEmpty()) {
                return;
            }
            toAdd.add(cur);
            cur=new TreeSet<Point>();
        }
    });
    private boolean editMode=true;
    public void solveCurrent() {
        if (toAdd!=null) {
            return;
        }
        toAdd=new LinkedList<Set<Point>>();
        solver.intersectingPairs(segments);
    }
    public boolean getEditMode() {
        return editMode;
    }
    public void switchEditMode() {
        editMode=!editMode;
        reset();
    }
    public void reset() {
        if (play!=null && play.isAlive()) {
            play.interrupt();
        }
        toAdd=null;
        points=new Vector<Point>();
        recentlyAdded=new TreeSet<Point>();
        repaint();
    }
    synchronized public boolean step() {
        solveCurrent();

        recentlyAdded=toAdd.poll();
        if (recentlyAdded==null) {
            repaint();
            return false;
        }
        points.addAll(recentlyAdded);

        repaint();
        return true;
    }
    private boolean showOnlyNewPoints;

    public void setShowOnlyNewPoints(boolean showOnlyNewPoints) {
        this.showOnlyNewPoints = showOnlyNewPoints;
        repaint();
    }
    Thread play;
    public void play() {
        solveCurrent();
        play=new Thread() {

            @Override
            public void run() {
                try {
                    while (step()) {
                        sleep(100);
                    }
                } catch (Exception e) {                    
                }
            }

        };
        play.start();
    }
    public void showResult() {
         while (step()) {
             
         }
    }
    Point firstEditedPoint=null,
            viewRectFrom=new Point(0, 0), viewRectTo=new Point(100, 100),
            oldViewRectFrom, oldViewRectTo;

    boolean isMovingCanvasNow=false;
    int canvasMoveX, canvasMoveY;
    private double getX(int x) {
        return (double)x/getWidth()*(viewRectTo.x-viewRectFrom.x)+viewRectFrom.x;
    }
    private double getY(int y) {
        return (double)(getHeight()-y)/getHeight()*(viewRectTo.y-viewRectFrom.y)+viewRectFrom.y;
    }
    private int getX(double x) {
        return (int)((x-viewRectFrom.x)/(viewRectTo.x-viewRectFrom.x)*getWidth());
    }
    private int getY(double y) {
        return getHeight()-(int)((y-viewRectFrom.y)/(viewRectTo.y-viewRectFrom.y)*getHeight());
    }
    private void checkSegments() {
        final double eps=0.1;
        for (Segment segment : segments) {
            if (Math.abs(segment.from.x-segment.to.x)<eps) {
                segment.to.x=segment.from.x+eps;
            }
        }
    }
    public View() {
        addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==1 && e.getButton()==MouseEvent.BUTTON1 && editMode) {
                    Point p=new Point(getX(e.getX()), getY(e.getY()));
                    if (firstEditedPoint==null) {
                        firstEditedPoint=p;
                    } else {
                        segments.add(new Segment(firstEditedPoint, p));
                        checkSegments();
                        firstEditedPoint=null;
                    }
                    repaint();
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.getButton()==MouseEvent.BUTTON3) {
                    isMovingCanvasNow=true;
                    canvasMoveX=e.getX();
                    canvasMoveY=e.getY();
                    oldViewRectFrom=new Point(viewRectFrom);
                    oldViewRectTo=new Point(viewRectTo);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.getButton()==MouseEvent.BUTTON3) {
                    isMovingCanvasNow=false;
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent e) {
                 if (isMovingCanvasNow) {
                    double dx=(canvasMoveX-e.getX())/(double)getWidth()*(viewRectTo.x-viewRectFrom.x),
                            dy=(canvasMoveY-e.getY())/(double)getHeight()*(viewRectTo.y-viewRectFrom.y);
                    viewRectFrom.x=oldViewRectFrom.x+dx;
                    viewRectTo.x=oldViewRectTo.x+dx;
                    viewRectFrom.y=oldViewRectFrom.y-dy;
                    viewRectTo.y=oldViewRectTo.y-dy;
                    repaint();
                }
            }

            public void mouseMoved(MouseEvent e) {

            }
        });
        addMouseWheelListener(new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                double percent=e.getWheelRotation()<1?0.9:1.1;
                double x=getX(e.getX()),
                        y=getY(e.getY());
                double  dx=viewRectTo.x-viewRectFrom.x,
                        dy=viewRectTo.y-viewRectFrom.y,
                        cx=(x-viewRectFrom.x),
                        cy=(y-viewRectFrom.y);
                viewRectFrom.x=x-cx*percent;
                viewRectTo.x=x+(dx-cx)*percent;
                viewRectFrom.y=y-cy*percent;
                viewRectTo.y=y+(dy-cy)*percent;
                repaint();
            }
        });
    }
    public void clear() {
        segments.clear();
        repaint();
    }
    public Collection<Segment> getSegments() {
        return segments;
    }
    public void setSegments(Collection<Segment> segments) {
        this.segments=segments;
        checkSegments();
        repaint();
    }
    @Override
    synchronized public void paint(Graphics g) {
        int cx1=0,
                cx2=g.getClipBounds().width,
                cy1=0,
                cy2=g.getClipBounds().height;
        if (editMode) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRect(cx1, cy1, cx2, cy2);

        g.setColor(Color.BLACK);
        for (Segment s : segments) {
            g.drawLine(getX(s.from.x),
                    getY(s.from.y),
                    getX(s.to.x),
                    getY(s.to.y));
            //g.drawString(s.from.toString(), getX(s.from.x), getY(s.from.y));
        }
        if (firstEditedPoint!=null) {
            g.setColor(Color.BLUE);
            g.fillOval(getX(firstEditedPoint.x), getY(firstEditedPoint.y), 2, 2);
        }
        if (!showOnlyNewPoints || recentlyAdded==null) {
            g.setColor(Color.BLUE);
            for (Point point : points) {
                g.fillOval(getX(point.x)-3, getY(point.y)-3, 5, 5);
            }
        }
        g.setColor(Color.GREEN);
        if (recentlyAdded!=null) {
            for (Point point : recentlyAdded) {
                g.fillOval(getX(point.x)-4, getY(point.y)-4, 7, 7);
            }
        }
    }

}
