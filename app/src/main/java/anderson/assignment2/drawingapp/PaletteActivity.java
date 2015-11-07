package anderson.assignment2.drawingapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import anderson.assignment2.drawingapp.models.Paint;
import anderson.assignment2.drawingapp.models.Palette;

/**
 * Created by anderson on 10/5/15.
 */
public class PaletteActivity extends Activity {

    private PaletteView paletteView;
    private Palette palette;
    private String FILE_NAME = "palette.txt";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        palette = Palette.getInstance();

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.DKGRAY);
        setContentView(rootLayout);

        int d = (int)(getResources().getDisplayMetrics().density);
        paletteView = new PaletteView(this);
        paletteView.setBackgroundColor(Color.DKGRAY);
        paletteView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        paletteView.setPadding(d * 10, d * 5, d * 5, d * 20);
        rootLayout.addView(paletteView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        addDeleteButton();
        //loadPaints();

        TextView resetPalette = new TextView(this);
        resetPalette.setBackgroundColor(Color.DKGRAY);
        resetPalette.setTextColor(Color.WHITE);
        resetPalette.setText("RESET PALETTE");
        resetPalette.setTextSize(d * 6);
        resetPalette.setGravity(Gravity.RIGHT);
        rootLayout.addView(resetPalette, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

        resetPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllPaints();
                loadPaints();
            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
        try {
            palette.savePalette(new File(getFilesDir(), FILE_NAME).getPath());
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            palette.savePalette(new File(getFilesDir(), FILE_NAME).getPath());
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            palette.loadPalette(new File(getFilesDir(), FILE_NAME).getPath());
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        loadPaints();
    }

    private void loadPaints(){
        PaintBtn colorBtn;
        for(int i = 0; i < palette.getPaintsCount(); i++){
            Paint paint = palette.getPaint(i);
            colorBtn = new PaintBtn(this, paint.getColor());
            colorBtn.setSelected(paint.isSelected());
            colorBtn.setPadding(0, 0, 0, 0);
            final int id = View.generateViewId();
            colorBtn.setId(id);
            paletteView.addView(colorBtn);
            colorBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        toggleColor(id);
                    }
                    return true;
                }
            });
        }
    }

    private void toggleColor(int id){
        PaintBtn clickedBtn = (PaintBtn) findViewById(id);
        PaintBtn otherBtn;
        PaintBtn activeMixBtn = null;
        int activeColor = clickedBtn.getColor();
        boolean isMix = false;

        for (int i=0; i < paletteView.getChildCount(); i++){
            otherBtn = (PaintBtn) paletteView.getChildAt(i);
            if(!otherBtn.equals(clickedBtn)) {
                if(otherBtn.isMixModeOn()){
                    activeMixBtn = otherBtn;
                }
                otherBtn.invalidate();
                otherBtn.setSelected(false);
                otherBtn.setMixMode(false);
            }
        }

        if(clickedBtn.isSelected() && !clickedBtn.isMixModeOn()) clickedBtn.setMixMode(true);
        else if(clickedBtn.isSelected() && clickedBtn.isMixModeOn()) clickedBtn.setMixMode(false);

        if(activeMixBtn != null && paletteView.getChildCount() > 9){
            Toast.makeText(this, "There are too many colors in the palette. Remove one before you create another.",
                    Toast.LENGTH_LONG).show();
            activeMixBtn.setSelected(true);
            activeColor = activeMixBtn.getColor();
            activeMixBtn.invalidate();
        } else if(activeMixBtn != null){
            clickedBtn.setSelected(false);
            addColor(activeMixBtn, clickedBtn);
            isMix = true;
        } else {
            clickedBtn.setSelected(true);
            activeColor = clickedBtn.getColor();
            //drawingView.setPaintColor(clickedBtn.getColor());
        }

        if(!isMix)
            palette.togglePaint(activeColor);

        clickedBtn.invalidate();
    }

    private void addDeleteButton(){
        final PaintBtn removeBtn = new PaintBtn(this, Color.WHITE, true);
        paletteView.addView(removeBtn);
        removeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    removeColor();
                }
                return true;
            }
        });
    }

    private void removeColor(){
        PaintBtn colorBtn;
        for (int i=0; i < paletteView.getChildCount(); i++){
            colorBtn = (PaintBtn) paletteView.getChildAt(i);
            if(colorBtn.isSelected() && colorBtn.isMixModeOn()) {
                paletteView.removeColor(colorBtn);
                palette.removePaint(i - 1);
                break;
            }
        }
    }

    private PaintBtn addColor(PaintBtn activeBtn, PaintBtn clickedBtn){
        int activeRed = Color.red(activeBtn.getColor());
        int activeGreen = Color.green(activeBtn.getColor());
        int activeBlue = Color.blue(activeBtn.getColor());
        int clickedRed = Color.red(clickedBtn.getColor());
        int clickedGreen = Color.green(clickedBtn.getColor());
        int clickedBlue = Color.blue(clickedBtn.getColor());
        int finalColor = Color.rgb(activeRed - (activeRed - clickedRed) / 2,
                activeGreen - (activeGreen - clickedGreen) / 2,
                activeBlue - (activeBlue - clickedBlue) / 2);

        final PaintBtn mixBtn = new PaintBtn(this, finalColor);
        mixBtn.setSelected(true);
        mixBtn.setId(View.generateViewId());
        paletteView.addColor(mixBtn);
        //drawingView.setPaintColor(mixBtn.getColor());

        mixBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    toggleColor(mixBtn.getId());
                }
                return true;
            }
        });

        palette.addPaint(new Paint(finalColor));

        return mixBtn;
    }

    private void removeAllPaints(){
        for (int i = paletteView.getChildCount() - 1; i > 0 ; i--){
            paletteView.removeColor(paletteView.getChildAt(i));
        }

        try {
            palette.removeAllPaints(new File(getFilesDir(), FILE_NAME).getPath());
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
