package anderson.assignment2.drawingapp.models;

import android.graphics.Color;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anderson on 10/5/15.
 */
public class Palette {
    private static Palette _Instance = null;
    private int[] defaultColors = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.WHITE};
    List<Paint> paints = new ArrayList<>();
    int activeColor = Color.BLACK;

    private Palette(){
    }

    public static Palette getInstance(){
        if(_Instance == null){
            _Instance = new Palette();
        }
        return _Instance;
    }

    public List<Paint> getPaints(){
        return paints;
    }

    public int getActiveColor(){
        return activeColor;
    }

    public Paint getPaint(int index){
        return paints.get(index);
    }

    public int getPaintsCount(){
        return paints.size();
    }

    public void addPaint(Paint paint){
        for (Paint currentPaint : paints) {
            currentPaint.setSelected(false);
        }
        paint.setSelected(true);
        paints.add(paint);
        activeColor = paint.getColor();
    }

    public void removePaint(int index){
        paints.remove(index);
    }

    public void removeAllPaints(String path) throws Exception{
        paints = new ArrayList<>();
        savePalette(path);
        loadPalette(path);
    }

    public void togglePaint(int color){
        for (Paint currentPaint : paints) {
            currentPaint.setSelected(false);
            if(currentPaint.getColor() == color)
                currentPaint.setSelected(true);
        }
        activeColor = color;
    }

    public void loadPalette(String path) throws Exception{
        Gson gson = new Gson();

        try {
            File drawingFile = new File(path);
            FileReader fileReader = new FileReader(drawingFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String paintsJson = reader.readLine();

            Type collectionType = new TypeToken<ArrayList<Paint>>(){}.getType();
            paints = gson.fromJson(paintsJson, collectionType);
        } catch (Exception e){
            throw new Exception("Error - Loading palette");
        }

        if(getPaintsCount() == 0){
            for (Integer color : defaultColors) {
                paints.add(new Paint(color));
            }

            getPaint(0).setSelected(true);
        }
    }

    public void savePalette(String path) throws Exception{
        Gson gson = new Gson();
        String jsonPaints = gson.toJson(paints);

        try {
            File drawingFile = new File(path);
            FileWriter fileWriter = new FileWriter(drawingFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(jsonPaints);
            writer.close();
        } catch (Exception e){
            throw new Exception("Error - Saving palette");
        }
    }
}
