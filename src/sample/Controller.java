package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button drawBtn;
    public Canvas canvas;
    public GraphicsContext gc;

    public ColorPicker cpLine = new ColorPicker(Color.BLACK);
    public ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);


    public void setOnMousePressed(MouseEvent e){
            gc.setStroke(cpLine.getValue());
            gc.beginPath();
            System.out.println("e.getX(): " + e.getX() + ", " + "e.getY(): " + e.getY());
            gc.lineTo(e.getX(), e.getY());
    }

    public void setOnMouseDragged(MouseEvent e){
        gc.lineTo(e.getX(), e.getY());
        gc.stroke();
    }

    public void setOnMouseReleased(MouseEvent e){
        gc.lineTo(e.getX(), e.getY());
        gc.stroke();
        gc.closePath();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
    }


}
