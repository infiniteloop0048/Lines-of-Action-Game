package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application {

    private Scene scene;
    private Group root;
    private int count = 0;
    private AI ai = new AI();
    ArrayList<piece> list = new ArrayList<>();
    ArrayList<Circle> temp_circle_list = new ArrayList<>();
    HashMap<point2D, Boolean> temp_circle_map = new HashMap<>();
    HashMap<point2D, Boolean> is_visited = new HashMap<>();
    private int dfs_count = 0;

    public void addToCurrentScene(piece p){
        root.getChildren().add(p.getImageView());
    }

    public void remove_from_current_scene(piece p){
        root.getChildren().remove(p.getImageView());
    }

    public void addCircle(Circle c){ root.getChildren().add(c); }

    public void removeCircle(){
        for(Circle circle : temp_circle_list){
            root.getChildren().remove(circle);
        }
    }

    @Override
    public void start(Stage stage) throws Exception{
        MousePointer.main = this;
        Image image = new Image(new FileInputStream("chessBoard.png"));
        ImageView imageView = new ImageView(image);

        imageView.setX(0);
        imageView.setY(0);

        imageView.setFitHeight(400);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                count ++ ;
                int x = (int) ((int)(e.getX()/50)*50);
                int y = (int) ((int)(e.getY()/50)*50);
                if(count == 1){
                    piece p = MousePointer.pointToPiece.get(new point2D(x, y));
                    if(p == null) count = 0;
                    else{
                        list.add(p);
                        list_all_valid_point(p);
                    }
                }
                else if(count == 2){
                    //System.out.println("x->" + x + " y->" + y + " x->" + list.get(0).getP2D().getX()+ " y->" + list.get(0).getP2D().getY());

                    if(list.get(0).getP2D().equals(new point2D(x, y))){
                        list.clear();
                        count = 0;
                        removeCircle();
                        temp_circle_list.clear();
                        temp_circle_map.clear();
                    }
                    else if(temp_circle_map.get(new point2D(x, y)) == null){
                        count -- ;
                    }
                    else{
                        removeCircle();
                        temp_circle_list.clear();
                        temp_circle_map.clear();

                        //stage.show();
                        move_to_new_point(list.get(0).getP2D(), new point2D(x, y), 1);

                    }
                }
            }
        };

        //Creating a Group object
        root = new Group(imageView);


        scene = new Scene(root, 400, 400);
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
        stage.setTitle("Loading an image");
        stage.setScene(scene);
        stage.show();
        MousePointer.initial_all_map();
        MousePointer.init();
        //print_red_and_blue_pieces();
        //MousePointer.show();

    }

    public void apply_AI(){
        ArrayList<point2D> red_pieces = (ArrayList<point2D>) DeepCopy.deepCopy(MousePointer.red_pieces);
        ArrayList<point2D> blue_pieces = (ArrayList<point2D>) DeepCopy.deepCopy(MousePointer.blue_pieces);
        HashMap<point2D, String > pointToColor = (HashMap<point2D, String>) DeepCopy.deepCopy(MousePointer.pointToColor);
        HashMap<Integer, Integer> same_x_axis = (HashMap<Integer, Integer>) DeepCopy.deepCopy(MousePointer.same_x_axis);
        HashMap<Integer, Integer> same_y_axis = (HashMap<Integer, Integer>) DeepCopy.deepCopy(MousePointer.same_y_axis);
        HashMap<Integer, Integer> left_to_right_diagonal = (HashMap<Integer, Integer>) DeepCopy.deepCopy(MousePointer.left_to_right_diagonal);
        HashMap<Integer, Integer> right_to_left_diagonal = (HashMap<Integer, Integer>) DeepCopy.deepCopy(MousePointer.right_to_left_diagonal);
        Position root_position = new Position(red_pieces, blue_pieces, pointToColor, same_x_axis, same_y_axis, left_to_right_diagonal, right_to_left_diagonal, 0);
        ai.calculate(root_position);
        //new ai_thread(root_position);
        //print_red_and_blue_pieces(root_position);
    }

    public void move_to_new_point(point2D prev, point2D new_, int id){
        reduce_for_map_maintain(prev.getX(), prev.getY());

        piece piece1 = MousePointer.pointToPiece.get(prev);

        MousePointer.pointToPiece.put(prev, null);
        MousePointer.pointToColor.put(prev, null);

        //point2D pd = new point2D(x, y);

        //deleting the previous element if already existed
        piece to_delete = MousePointer.pointToPiece.get(new_);
        if(to_delete != null){
            remove_from_current_scene(to_delete);
            remove_from_red_blue_list(to_delete);
        }
        else{
            // to maintain map
            add_for_map_maintain(new_.getX(), new_.getY());
        }
        move_synchronize(prev, new_, piece1.getColor());
        piece1.setP2D(new_);
        MousePointer.pointToPiece.put(new_, piece1);
        MousePointer.pointToColor.put(new_, piece1.getColor());
        count = 0;
        list.clear();
        System.out.println("hi");

        if(id == 1) {
            dfs_for_red(MousePointer.red_pieces.get(0));
            dfs_for_blue(MousePointer.blue_pieces.get(0));
            apply_AI();
        }
        else{
            dfs_for_blue(MousePointer.blue_pieces.get(0));
            dfs_for_red(MousePointer.red_pieces.get(0));
        }
    }

    public void dfs_for_red(point2D point2D){
        dfs_count = 0;
        dfs(point2D, "red");
        if(dfs_count == MousePointer.red_pieces.size()){
            System.out.println("red won");
            System.exit(0);
        }
    }

    public void dfs_for_blue(point2D point2D){
        dfs_count = 0;
        dfs(point2D, "blue");
        if(dfs_count == MousePointer.blue_pieces.size()){
            System.out.println("blue won");
            System.exit(0);
        }
    }

    private void dfs(point2D point2D, String string){
        if(MousePointer.pointToColor.get(new point2D(point2D.getX(), point2D.getY())) != string) return ;
        if(is_visited.get(point2D) != null) return ;
        is_visited.put(point2D, true);
        dfs_count ++ ;
        dfs(new point2D(point2D.getX() + 50, point2D.getY()), string);
        dfs(new point2D(point2D.getX() + 50, point2D.getY() - 50), string);
        dfs(new point2D(point2D.getX() + 0, point2D.getY() - 50), string);
        dfs(new point2D(point2D.getX() - 50, point2D.getY() - 50), string);
        dfs(new point2D(point2D.getX() - 50, point2D.getY()), string);
        dfs(new point2D(point2D.getX() - 50, point2D.getY() + 50), string);
        dfs(new point2D(point2D.getX() + 0, point2D.getY() + 50), string);
        dfs(new point2D(point2D.getX() + 50, point2D.getY() + 50), string);

    }

    public void move_synchronize(point2D prev, point2D now, String string){
        if(string.equals("red")){
            MousePointer.red_pieces.remove(new point2D(prev.getX(), prev.getY()));
            MousePointer.red_pieces.add(new point2D(now.getX(), now.getY()));
        }
        else{
            MousePointer.blue_pieces.remove(new point2D(prev.getX(), prev.getY()));
            MousePointer.blue_pieces.add(new point2D(now.getX(), now.getY()));
        }
    }

    public void remove_from_red_blue_list(piece p){
        if(p.getColor().equals("red")) MousePointer.red_pieces.remove(p.getP2D());
        else MousePointer.blue_pieces.remove(p.getP2D());
    }

    public void reduce_for_map_maintain(int x, int y){
        MousePointer.same_x_axis.put(x, MousePointer.same_x_axis.get(x) - 1);
        MousePointer.same_y_axis.put(y, MousePointer.same_y_axis.get(y) - 1);
        MousePointer.left_to_right_diagonal.put((x - y), MousePointer.left_to_right_diagonal.get(x - y) - 1);
        MousePointer.right_to_left_diagonal.put((x + y), MousePointer.right_to_left_diagonal.get(x + y) - 1);
    }

    public void add_for_map_maintain(int x, int y){
        MousePointer.same_x_axis.put(x, MousePointer.same_x_axis.get(x) + 1);
        MousePointer.same_y_axis.put(y, MousePointer.same_y_axis.get(y) + 1);
        MousePointer.left_to_right_diagonal.put((x - y), MousePointer.left_to_right_diagonal.get(x - y) + 1);
        MousePointer.right_to_left_diagonal.put((x + y), MousePointer.right_to_left_diagonal.get(x + y) + 1);
    }

    private boolean validity_check(int x, int y, int count, int count_to_compare, String own_color){
        if(MousePointer.pointToPiece.get(new point2D(x, y)) == null) return false;
        if(count == count_to_compare && MousePointer.pointToPiece.get(new point2D(x, y)).getColor() == own_color){
            return true;
        }
        else if(count < count_to_compare && MousePointer.pointToPiece.get(new point2D(x, y)).getColor() != own_color){
            return true;
        }
        return false;
    }

    private void point_with_circle(int x, int y){
        Circle circle = new Circle(12);
        circle.setCenterX(x + 25);
        circle.setCenterY(y + 25);
        addCircle(circle);
        temp_circle_list.add(circle);
        temp_circle_map.put(new point2D(x, y), true);
    }

    public void list_all_valid_point(piece p){
        int x = p.getP2D().getX();
        int y = p.getP2D().getY();
        String own_color = p.getColor();
        Boolean flag;
        //for x axis
        int count_in_x_axis = MousePointer.same_x_axis.get(x);
        if(y + (50*count_in_x_axis) <= 350){
            flag = false;
            for(int count = 1; count <= count_in_x_axis; count++){
                int ny = y + 50*count;
                if(validity_check(x, ny, count, count_in_x_axis, own_color)) {
                    flag = true;
                    break;
                }
            }
            if(!flag){
                int newY  = y + (50*count_in_x_axis);
                point_with_circle(x, newY);

            }
        }
        if(y - (50*count_in_x_axis) >= 0){
            flag = false;
            for(int count = 1; count <= count_in_x_axis; count++){
                int ny = y - 50*count;
                if(validity_check(x, ny, count, count_in_x_axis, own_color)) {
                    flag = true;
                    break;
                }

            }

            if(!flag){
                int newY  = y - (50*count_in_x_axis);
                point_with_circle(x, newY);

            }
        }
        //for y axis
        int count_in_y_axis = MousePointer.same_y_axis.get(y);
        if(x + (50*count_in_y_axis) <= 350){

            flag = false;
            for(int count = 1; count <= count_in_y_axis; count++){
                int nx = x + 50*count;
                if(validity_check(nx, y, count, count_in_y_axis, own_color)) {
                    flag = true;
                    break;
                }

            }

            if(!flag){
                int newX  = x + (50*count_in_y_axis);
                point_with_circle(newX, y);

            }
            //System.out.println("y->" + newY + "x->" + x + " " + count_in_y_axis);
        }
        if(x - (50*count_in_y_axis) >= 0){

            flag = false;
            for(int count = 1; count <= count_in_y_axis; count++){
                int nx = x - 50*count;
                if(validity_check(nx, y, count, count_in_y_axis, own_color)) {
                    flag = true;
                    break;
                }

            }

            if(!flag){
                int newX  = x - (50*count_in_y_axis);
                point_with_circle(newX, y);

            }
            //System.out.println("y->" + newY + "x->" + x + " " + count_in_y_axis);
        }
        //left right diagonal
        int count_left_right_diagonal = MousePointer.left_to_right_diagonal.get(x - y);
        if(x + (50*count_left_right_diagonal) <= 350 && y + (50*count_left_right_diagonal) <= 350){

            flag = false;
            for(int count = 1; count <= count_left_right_diagonal; count++){
                int nx = x + 50*count;
                int ny = y + 50*count;
                if(validity_check(nx, ny, count, count_left_right_diagonal, own_color)) {
                    flag = true;
                    break;
                }

            }

            if(!flag){
                int newX = x + (50*count_left_right_diagonal);
                int newY = y + (50*count_left_right_diagonal);
                point_with_circle(newX, newY);

            }
        }

        if(x - (50*count_left_right_diagonal) >= 0 && y - (50*count_left_right_diagonal) >= 0){

            flag = false;
            for(int count = 1; count <= count_left_right_diagonal; count++){
                int nx = x - 50*count;
                int ny = y - 50*count;
                if(validity_check(nx, ny, count, count_left_right_diagonal, own_color)) {
                    flag = true;
                    break;
                }
            }

            if(!flag){
                int newX = x - (50*count_left_right_diagonal);
                int newY = y - (50*count_left_right_diagonal);
                point_with_circle(newX, newY);

            }
        }
        //right left diagonal
        int count_right_left_diagonal = MousePointer.right_to_left_diagonal.get(x + y);
        if(x + (50*count_right_left_diagonal) <= 350 && y - (50*count_right_left_diagonal) >= 0){

            flag = false;
            for(int count = 1; count <= count_right_left_diagonal; count++){
                int nx = x + 50*count;
                int ny = y - 50*count;
                if(validity_check(nx, ny, count, count_right_left_diagonal, own_color)) {
                    flag = true;
                    break;
                }
            }


            if(!flag){
                int newX = x + (50*count_right_left_diagonal);
                int newY = y - (50*count_right_left_diagonal);
                point_with_circle(newX, newY);

            }
        }
        if(x - (50*count_right_left_diagonal) >= 0 && y + (50*count_right_left_diagonal) <= 350){

            flag = false;
            for(int count = 1; count <= count_right_left_diagonal; count++){
                int nx = x - 50*count;
                int ny = y + 50*count;
                if(validity_check(nx, ny, count, count_right_left_diagonal, own_color)) {
                    flag = true;
                    break;
                }

            }

            if(!flag){
                int newX = x - (50*count_right_left_diagonal);
                int newY = y + (50*count_right_left_diagonal);
                point_with_circle(newX, newY);

            }
        }
    }




    public static void main(String[] args) {

        launch(args);
    }

}
