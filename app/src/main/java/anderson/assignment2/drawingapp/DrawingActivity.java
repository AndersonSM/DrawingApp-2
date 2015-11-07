package anderson.assignment2.drawingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import anderson.assignment2.drawingapp.models.Drawing;
import anderson.assignment2.drawingapp.models.Palette;
import anderson.assignment2.drawingapp.models.Polyline;

/*
 * Anderson Sales de Menezes
 * u1034315
 * Assignment 2 - Drawing App
 */

public class DrawingActivity extends Activity {

    private String FILE_NAME = "drawing.txt";
    private DrawingView drawingView;
    private TextView openPalette;
    private Drawing drawing;
    private Palette palette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawing = Drawing.getInstance();
        palette = Palette.getInstance();

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(rootLayout);

        drawingView = new DrawingView(this);
        rootLayout.addView(drawingView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        LinearLayout textViews = new LinearLayout(this);
        float d = getResources().getDisplayMetrics().density;

        TextView watchMode = new TextView(this);
        watchMode.setBackgroundColor(Color.DKGRAY);
        watchMode.setTextColor(Color.WHITE);
        watchMode.setText("WATCH MODE");
        watchMode.setTextSize(d * 5.5f);
        watchMode.setGravity(Gravity.LEFT);
        textViews.addView(watchMode, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView resetCanvas = new TextView(this);
        resetCanvas.setBackgroundColor(Color.DKGRAY);
        resetCanvas.setTextColor(Color.WHITE);
        resetCanvas.setText("RESET CANVAS");
        resetCanvas.setTextSize(d * 5.5f);
        resetCanvas.setGravity(Gravity.CENTER_HORIZONTAL);
        textViews.addView(resetCanvas, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        openPalette = new TextView(this);
        openPalette.setBackgroundColor(palette.getActiveColor());
        openPalette.setTextColor(Color.WHITE);
        openPalette.setShadowLayer(1.0f * d, -2.0f * d, 2.0f * d, Color.BLACK);
        openPalette.setText("OPEN PALETTE");
        openPalette.setTextSize(d * 5.5f);
        openPalette.setGravity(Gravity.RIGHT);
        textViews.addView(openPalette, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        rootLayout.addView(textViews);

        openPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openPaletteActivityIntent = new Intent();
                openPaletteActivityIntent.setClass(DrawingActivity.this, PaletteActivity.class);
                DrawingActivity.this.startActivity(openPaletteActivityIntent);
            }
        });


        resetCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.resetCanvas();
                drawingView.invalidate();
                drawing.removeAllPolylines();
                try {
                    drawing.saveDrawing(new File(getFilesDir(), FILE_NAME).getPath());
                } catch (Exception e) {
                }
            }
        });

        watchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openWatchingActivityIntent = new Intent();
                openWatchingActivityIntent.setClass(DrawingActivity.this, WatchingActivity.class);
                DrawingActivity.this.startActivity(openWatchingActivityIntent);
            }
        });

        drawingView.setOnPolylineCompletedListener(new DrawingView.OnPolylineCompletedListener() {
            @Override
            public void onPolylineCompleted(Polyline polyline) {
                drawing.addPolyline(polyline);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            drawing.saveDrawing(new File(getFilesDir(), FILE_NAME).getPath());
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            drawing.loadDrawing(new File(getFilesDir(), FILE_NAME).getPath());
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        drawingView.loadDrawing(drawing.getPolylines());

        drawingView.setPaintColor(palette.getActiveColor());
        openPalette.setBackgroundColor(palette.getActiveColor());
    }
}
