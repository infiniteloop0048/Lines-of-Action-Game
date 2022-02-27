package  sample;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Position implements Serializable {
    public ArrayList<point2D> red_pieces = new ArrayList<>();
    public ArrayList<point2D> blue_pieces = new ArrayList<>();
    public HashMap<point2D, String> pointToColor = new HashMap<>();
    public HashMap<Integer, Integer> same_x_axis = new HashMap<>();
    public HashMap<Integer, Integer> same_y_axis = new HashMap<>();
    public HashMap<Integer, Integer> left_to_right_diagonal = new HashMap<>();
    public HashMap<Integer, Integer> right_to_left_diagonal = new HashMap<>();
    public int level;
    public int value;
    public Position parent;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Position(ArrayList<point2D> red_pieces, ArrayList<point2D> blue_pieces, HashMap<point2D, String> pointToColor, HashMap<Integer, Integer> same_x_axis, HashMap<Integer, Integer> same_y_axis, HashMap<Integer, Integer> left_to_right_diagonal, HashMap<Integer, Integer> right_to_left_diagonal, int level) {
        this.red_pieces = red_pieces;
        this.blue_pieces = blue_pieces;
        this.pointToColor = pointToColor;
        this.same_x_axis = same_x_axis;
        this.same_y_axis = same_y_axis;
        this.left_to_right_diagonal = left_to_right_diagonal;
        this.right_to_left_diagonal = right_to_left_diagonal;
        this.level = level;
        this.parent = null;
        this.value = -1;
    }

}