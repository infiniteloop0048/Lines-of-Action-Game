package sample;

import java.util.ArrayList;
import java.util.HashMap;

public class ai_thread implements Runnable{

    public static boolean flag = false;
    public ai_thread(){
        new Thread(this).start();
    }
    @Override
    public void run() {
        //.calculate(position);
        //System.out.println("achi vai sokol");
        while (true) {
            System.out.println(flag);
            if(flag) {
                System.out.println("hoy ki kjokho");
                ArrayList<point2D> red_pieces = (ArrayList<point2D>) DeepCopy.deepCopy(MousePointer.red_pieces);
                ArrayList<point2D> blue_pieces = (ArrayList<point2D>) DeepCopy.deepCopy(MousePointer.blue_pieces);
                HashMap<point2D, String> pointToColor = (HashMap<point2D, String>) DeepCopy.deepCopy(MousePointer.pointToColor);
                HashMap<Integer, Integer> same_x_axis = (HashMap<Integer, Integer>) DeepCopy.deepCopy(MousePointer.same_x_axis);
                HashMap<Integer, Integer> same_y_axis = (HashMap<Integer, Integer>) DeepCopy.deepCopy(MousePointer.same_y_axis);
                HashMap<Integer, Integer> left_to_right_diagonal = (HashMap<Integer, Integer>) DeepCopy.deepCopy(MousePointer.left_to_right_diagonal);
                HashMap<Integer, Integer> right_to_left_diagonal = (HashMap<Integer, Integer>) DeepCopy.deepCopy(MousePointer.right_to_left_diagonal);
                Position root_position = new Position(red_pieces, blue_pieces, pointToColor, same_x_axis, same_y_axis, left_to_right_diagonal, right_to_left_diagonal, 0);
                new AI().calculate(root_position);
                flag = false;
            }
        }
    }
}
