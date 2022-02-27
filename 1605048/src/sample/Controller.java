package sample;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label1;

    @FXML
    private CheckBox first;

    @FXML
    private CheckBox secnod;

    @FXML
    void action(ActionEvent event) {
        String s = "";
        if(first.isSelected()){
            s += "first";
        }
        if(secnod.isSelected()){
            s += "second";
        }
        label1.setText(s);
    }

    @FXML
    void initialize() {
        assert label1 != null : "fx:id=\"label1\" was not injected: check your FXML file 'sample.fxml'.";
        assert first != null : "fx:id=\"first\" was not injected: check your FXML file 'sample.fxml'.";
        assert secnod != null : "fx:id=\"secnod\" was not injected: check your FXML file 'sample.fxml'.";
        label1.setText("jania");
    }
}
