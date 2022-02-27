package sample;

import javafx.collections.ArrayChangeListener;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;

public class fun {
    public static void main(String[] args){
        System.exit(0);
        System.out.println("hello");
        String s = "hello";
        HashMap<Integer, String > hashMap = new HashMap<>();
        hashMap.put(1, "hello");
        if(hashMap.get(1) == s) System.out.println("hey");
    }
}
