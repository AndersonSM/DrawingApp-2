package anderson.assignment2.drawingapp.models;

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
 * Created by anderson on 10/4/15.
 */
public class Drawing {
    private List<Polyline> polylines = new ArrayList<>();
    private static Drawing _Instance = null;

    private Drawing(){
    }

    public static Drawing getInstance(){
        if(_Instance == null)
            _Instance = new Drawing();

        return _Instance;
    }

    public int getPolylinesCount(){
        return polylines.size();
    }

    public int getPointsCount(){
        int pointsCount = 0;

        for (Polyline polyline : polylines) {
            pointsCount += polyline.getPointsCount();
        }

        return pointsCount;
    }

    public Polyline getPolyline(int index){
        return polylines.get(index);
    }

    public List<Polyline> getPolylines(){
        return polylines;
    }

    public void addPolyline(Polyline line){
        polylines.add(line);
    }

    public void removePolyline(int index){
        polylines.remove(index);
    }

    public void removeAllPolylines(){
        polylines = new ArrayList<>();
    }

    public void loadDrawing(String path) throws Exception{
        Gson gson = new Gson();

        try {
            File drawingFile = new File(path);
            FileReader fileReader = new FileReader(drawingFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String polylinesJson = reader.readLine();

            Type collectionType = new TypeToken<ArrayList<Polyline>>(){}.getType();
            polylines = gson.fromJson(polylinesJson, collectionType);
        } catch (Exception e){
            throw new Exception("Error - Loading drawing");
        }
    }

    public void saveDrawing(String path) throws Exception{
        Gson gson = new Gson();
        String jsonPolylines = gson.toJson(polylines);

        try {
            File drawingFile = new File(path);
            FileWriter fileWriter = new FileWriter(drawingFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(jsonPolylines);
            writer.close();
        } catch (Exception e){
            throw new Exception("Error - Saving drawing");
        }
    }
}
