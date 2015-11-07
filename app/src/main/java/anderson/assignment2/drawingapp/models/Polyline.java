package anderson.assignment2.drawingapp.models;

/**
 * Created by anderson on 10/4/15.
 */
public class Polyline {
    private Point[] points;
    private int color;

    public Polyline(int size, int color){
        points = new Point[size];
        this.color = color;
    }

    public Polyline(Point[] points, int color){
        this.points = points;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public Point[] getPoints() {
        return points;
    }

    public Point getPoint(int index){
        return points[index];
    }

    public int getPointsCount(){
        return points.length;
    }
}
