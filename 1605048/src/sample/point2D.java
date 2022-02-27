package sample;

import java.io.Serializable;

public class point2D implements Serializable {
    private int x;
    private int y;

    public point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object obj){
        if(obj == null) return false;
        if(obj == this) return false;
        if(x == ((point2D)obj).getX() && y == ((point2D)obj).getY()) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return x + y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "point2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
