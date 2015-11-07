package anderson.assignment2.drawingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import anderson.assignment2.drawingapp.models.Point;
import anderson.assignment2.drawingapp.models.Polyline;

/**
 * Created by anderson on 9/20/15.
 */
public class DrawingView extends ViewGroup {

    public interface OnPolylineCompletedListener {
        void onPolylineCompleted(Polyline polyline);
    }

    OnPolylineCompletedListener _onPolylineCompletedListener = null;
    private Path _path = new Path();
    private Canvas _canvas;
    private Paint _paint, _paintCanvas;
    private Bitmap _canvasBitmap;
    private List<Point> _currentPoints;
    private List<Polyline> _polylines;
    private boolean watchMode = false;

    public DrawingView(Context context){
        super(context);
        setBackgroundColor(Color.WHITE);
        _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _paint.setColor(Color.BLACK);
        _paint.setAntiAlias(true);
        _paint.setStyle(Paint.Style.STROKE);
        _paint.setStrokeWidth(5.0f * getResources().getDisplayMetrics().density);
        _paint.setStrokeJoin(Paint.Join.ROUND);
        _paint.setStrokeCap(Paint.Cap.ROUND);
        _paintCanvas = new Paint(Paint.DITHER_FLAG);
    }

    public DrawingView(Context context, boolean watchMode){
        this(context);
        this.watchMode = watchMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(watchMode)
            return true;

        float x = event.getX();
        float y = event.getY();

        if(event.getAction() == event.ACTION_DOWN){
            _path.moveTo(x, y);
            _currentPoints = new ArrayList<>();
            _currentPoints.add(new Point(x / getWidth(), y / getHeight()));
        } else if(event.getAction() == event.ACTION_MOVE) {
            _path.lineTo(x, y);
            _currentPoints.add(new Point(x / getWidth(), y / getHeight()));
        } else if(event.getAction() == event.ACTION_UP){
            _canvas.drawPath(_path, _paint);
            _path.reset();

            if(_onPolylineCompletedListener != null){
                Point[] points = new Point[_currentPoints.size()];
                _currentPoints.toArray(points);
                _onPolylineCompletedListener.onPolylineCompleted(new Polyline(points, _paint.getColor()));
            }
        }

        invalidate();

        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            _canvasBitmap = _canvasBitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
            _canvas = new Canvas(_canvasBitmap);

            if(_polylines != null && !watchMode)
                loadDrawing(_polylines);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(_canvasBitmap, 0, 0, _paintCanvas);
        canvas.drawPath(_path, _paint);
    }

    public void loadDrawing(List<Polyline> polylines){
        _polylines = polylines;

        if(_canvas == null)
            return;
        if(polylines.size() == 0)
            return;

        _path.reset();
        Point point;
        Polyline polyline;

        for (int i = 0; i < polylines.size(); i++){
            polyline = polylines.get(i);
            for (int j = 0; j < polyline.getPoints().length; j++){
                point = polylines.get(i).getPoint(j);

                if(j == 0){
                    _path.moveTo(point.getX() * getWidth(), point.getY() * getHeight());
                } else if(j < polyline.getPoints().length - 1){
                    _path.lineTo(point.getX() * getWidth(), point.getY() * getHeight());
                } else {
                    _paint.setColor(polyline.getColor());
                    _canvas.drawPath(_path, _paint);
                    _path.reset();
                }
            }
        }

        invalidate();
    }

    public void loadDrawing(List<Polyline> polylines, int lastPointIndex){
        if(_canvas == null)
            return;
        if(polylines.size() == 0)
            return;

        _canvasBitmap = _canvasBitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        _canvas = new Canvas(_canvasBitmap);
        _path.reset();
        Point point;
        Polyline polyline;

        int pointsCount = 0;
        boolean breakFlag = false;

        for (int i = 0; i < polylines.size(); i++){
            polyline = polylines.get(i);
            for (int j = 0; j < polyline.getPoints().length; j++){
                pointsCount++;
                point = polylines.get(i).getPoint(j);

                if(j == 0){
                    _path.moveTo(point.getX() * getWidth(), point.getY() * getHeight());
                } else if(j < polyline.getPoints().length - 1){
                    _path.lineTo(point.getX() * getWidth(), point.getY() * getHeight());
                } else {
                    _paint.setColor(polyline.getColor());
                    _canvas.drawPath(_path, _paint);
                    _path.reset();
                }
                if(pointsCount > lastPointIndex) {
                    breakFlag = true;
                    break;
                }
            }
            if(breakFlag)
                break;
        }

        invalidate();
    }

    public void setPaintColor(int color){
        _paint.setColor(color);
    }

    public void resetCanvas(){
        _canvasBitmap = _canvasBitmap.createBitmap(_canvasBitmap.getWidth(), _canvasBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        _canvas = new Canvas(_canvasBitmap);
        _currentPoints = new ArrayList<>();
        _polylines = null;
    }

    public OnPolylineCompletedListener getOnPolylineCompletedListener() {
        return _onPolylineCompletedListener;
    }

    public void setOnPolylineCompletedListener(OnPolylineCompletedListener onPolylineCompletedListener) {
        this._onPolylineCompletedListener = onPolylineCompletedListener;
    }

    public boolean isWatchMode() {
        return watchMode;
    }

    public void setWatchMode(boolean watchMode) {
        this.watchMode = watchMode;
    }
}
