package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;

public class MousePointer {
    public static Main main;
    public static ArrayList<point2D> blue_pieces = new ArrayList<>();
    public static ArrayList<point2D> red_pieces = new ArrayList<>();
    public static HashMap<point2D, piece> pointToPiece = new HashMap<>();
    public static HashMap<point2D, String> pointToColor = new HashMap<>();
    public static HashMap<Integer, Integer> same_x_axis = new HashMap<>();
    public static HashMap<Integer, Integer> same_y_axis = new HashMap<>();
    public static HashMap<Integer, Integer> left_to_right_diagonal = new HashMap<>();
    public static HashMap<Integer, Integer> right_to_left_diagonal = new HashMap<>();

    public static void init() throws FileNotFoundException {

        for(int i=1; i<=6; i++){
            init_complete(50*i, 0, "red");
        }

        for(int i=1; i<=6; i++){
            init_complete(50*i, 350, "red");
        }

        for(int i=1; i<=6; i++){
            init_complete(350, 50*i, "blue");
        }

        for(int i=1; i<=6; i++){
            init_complete(0, 50*i, "blue");
        }
        System.out.println("total size -> " + pointToPiece.size());
        //System.out.println(pointToPiece.get(new point2D(0, 50)).getP2D().getY());
    }

    public static void init_complete(int x, int y, String string) throws FileNotFoundException {
        point2D p2D = new point2D(x, y);
        piece p = new piece(string+".png", p2D,  string);
        pointToPiece.put(new point2D(x, y), p);
        main.addToCurrentScene(p);
        if(string.equals("red")){
            red_pieces.add(new point2D(x, y));
            pointToColor.put(new point2D(x, y), "red");
        }
        else {
            blue_pieces.add(new point2D(x, y));
            pointToColor.put(new point2D(x, y), "blue");
        }
        same_x_axis.put(x, same_x_axis.get(x) + 1);
        same_y_axis.put(y, same_y_axis.get(y) + 1);
        left_to_right_diagonal.put((x-y), left_to_right_diagonal.get(x-y) + 1);
        right_to_left_diagonal.put((x+y), right_to_left_diagonal.get(x+y) + 1);
    }

    public static void call_from_ai(point2D prev, point2D new_){
        main.move_to_new_point(prev, new_, 2);
    }

    public static void initial_all_map(){
        for(int i=-7; i<8; i++){
            left_to_right_diagonal.put(50*i, 0);
        }
        for(int i=0; i<=700; i = i + 50){
            right_to_left_diagonal.put(i, 0);
        }
        for(int i=0; i<8; i++){
            same_x_axis.put(50*i, 0);
        }
        for(int i=0; i<8; i++){
            same_y_axis.put(50*i, 0);
        }
    }

    public static void show(){
        System.out.println("for left to right -----");
        for(int i=-7; i<8; i++){
            System.out.println("key : " + 50*i + "value : " + left_to_right_diagonal.get(50*i));
            //left_to_right_diagonal.put(50*i, 0);
        }
        System.out.println("for right to left ------");
        for(int i=0; i<=700; i = i + 50){
            System.out.println("key: " + i + "value: " + right_to_left_diagonal.get(i));
        }
        System.out.println("same x --------");
        for(int i=0; i<8; i++){
            System.out.println("key: " + 50*i + "value: " + same_x_axis.get(50*i));
        }
        System.out.println("same y ---------");
        for(int i=0; i<8; i++){
            System.out.println("key: " + 50*i + "value: " + same_y_axis.get(50*i));
        }
    }

}
