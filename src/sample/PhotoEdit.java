package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class PhotoEdit implements Initializable {

    //GUI

    //Menu Bar

    //Left VBOX
    public ToggleGroup toggleGroup;
    public ToggleButton penBtn;
    public ToggleButton lineBtn;
    public ToggleButton rectangleBtn;
    public ToggleButton circleBtn;
    public ToggleButton ellipseBtn;
    public ToggleButton rubberBtn;
    public ToggleButton textBtn;
    public ToggleButton eyedropperBtn;

    public TextArea textArea;

    public MenuItem openImgBtn;
    public MenuItem saveBtn;

    public Slider lineWidthSlider;
    public Slider alphaSlider;

    public ColorPicker fileColors;
    public ColorPicker lineColor;

    public Canvas canvas;
    public GraphicsContext gc;

    //Shapes
    public Line line = new Line();
    public Rectangle rect = new Rectangle();
    public Circle circle = new Circle();
    public Ellipse ellipse = new Ellipse();

    //Variables
    public boolean isEyedropper = false;

    //Create
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        lineColor.setValue(Color.valueOf("#000000"));
        penBtn.isSelected();
        textArea.setPrefRowCount(1);
    }

    //Draw
    public void setOnMousePressed(MouseEvent e) {
        // Draw Free Using Pen
        if (penBtn.isSelected()) {
            gc.setStroke(lineColor.getValue());
            gc.beginPath();
            System.out.println("e.getX(): " + e.getX() + ", " + "e.getY(): " + e.getY());
            gc.lineTo(e.getX(), e.getY());
        }

        //Draw Line
        if (lineBtn.isSelected()) {
            gc.setStroke(lineColor.getValue());
            line.setStartX(e.getX());
            line.setStartY(e.getY());
        }

        //Draw Rectangle
        if (rectangleBtn.isSelected()) {
            gc.setStroke(lineColor.getValue());
            gc.setFill(fileColors.getValue());
            rect.setX(e.getX());
            rect.setY(e.getY());
        }

        //Draw Circle
        if (circleBtn.isSelected()) {
            gc.setStroke(lineColor.getValue());
            gc.setFill(fileColors.getValue());
            circle.setCenterX(e.getX());
            circle.setCenterY(e.getY());
        }

        //Draw Ellipse
        if (ellipseBtn.isSelected()) {
            gc.setStroke(lineColor.getValue());
            gc.setFill(fileColors.getValue());
            ellipse.setCenterX(e.getX());
            ellipse.setCenterY(e.getY());
        }

        //Text
        if (textBtn.isSelected()) {
            gc.setLineWidth(1);
            gc.setFont(Font.font(lineWidthSlider.getValue()));
            gc.setStroke(lineColor.getValue());
            gc.setFill(fileColors.getValue());
            gc.fillText(textArea.getText(), e.getX(), e.getY());
            gc.strokeText(textArea.getText(), e.getX(), e.getY());
        }

        //Rubber
        if (rubberBtn.isSelected()) {
            double lineWidth = gc.getLineWidth();
            lineColor.setValue(Color.valueOf("#FFFFFF"));
            fileColors.setValue(Color.valueOf("#FFFFFF"));
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
        }

    }

    public void setOnMouseDragged(MouseEvent e) {
        // draw free using pen
        if (penBtn.isSelected()) {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        }

        //Rubber
        else if (rubberBtn.isSelected()) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
        }
    }

    public void setOnMouseReleased(MouseEvent e) {
        // Draw Free Using Pen
        if (penBtn.isSelected()) {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
            gc.closePath();
        }

        //Draw Line
        else if (lineBtn.isSelected()) {
            line.setEndX(e.getX());
            line.setEndY(e.getY());
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }

        //Draw Rectangle
        else if (rectangleBtn.isSelected()) {
            rect.setWidth(Math.abs((e.getX() - rect.getX())));
            rect.setHeight(Math.abs((e.getY() - rect.getY())));
            if (rect.getX() > e.getX()) {
                rect.setX(e.getX());
            }
            if (rect.getY() > e.getY()) {
                rect.setY(e.getY());
            }
            gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }

        //Draw Circle
        else if (circleBtn.isSelected()) {
            circle.setRadius((Math.abs(e.getX() - circle.getCenterX()) + Math.abs(e.getY() - circle.getCenterY())) / 2);
            if (circle.getCenterX() > e.getX()) {
                circle.setCenterX(e.getX());
            }
            if (circle.getCenterY() > e.getY()) {
                circle.setCenterY(e.getY());
            }
            gc.fillOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());
            gc.strokeOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());
        }

        //Draw Ellipse
        else if (ellipseBtn.isSelected()) {
            ellipse.setRadiusX(Math.abs(e.getX() - ellipse.getCenterX()));
            ellipse.setRadiusY(Math.abs(e.getY() - ellipse.getCenterY()));
            if (ellipse.getCenterX() > e.getX()) {
                ellipse.setCenterX(e.getX());
            }
            if (ellipse.getCenterY() > e.getY()) {
                ellipse.setCenterY(e.getY());
            }
            gc.strokeOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
            gc.fillOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
        }

        //Rubber
        else if (rectangleBtn.isSelected()) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
        }

        //Eyedropper
        else if (eyedropperBtn.isSelected()) {
            Point2D selectedPosition = new Point2D(e.getX(), e.getY());
            System.out.println("here: " + GetColor(selectedPosition));
            lineColor.setValue(GetColor(selectedPosition));
            gc.setStroke(lineColor.getValue());
            isEyedropper = false;
        }

    }

    //Clear
    public void clearScene(ActionEvent e) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    //Set Line Width
    public void lineWidthValue(MouseEvent e) {
        double width = lineWidthSlider.getValue();
        gc.setLineWidth(width);
    }

    //Set Opacity
    public void setOpacity(MouseEvent e) {
        /*double alpha = alphaSlider.getValue();
        gc.setGlobalAlpha(alpha);*/
    }

    //Open
    public void openImage(ActionEvent e) {
        FileChooser openFile = new FileChooser();
        openFile.setTitle("Open File");
        Stage fileUploadStage = new Stage();
        File file = openFile.showOpenDialog(fileUploadStage);
        if (file != null) {
            try {
                InputStream io = new FileInputStream(file);
                javafx.scene.image.Image img = new Image(io);

                gc.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight());

            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }
    }

    //Save
    public void savePng(ActionEvent e) {
        FileChooser saveFile = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        FileChooser.ExtensionFilter JPG = new FileChooser.ExtensionFilter("Image files (*.jpg)", "*.jpg", "*.JPG", "*.JPEG", "*.jpeg");
        saveFile.getExtensionFilters().add(extFilter);

        saveFile.setTitle("Save File");
        Stage fileChooserStage = new Stage();
        File file = saveFile.showSaveDialog(fileChooserStage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }
    }

    //Filter
    public void RGBToGray(ActionEvent e) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        gc.drawImage(RGBToGrayConvert(writableImage), 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void RGBToCMY(ActionEvent e) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        gc.drawImage(RGBToCMYConvert(writableImage), 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void RGBToCMYK(ActionEvent e) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        gc.drawImage(RGBToCMYKConvert(writableImage), 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void RGBToHSI(ActionEvent e) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        gc.drawImage(HSIToCMYKConvert(writableImage), 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void RGBToYCBCR(ActionEvent e) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        gc.drawImage(RGBToYCBCRConvert(writableImage), 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void imageMirror(ActionEvent e) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        gc.drawImage(Mirror(writableImage), 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    //Filter Function
    public Image RGBToGrayConvert(WritableImage writableImage) {
        Image convertedImage = null;

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = bufferedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;
                int alpha = (rgb >> 24) & 0xff;

                //calculate average
                int gray = (red + green + blue) / 3;

                //replace RGB value with avg
                rgb = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                bufferedImage.setRGB(x, y, rgb);
            }
            convertedImage = SwingFXUtils.toFXImage(bufferedImage, null);
        }

        return convertedImage;
    }

    public Image RGBToCMYConvert(WritableImage writableImage) {
        int imageHeight = (int) writableImage.getHeight();
        int imageWidth = (int) writableImage.getWidth();
        PixelReader pixelReader = writableImage.getPixelReader();
        WritableImage convertedImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = convertedImage.getPixelWriter();
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                Color color = pixelReader.getColor(j, i);
                double Cyan = 1 - color.getRed();
                double Magenta = 1 - color.getGreen();
                double Yellow = 1 - color.getBlue();
                Color newColor = new Color(Cyan, Magenta, Yellow, 1);
               pixelWriter.setColor(j, i, newColor);
            }
        }
        return convertedImage;
    }

    public Image RGBToCMYKConvert(WritableImage writableImage) {
        int imageHeight = (int) writableImage.getHeight();
        int imageWidth = (int) writableImage.getWidth();
        PixelReader pixelReader = writableImage.getPixelReader();
        WritableImage convertedImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = convertedImage.getPixelWriter();
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {

                Color color = pixelReader.getColor(j, i);
                double Cyan = 1 - color.getRed();
                double Magenta = 1 - color.getGreen();
                double Yellow = 1 - color.getBlue();

                double k = Math.min(Cyan, Math.min(Yellow, Magenta));
                Color newColor = new Color(Math.max(Cyan - k, 0), Math.max(Magenta - k, 0), Math.max(Yellow - k, 0), 1);
                pixelWriter.setColor(j, i, newColor);
            }
        }
        return convertedImage;
    }

    public Image HSIToCMYKConvert(WritableImage writableImage) {
        int imageHeight = (int) writableImage.getHeight();
        int imageWidth = (int) writableImage.getWidth();
        PixelReader pixelReader = writableImage.getPixelReader();
        WritableImage convertedImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = convertedImage.getPixelWriter();
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {

                Color color = pixelReader.getColor(j, i);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();

                double teta = Math.acos((0.5 * ((r - g) + (r - b))) / (Math.pow(r - g, 2) + ((r - b) * Math.pow(g - b, 0.5))));
                double h = 0;
                if (b > g)
                    h = 360 - teta;
                double s = 1 - ((3 / (r + g + b)) * Math.min(r, Math.min(g, b)));
                double I = 1 / 3 * (r + g + b);
                Color newColor = new Color(h, s, I, 1);
                pixelWriter.setColor(j, i, newColor);
            }
        }
        return convertedImage;
    }

    public Image RGBToYCBCRConvert(WritableImage writableImage) {
        int imageHeight = (int) writableImage.getHeight();
        int imageWidth = (int) writableImage.getWidth();
        PixelReader pixelReader = writableImage.getPixelReader();
        WritableImage convertedImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter pixelWriter = convertedImage.getPixelWriter();
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {

                Color color = pixelReader.getColor(j, i);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                float y, cb, cr;

                y = (float) (0.299 * r + 0.587 * g + 0.114 * b);
                cb = (float) (-0.169 * r - 0.331 * g + 0.500 * b);
                cr = (float) (0.500 * r - 0.419 * g - 0.081 * b);

                if (y < 0) {
                    y = 0;
                } else if (y > 1) {
                    y = 1;
                }

                if (cb < 0) {
                    cb = 0;
                } else if (cb > 1) {
                    cb = 1;
                }

                if (cr < 0) {
                    cr = 0;
                } else if (cr > 1) {
                    cr = 1;
                }

                Color newColor = new Color(y, cb, cr, 1);
                Color rr = Color.TRANSPARENT;
                Color mm = pixelReader.getColor(j, i);

                if (mm.equals(rr))
                    pixelWriter.setColor(j, i, rr);
                else
                    pixelWriter.setColor(j, i, newColor);
            }
        }
        return convertedImage;
    }

    public Image Mirror(WritableImage writableImage){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage mirrorImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Image convertedImage = null;

        // Create mirror image pixel by pixel
        for (int y = 0; y < height; y++) {
            for (int lx = 0, rx = width - 1; lx < width; lx++, rx--) {
                int p = bufferedImage.getRGB(lx, y);
                // set mirror image pixel value
                mirrorImg.setRGB(rx, y, p);
            }
        }
        convertedImage = SwingFXUtils.toFXImage(mirrorImg, null);
        return convertedImage;
    }

    //Helper Function
    public Color GetColor(Point2D mousePosition) {
        int mouseX = (int) mousePosition.getX(), mouseY = (int) mousePosition.getY();
        PixelReader pixelReader = GetPixelReader();
        Color color = pixelReader.getColor(mouseX, mouseY);
        return color;
    }

    private PixelReader GetPixelReader() {
        int canvasWidth = (int) canvas.getWidth(), canvasHeight = (int) canvas.getHeight();
        WritableImage image = new WritableImage(canvasWidth, canvasHeight);
        canvas.snapshot(null, image);
        return image.getPixelReader();
    }

}
