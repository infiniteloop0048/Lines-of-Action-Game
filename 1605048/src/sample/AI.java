package sample;

import javafx.geometry.Pos;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class AI {
    Random rand = new Random();
    point2D answer_prev;
    point2D answer_new_;
    int[][] arr = { { -80, -25, -20, -20, -20, -20, -25, -80 },
                    { -25,  10,  10,  10,  10,  10,  10,  -25},
                    {-20,  10,  25,  25,  25,  25,  10,  -20} ,
                    {-20,  10,  25,  50,  50,  25,  10,  -20},
                    {-20,  10,  25,  50,  50,  25,  10,  -20},
                    {-20,  10,  25,  25,  25,  25,  10,  -20},
                    {-25,  10,  10,  10,  10,  10,  10,  -25},
                    {-80, -25, -20, -20, -20, -20, -25, -80} };

    public void calculate(Position root) {
        int bis = dfs(root);
        MousePointer.call_from_ai(answer_prev, answer_new_);
    }

    public int dfs(Position u) {
        int val = 0;
        if(u.level == 3) return evaluation_function(u);
        if (u.level % 2 == 0) {
            boolean flag = false;
            for (point2D prev : u.blue_pieces) {
                for(int i=1; i<=8; i++){
                    point2D new_ = generate_move(i, u, prev);
                    if(new_ != null){
                        Position new_position = (Position) DeepCopy.deepCopy(u);
                        new_position.parent = u;
                        new_position.level = u.level + 1;
                        //System.out.println("iiiiiiiiiiiiii->>>>>" + i);
                        synchronize(new_position, prev, new_, "blue");
                        val = dfs(new_position);
                        if(u.value == -1){
                            u.value = val;
                            if(u.level == 0){
                                answer_prev = prev;
                                answer_new_ = new_;
                            }
                            if(u.parent != null && u.parent.value == -1 && u.parent.value <= u.value){
                                flag = true;
                                break;
                            }
                        }
                        else{
                            if(val > u.value){
                                u.value = val;
                                if(u.level == 0){
                                    answer_prev = prev;
                                    answer_new_ = new_;
                                }
                                if(u.parent != null && u.parent.value == -1 && u.parent.value <= u.value){
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(flag) break;
            }
        }
        else {
            boolean flag = false;
            for (point2D prev : u.red_pieces) {
                for(int i=1; i<=8; i++){
                    point2D new_ = generate_move(i, u, prev);
                    if(new_ != null){
                        Position new_position = (Position) DeepCopy.deepCopy(u);
                        new_position.parent = u;
                        new_position.level = u.level + 1;
                        synchronize(new_position, prev, new_, "red");
                        val = dfs(new_position);
                        if(u.value == -1){
                            u.value = val;
                            if(u.parent != null && u.parent.value == -1 && u.parent.value >= u.value){
                                flag = true;
                                break;
                            }
                        }
                        else{
                            if(val < u.value){
                                u.value = val;
                                if(u.parent != null && u.parent.value == -1 && u.parent.value >= u.value){
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(flag) break;
            }
        }
        return val;

    }

    private void synchronize(Position position, point2D prev, point2D new_, String color){

        if(color.equals("blue")){
            position.blue_pieces.remove(new point2D(prev.getX(), prev.getY()));
            position.blue_pieces.add(new point2D(new_.getX(), new_.getY()));
        }
        else{
            position.red_pieces.remove(new point2D(prev.getX(), prev.getY()));
            position.red_pieces.add(new point2D(new_.getX(), new_.getY()));
        }

        reduce_to_maintain_map(position, prev);

        if(position.pointToColor.get(new point2D(new_.getX(), new_.getY())) == null){
            add_to_maintain_map(position, new_);
        }
        else{
            if(color.equals("blue")){
                //delete from red
                position.red_pieces.remove(new point2D(new_.getX(), new_.getY()));
            }
            else{
                position.blue_pieces.remove(new point2D(new_.getX(), new_.getY()));
            }
        }

        position.pointToColor.put(new point2D(prev.getX(), prev.getY()), null);
        position.pointToColor.put(new point2D(new_.getX(), new_.getY()), color);
    }

    private void reduce_to_maintain_map(Position position, point2D prev){
        int x = prev.getX();
        int y = prev.getY();
        position.same_x_axis.put(x, position.same_x_axis.get(x) - 1);
        position.same_y_axis.put(y, position.same_y_axis.get(y) - 1);
        position.right_to_left_diagonal.put(x + y, position.right_to_left_diagonal.get(x + y) - 1);
        position.left_to_right_diagonal.put(x - y, position.left_to_right_diagonal.get(x - y) - 1);
    }

    private void add_to_maintain_map(Position position, point2D new_){
        int x = new_.getX();
        int y = new_.getY();
        //System.out.println("new_x + new_y->" + new_);
        position.same_x_axis.put(x, position.same_x_axis.get(x) + 1);
        position.same_y_axis.put(y, position.same_y_axis.get(y) + 1);
        position.right_to_left_diagonal.put(x + y, position.right_to_left_diagonal.get(x + y) + 1);
        position.left_to_right_diagonal.put(x - y, position.left_to_right_diagonal.get(x - y) + 1);
    }

    private int evaluation_function(Position position){
        return area(position) + 2*position_value(position) + 3*connectedness(position) + 4*density(position);
    }

    private int connectedness(Position position){
        return cal_connectedness(position.blue_pieces, "blue", position.pointToColor) - cal_connectedness(position.red_pieces, "red", position.pointToColor);
    }

    private int cal_connectedness(ArrayList<point2D> arrayList, String s, HashMap<point2D, String> hashMap){
        int score = 0;
        for(point2D point2D : arrayList){
            if(hashMap.get(new point2D(point2D.getX() + 50, point2D.getY())) == s) score ++ ;
            if(hashMap.get(new point2D(point2D.getX() + 50, point2D.getY() - 50)) == s) score ++ ;
            if(hashMap.get(new point2D(point2D.getX() + 0, point2D.getY() - 50)) == s) score ++ ;
            if(hashMap.get(new point2D(point2D.getX() - 50, point2D.getY() - 50)) == s) score ++ ;
            if(hashMap.get(new point2D(point2D.getX() - 50, point2D.getY())) == s) score ++ ;
            if(hashMap.get(new point2D(point2D.getX() - 50, point2D.getY() + 50)) == s) score ++ ;
            if(hashMap.get(new point2D(point2D.getX() + 0, point2D.getY() + 50)) == s) score ++ ;
            if(hashMap.get(new point2D(point2D.getX() + 50, point2D.getY() + 50)) == s) score ++ ;
        }
        return score;
    }

    private int position_value(Position position){
        int score1 = 0, score2 = 0;
        for(point2D point2D : position.blue_pieces){
            int x = point2D.getX()/50;
            int y = point2D.getY()/50;
            score1 += arr[x][y];
        }
        for(point2D point2D : position.red_pieces){
            int x = point2D.getX()/50;
            int y = point2D.getY()/50;
            score2 += arr[x][y];
        }
        return score1 - score2;
    }

    private int density(Position position){
        int score1 = 0, score2 = 0, center_x = 0, center_y = 0;
        int score11 = 0, score21 = 0, center_x1 = 0, center_y1 = 0;
        for(point2D point2D : position.blue_pieces){
            center_x += point2D.getX();
            center_y += point2D.getY();
        }
        for(point2D point2D : position.red_pieces){
            center_x1 += point2D.getX();
            center_y1 += point2D.getY();
        }

        if(position.blue_pieces.size() != 0){
            center_x = center_x / position.blue_pieces.size();
            center_y = center_y / position.blue_pieces.size();
            for(point2D point2D : position.blue_pieces){
                score1 += Math.abs(center_x - point2D.getX());
                score2 += Math.abs(center_y - point2D.getY());
            }
        }
        if(position.red_pieces.size() != 0){
            center_x1 = center_x1 / position.red_pieces.size();
            center_y1 = center_y1 / position.red_pieces.size();
            for(point2D point2D : position.red_pieces){
                score11 += Math.abs(center_x1 - point2D.getX());
                score21 += Math.abs(center_y1 - point2D.getY());
            }
        }
        return (score1 + score2) - (score11 + score21);
    }

    private int area(Position position){
        return minmax(position.blue_pieces) - minmax(position.red_pieces);
    }

    private int minmax(ArrayList<point2D> p){
        int minx = 10000, miny = 10000, maxx = -10000, maxy = -10000;
        for(point2D point2D : p){
            minx = Math.min(minx, point2D.getX());
            maxx = Math.max(maxx, point2D.getX());
            miny = Math.min(miny, point2D.getY());
            maxy = Math.max(maxy, point2D.getY());
        }
        int area = (maxx - minx)/50 * (maxy - miny)/50;
        return area;
    }

    private point2D generate_move(int id, Position position, point2D point2D){
        if(id == 1){
            return move_down(position, point2D);
        }
        else if(id == 2){
            return move_up(position, point2D);
        }
        else if(id == 3){
            return move_right(position, point2D);
        }
        else if(id == 4){
            return move_left(position, point2D);
        }
        else if(id == 5){
            return move_left_down(position, point2D);
        }
        else if(id == 6){
            return move_left_up(position, point2D);
        }
        else if(id == 7){
            return move_right_down(position, point2D);
        }
        else return move_right_up(position, point2D);


    }

    private boolean validity_check(Position position, int x, int ny, int count, int count_to_compare, String own_color) {
        if (position.pointToColor.get(new point2D(x, ny)) == null) return false;
        if (count == count_to_compare && position.pointToColor.get(new point2D(x, ny)) == own_color) {
            return true;
        }
        if (count < count_to_compare && position.pointToColor.get(new point2D(x, ny)) != own_color) {
            return true;
        }
        return false;
    }

    private point2D move_down(Position position, point2D current) {
        int x = current.getX();
        int y = current.getY();
        String own_color = position.pointToColor.get(new point2D(x, y));
        boolean flag = false;
        //for x axis
        //int count_in_x_axis = MousePointer.same_x_axis.get(x);
        int count_in_x_axis = position.same_x_axis.get(x);
        if (y + (50 * count_in_x_axis) <= 350) {
            for (int count = 1; count <= count_in_x_axis; count++) {
                int ny = y + 50 * count;
                if (validity_check(position, x, ny, count, count_in_x_axis, own_color)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return  new point2D(x, y + 50*count_in_x_axis);
        }
        return null;
    }

    private point2D move_up(Position position, point2D current) {
        int x = current.getX();
        int y = current.getY();
        String own_color = position.pointToColor.get(new point2D(x, y));
        boolean flag = false;
        //for x axis
        //int count_in_x_axis = MousePointer.same_x_axis.get(x);
        int count_in_x_axis = position.same_x_axis.get(x);
        if (y - (50 * count_in_x_axis) >= 0) {
            for (int count = 1; count <= count_in_x_axis; count++) {
                int ny = y - 50 * count;
                if (validity_check(position, x, ny, count, count_in_x_axis, own_color)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) new point2D(x , y - 50*count_in_x_axis);
        }
        return null;
    }

    private point2D move_right(Position position, point2D current) {
        int x = current.getX();
        int y = current.getY();
        String own_color = position.pointToColor.get(new point2D(x, y));
        boolean flag = false;

        //int count_in_y_axis = MousePointer.same_y_axis.get(y);
        int count_in_y_axis = position.same_y_axis.get(y);
        if (x + (50 * count_in_y_axis) <= 350) {

            for (int count = 1; count <= count_in_y_axis; count++) {
                int nx = x + 50 * count;
                if (validity_check(position, nx, y, count, count_in_y_axis, own_color)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return new point2D(x + 50*count_in_y_axis, y);
        }
        return null;
    }

    private point2D move_left(Position position, point2D current) {
        int x = current.getX();
        int y = current.getY();
        String own_color = position.pointToColor.get(new point2D(x, y));
        boolean flag = false;

        int count_in_y_axis = position.same_y_axis.get(y);
        if (x - (50 * count_in_y_axis) >= 0) {

            for (int count = 1; count <= count_in_y_axis; count++) {
                int nx = x - 50 * count;
                if (validity_check(position, nx, y, count, count_in_y_axis, own_color)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return new point2D(x - 50*count_in_y_axis, y);
        }
        return null;
    }

    private point2D move_right_down(Position position, point2D current){
        int x = current.getX();
        int y = current.getY();
        String own_color = position.pointToColor.get(new point2D(x, y));
        boolean flag = false;

        int count_left_right_diagonal = position.left_to_right_diagonal.get(x - y);
        if(x + (50*count_left_right_diagonal) <= 350 && y + (50*count_left_right_diagonal) <= 350) {

            for (int count = 1; count <= count_left_right_diagonal; count++) {
                int nx = x + 50 * count;
                int ny = y + 50 * count;
                if (validity_check(position, nx, ny, count, count_left_right_diagonal, own_color)) {
                    flag = true;
                    break;
                }
            }
            if(!flag) return new point2D(x + (50*count_left_right_diagonal), y + (50*count_left_right_diagonal));
        }
        return null;
    }

    private point2D move_left_up(Position position, point2D current){
        int x = current.getX();
        int y = current.getY();
        String own_color = position.pointToColor.get(new point2D(x, y));
        boolean flag = false;

        int count_left_right_diagonal = position.left_to_right_diagonal.get(x - y);
        if(x - (50*count_left_right_diagonal) >= 0 && y - (50*count_left_right_diagonal) >= 0) {

            for (int count = 1; count <= count_left_right_diagonal; count++) {
                int nx = x - 50 * count;
                int ny = y - 50 * count;
                if (validity_check(position, nx, ny, count, count_left_right_diagonal, own_color)) {
                    flag = true;
                    break;
                }
            }
            if(!flag) return new point2D(x - (50*count_left_right_diagonal), y - (50*count_left_right_diagonal));
        }
        return null;
    }

    private point2D move_right_up(Position position, point2D current){
        int x = current.getX();
        int y = current.getY();
        String own_color = position.pointToColor.get(new point2D(x, y));
        boolean flag = false;

        int count_right_left_diagonal = position.right_to_left_diagonal.get(x + y);
        if(x + (50*count_right_left_diagonal) <= 350 && y - (50*count_right_left_diagonal) >= 0) {

            for (int count = 1; count <= count_right_left_diagonal; count++) {
                int nx = x + 50 * count;
                int ny = y - 50 * count;
                if (validity_check(position, nx, ny, count, count_right_left_diagonal, own_color)) {
                    flag = true;
                    break;
                }
            }
            if(!flag) return new point2D(x + (50*count_right_left_diagonal), y - (50*count_right_left_diagonal));
        }
        return null;
    }

    private point2D move_left_down(Position position, point2D current){
        int x = current.getX();
        int y = current.getY();
        String own_color = position.pointToColor.get(new point2D(x, y));
        boolean flag = false;

        int count_right_left_diagonal = position.right_to_left_diagonal.get(x + y);
        if(x - (50*count_right_left_diagonal) >= 0 && y + (50*count_right_left_diagonal) <= 350) {

            for (int count = 1; count <= count_right_left_diagonal; count++) {
                int nx = x - 50 * count;
                int ny = y + 50 * count;
                if (validity_check(position, nx, ny, count, count_right_left_diagonal, own_color)) {
                    flag = true;
                    break;
                }
            }
            if(!flag) return new point2D(x - (50*count_right_left_diagonal), y + (50*count_right_left_diagonal));
        }
        return null;
    }

}
