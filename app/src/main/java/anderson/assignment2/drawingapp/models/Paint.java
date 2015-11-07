package anderson.assignment2.drawingapp.models;

/**
 * Created by anderson on 10/5/15.
 */
public class Paint {

    int color;
    boolean isSelected = false;

    public Paint(int color){
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
