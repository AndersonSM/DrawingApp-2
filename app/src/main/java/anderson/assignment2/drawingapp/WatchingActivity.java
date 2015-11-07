package anderson.assignment2.drawingapp;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import anderson.assignment2.drawingapp.models.Drawing;

/**
 * Created by anderson on 10/5/15.
 */
public class WatchingActivity extends Activity {

    private String FILE_NAME = "drawing.txt";
    private DrawingView watchingView;
    private Drawing drawing;
    private boolean isPlaying = false;
    private Button playButton;
    private ValueAnimator seekBarAnimator;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawing = Drawing.getInstance();

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(rootLayout);

        watchingView = new DrawingView(this, true);
        rootLayout.addView(watchingView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        LinearLayout drawingControllers = new LinearLayout(this);

        playButton = new Button(this);
        playButton.setText("Play");
        int d = (int)getResources().getDisplayMetrics().density;
        playButton.setPadding(d * 5, 0, d * 5, 0);
        drawingControllers.addView(playButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

        seekBar = new SeekBar(this);
        seekBar.setMax(drawing.getPointsCount());
        drawingControllers.addView(seekBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        rootLayout.addView(drawingControllers);

        seekBarAnimator = new ValueAnimator();
        seekBarAnimator.setIntValues(0, seekBar.getMax());
        seekBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                seekBar.setProgress(value);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seekBar.getProgress() == seekBar.getMax()){
                    seekBar.setProgress(0);
                }
                seekBarAnimator.setIntValues(seekBar.getProgress(), seekBar.getMax());
                seekBarAnimator.setDuration((drawing.getPointsCount() - seekBar.getProgress()) * 10);
                playPauseAnimation();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                watchingView.loadDrawing(drawing.getPolylines(), progress);
                if (progress == seekBar.getMax()) {
                    pauseAnimation();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAnimation();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void playPauseAnimation(){
        if (!isPlaying) {
            startAnimation();
        } else {
            pauseAnimation();
        }
    }

    private void pauseAnimation(){
        playButton.setText("Play");
        playButton.invalidate();
        seekBarAnimator.cancel();
        isPlaying = false;
    }

    private void startAnimation(){
        playButton.setText("Pause");
        playButton.invalidate();
        seekBarAnimator.start();
        isPlaying = true;
    }
}
