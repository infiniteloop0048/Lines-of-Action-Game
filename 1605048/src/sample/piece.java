package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

public class piece implements Serializable {
    private ImageView imageView;
    private point2D p2D;
    String color;
    String file_path;

    public piece(String file_path, point2D p2D, String color) throws FileNotFoundException {
        this.file_path = file_path;
        this.p2D = p2D;
        this.color = color;
        prepare_imageview();
    }

    public point2D getP2D() {
        return p2D;
    }

    public void setP2D(point2D p2D) {
        this.p2D = p2D;
        imageView.setX(this.p2D.getX());
        imageView.setY(this.p2D.getY());
    }

    public void prepare_imageview() throws FileNotFoundException {
        imageView = new ImageView(new Image(new FileInputStream(file_path)));
        imageView.setX(this.p2D.getX());
        imageView.setY(this.p2D.getY());
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        imageView.setPreserveRatio(true);
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

}
