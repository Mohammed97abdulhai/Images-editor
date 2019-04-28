package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;

public class Controller implements Initializable {

    //GUI
    public ToggleButton drawBtn;
    public ToggleButton lineBtn;
    public ToggleButton rectangleBtn;
    public ToggleButton circleBtn;
    public ToggleButton ellipseBtn;
    public ToggleButton textBtn;
    public ToggleButton rubberBtn;

    public TextArea text;

    public Button undoBtn;
    public Button redoBtn;
    public Button saveBtn;
    public Button uploadBtn;
    public Button eyedropperBtn;
    public Button convertToGray;



    public Canvas canvas;
    public GraphicsContext gc;

    //Shapes
    public Line line = new Line();
    public Rectangle rect = new Rectangle();
    public Circle circ = new Circle();
    public Ellipse elps = new Ellipse();

    //Coloe Picker
    public ColorPicker cpLine = new ColorPicker(Color.BLACK);
    public ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);

    //Slider
    public Slider slider;

    public boolean isEyedropper = false;

    //Layers
    Stack<Shape> undoHistory = new Stack();
    Stack<Shape> redoHistory = new Stack();

    public void setOnMousePressed(MouseEvent e) {
        // Draw Free Using Pen
        if (drawBtn.isSelected()) {
            gc.setStroke(cpLine.getValue());
            gc.beginPath();
            System.out.println("e.getX(): " + e.getX() + ", " + "e.getY(): " + e.getY());
            gc.lineTo(e.getX(), e.getY());
        }

        //Draw Line
        if (lineBtn.isSelected()) {
            gc.setStroke(cpLine.getValue());
            line.setStartX(e.getX());
            line.setStartY(e.getY());
        }

        //Draw Rectangle
        if (rectangleBtn.isSelected()) {
            gc.setStroke(cpLine.getValue());
            gc.setFill(cpFill.getValue());
            rect.setX(e.getX());
            rect.setY(e.getY());
        }

        //Draw Circle
        if (circleBtn.isSelected()) {
            gc.setStroke(cpLine.getValue());
            gc.setFill(cpFill.getValue());
            circ.setCenterX(e.getX());
            circ.setCenterY(e.getY());
        }

        //Draw Ellipse
        if (ellipseBtn.isSelected()) {
            gc.setStroke(cpLine.getValue());
            gc.setFill(cpFill.getValue());
            elps.setCenterX(e.getX());
            elps.setCenterY(e.getY());
        }

        //Text
        if (textBtn.isSelected()) {
            gc.setLineWidth(1);
            gc.setFont(Font.font(slider.getValue()));
            gc.setStroke(cpLine.getValue());
            gc.setFill(cpFill.getValue());
            gc.fillText(text.getText(), e.getX(), e.getY());
            gc.strokeText(text.getText(), e.getX(), e.getY());
        }

        //Rubber
        if (rubberBtn.isSelected()) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);

        }
    }

    public void setOnMouseDragged(MouseEvent e) {
        // draw free using pen
        if (drawBtn.isSelected()) {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        }

        else if (rubberBtn.isSelected()) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
        }
    }

    public void setOnMouseReleased(MouseEvent e) {
        // Draw Free Using Pen
        if (drawBtn.isSelected()) {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
            gc.closePath();
        }

        //Draw Line
        else if (lineBtn.isSelected()) {
            line.setEndX(e.getX());
            line.setEndY(e.getY());
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
            undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
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
            undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
        }

        //Draw Circle
        else if (circleBtn.isSelected()) {
            circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);
            if (circ.getCenterX() > e.getX()) {
                circ.setCenterX(e.getX());
            }
            if (circ.getCenterY() > e.getY()) {
                circ.setCenterY(e.getY());
            }
            gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
            gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
            undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
        }

        //Draw Ellipse
        else if (ellipseBtn.isSelected()) {
            elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
            elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));
            if (elps.getCenterX() > e.getX()) {
                elps.setCenterX(e.getX());
            }
            if (elps.getCenterY() > e.getY()) {
                elps.setCenterY(e.getY());
            }
            gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
            gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
            undoHistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));
        }

        //Rubber
        if (rectangleBtn.isSelected()) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
        }

        redoHistory.clear();

        if (!undoHistory.empty()) {
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());
        }

        if (isEyedropper) {
            Point2D selectedPosition = new Point2D(e.getX(), e.getY());
            System.out.println("here: " + GetColor(selectedPosition));
            cpLine.setValue(GetColor(selectedPosition));
            gc.setStroke(cpLine.getValue());
            isEyedropper = false;
        }
    }

    public void lineWidthSlider(MouseEvent e) {
        double width = slider.getValue();
        gc.setLineWidth(width);

    }

    public void uploadFile(ActionEvent e) {
        FileChooser openFile = new FileChooser();
        openFile.setTitle("Open File");
        Stage fileUploadStage = new Stage();
        File file = openFile.showOpenDialog(fileUploadStage);
        if (file != null) {
            try {
                InputStream io = new FileInputStream(file);
                Image img = new Image(io);

                gc.drawImage(img, 0, 0, 500, 500);

            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }
    }

    public void saveFile(ActionEvent e) {
        FileChooser saveFile = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        saveFile.getExtensionFilters().add(extFilter);

        saveFile.setTitle("Save File");
        Stage fileChooserStage = new Stage();
        File file = saveFile.showSaveDialog(fileChooserStage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage(1080, 790);
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }
    }

    public void undoEdit(ActionEvent e) {
        if (!undoHistory.empty()) {
            gc.clearRect(0, 0, 1080, 790);
            Shape removedShape = undoHistory.lastElement();

            if (removedShape.getClass() == Line.class) {
                Line tempLine = (Line) removedShape;
                tempLine.setFill(gc.getFill());
                tempLine.setStroke(gc.getStroke());
                tempLine.setStrokeWidth(gc.getLineWidth());
                redoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
            } else if (removedShape.getClass() == Rectangle.class) {
                Rectangle tempRect = (Rectangle) removedShape;
                tempRect.setFill(gc.getFill());
                tempRect.setStroke(gc.getStroke());
                tempRect.setStrokeWidth(gc.getLineWidth());
                redoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
            } else if (removedShape.getClass() == Circle.class) {
                Circle tempCirc = (Circle) removedShape;
                tempCirc.setStrokeWidth(gc.getLineWidth());
                tempCirc.setFill(gc.getFill());
                tempCirc.setStroke(gc.getStroke());
                redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
            } else if (removedShape.getClass() == Ellipse.class) {
                Ellipse tempElps = (Ellipse) removedShape;
                tempElps.setFill(gc.getFill());
                tempElps.setStroke(gc.getStroke());
                tempElps.setStrokeWidth(gc.getLineWidth());
                redoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
            }

            Shape lastRedo = redoHistory.lastElement();
            lastRedo.setFill(removedShape.getFill());
            lastRedo.setStroke(removedShape.getStroke());
            lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
            undoHistory.pop();

            for (int i = 0; i < undoHistory.size(); i++) {
                Shape shape = undoHistory.elementAt(i);
                if (shape.getClass() == Line.class) {
                    Line temp = (Line) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                } else if (shape.getClass() == Rectangle.class) {
                    Rectangle temp = (Rectangle) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                } else if (shape.getClass() == Circle.class) {
                    Circle temp = (Circle) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                } else if (shape.getClass() == Ellipse.class) {
                    Ellipse temp = (Ellipse) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                }
            }
        } else {
            System.out.println("there is no action to undo");
        }
    }

    public void redoEdit(ActionEvent e) {
        if (!redoHistory.empty()) {
            Shape shape = redoHistory.lastElement();
            gc.setLineWidth(shape.getStrokeWidth());
            gc.setStroke(shape.getStroke());
            gc.setFill(shape.getFill());

            redoHistory.pop();
            if (shape.getClass() == Line.class) {
                Line tempLine = (Line) shape;
                gc.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
            } else if (shape.getClass() == Rectangle.class) {
                Rectangle tempRect = (Rectangle) shape;
                gc.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                gc.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());

                undoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
            } else if (shape.getClass() == Circle.class) {
                Circle tempCirc = (Circle) shape;
                gc.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                gc.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());

                undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
            } else if (shape.getClass() == Ellipse.class) {
                Ellipse tempElps = (Ellipse) shape;
                gc.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                gc.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());

                undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
            }
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());
        } else {
            System.out.println("there is no action to redo");
        }
    }


    public int imageWidth = 500;
    public int imageHeight = 500;

    public void setGrayImage(ActionEvent e) throws IOException {
        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        canvas.snapshot(null, writableImage);

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
                int avg = (red + green + blue) / 3;

                //replace RGB value with avg
                rgb = (alpha << 24) | (avg << 16) | (avg << 8) | avg;
                bufferedImage.setRGB(x, y, rgb);
            }
        }
        Image img;
        img = SwingFXUtils.toFXImage(bufferedImage, null);
        gc.drawImage(img, 0, 0, width, height);
    }

    public void CMYConvert(ActionEvent e) {
        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        canvas.snapshot(null, writableImage);

        PixelReader pixelReader = writableImage.getPixelReader();
        WritableImage temp = new WritableImage(imageWidth, imageHeight);

        PixelWriter pixelWriter = temp.getPixelWriter();
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {

                Color color = pixelReader.getColor(j, i);
                Color newColor = new Color(Math.max(1 - color.getRed(), 0), Math.max(1 - color.getGreen(), 0), Math.max(1 - color.getBlue(), 0), 1);
                Color rr = Color.TRANSPARENT;
                Color mm = pixelReader.getColor(j, i);

                if (mm.equals(rr))
                    pixelWriter.setColor(j, i, rr);
                else
                    pixelWriter.setColor(j, i, newColor);
            }
        }
        gc.drawImage(temp, 0, 0,500,500);
    }

    public void CMYKConvert(ActionEvent e) {
        /*WritableImage writableImage = new WritableImage(1080, 790);
        canvas.snapshot(null, writableImage);

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

                //Converting from RGB to CMYK:
                int Cyan = green + blue;
                int Magenta = red + blue;
                int Yellow = red + green;

                int K = Math.min(Math.min(Cyan, Magenta), Yellow);

                int C = Cyan - K;
                int M = Magenta - K;
                int Y = Yellow - K;

                //replace RGB value with CMYK
                rgb = (alpha << 24) | (C << 16) | (M << 8) | Y;

                bufferedImage.setRGB(x, y, rgb);
            }
        }

        Image img;
        img = SwingFXUtils.toFXImage(bufferedImage, null);
        gc.drawImage(img, 0, 0, width, height);*/
    }

    public void HSIConvert(ActionEvent e) {
        WritableImage writableImage = new WritableImage(imageWidth, imageWidth);
        canvas.snapshot(null, writableImage);

        PixelReader pixelReader = writableImage.getPixelReader();
        WritableImage temp = new WritableImage(imageWidth , imageHeight);
        PixelWriter pixelWriter = temp.getPixelWriter();
        for  (int i =0 ; i<imageHeight ; i++ ){
            for (int j=0 ; j<imageWidth ; j++ ){

                Color color = pixelReader.getColor(j , i );
                double r =   color.getRed() ;
                double g =   color.getGreen() ;
                double b =  color.getBlue() ;

                double teta = Math.acos(   (0.5 * ( (r-g) + (r-b)) )  / ( Math.pow(  r - g, 2) + ( (r-b ) * Math.pow(g-b , 0.5)) ) );
                double h = 0 ;
                if (b > g )
                    h =  360-teta ;
                double s = 1 - ((3 / (r+g+b)) * Math.min(r, Math.min(g,b)) );
                double I  = 1 /3 *   (r+g+b);
                Color newColor = new Color(h,s,I,1);
                Color rr = Color.TRANSPARENT ;
                Color mm = pixelReader.getColor(j,i);

                if( mm.equals(rr)  )
                    pixelWriter.setColor(j,i, rr);
                else
                    pixelWriter.setColor(j,i, newColor);
            }
        }
        gc.drawImage(temp, 0, 0,imageWidth,imageHeight);
    }

    public void NegativeConvert(ActionEvent e) {
        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        canvas.snapshot(null, writableImage);

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

                //subtract RGB from 255
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;

                //set new RGB value
                rgb = (alpha << 24) | (red << 16) | (green << 8) | blue;

                bufferedImage.setRGB(x, y, rgb);
            }
        }
        Image img;
        img = SwingFXUtils.toFXImage(bufferedImage, null);
        gc.drawImage(img, 0, 0, width, height);
    }

    public void mirrorConvert(ActionEvent e) {
        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        canvas.snapshot(null, writableImage);

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage mimg = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        // Create mirror image pixel by pixel
        for (int y = 0; y < height; y++) {
            for (int lx = 0, rx = width - 1; lx < width; lx++, rx--) {
                int p = bufferedImage.getRGB(lx, y);
                // set mirror image pixel value
                mimg.setRGB(rx, y, p);
            }
        }
        Image img;
        img = SwingFXUtils.toFXImage(mimg, null);
        gc.drawImage(img, 0, 0, width, height);
    }

    public void Rotate(ActionEvent e) {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());


        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage result = rotate(bufferedImage, 45.0);

        Image img;
        img = SwingFXUtils.toFXImage(result, null);
        gc.drawImage(img, 0, 0, width, height);

        /*g.drawRenderedImage(result, null);
        g.dispose();*/
    }


    public static BufferedImage rotate(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(h * cos + w * sin);
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }

    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    ArrayList<File> fileList = new ArrayList<File>();
    ArrayList<String> fileName = new ArrayList<String>();

    public void Merge(ActionEvent e) {
        FileChooser openFile = new FileChooser();
        openFile.setTitle("Open File");
        Stage fileUploadStage = new Stage();
        File file = openFile.showOpenDialog(fileUploadStage);
        if (file != null) {
            try {
                InputStream io = new FileInputStream(file);
                fileList.add(file);
                fileName.add(file.getPath());

                if (fileList.size() > 1000) {

                    System.out.println("file: " + fileList.size());

                    File file1 = new File(fileName.get(0));
                    //File file2 = new File(fileName.get(1));

                    //BufferedImage image = ImageIO.read(file1);
                    BufferedImage overlay = ImageIO.read(file1);
                    Image img;
                    img = SwingFXUtils.toFXImage(overlay, null);

                    PixelReader pixelReader = img.getPixelReader();
                    Pane root = new Pane();

                    // read the underlaying image
                    root.getChildren().add(new ImageView(new Image("sample/2.jpg")));

                    Canvas resultCanvas = new Canvas();
                    root.getChildren().add(resultCanvas);

                    GraphicsContext resultLayer = resultCanvas.getGraphicsContext2D();
                    for (int y = 0; y < img.getHeight(); y++) {
                        for (int x = 0; x < img.getWidth(); x++) {
                            if (pixelReader.getColor(x, y).equals(Color.WHITE)) {
                                resultLayer.fillRect(x, y, 0.1, 1.0);
                            }
                        }
                    }


                    gc.drawImage(img, 0, 0, imageWidth, imageHeight);


                } else {
                    io = new FileInputStream(file);
                    File file1 = new File(fileName.get(0));
                    BufferedImage overlay = ImageIO.read(file1);
                    Image img;
                    img = SwingFXUtils.toFXImage(overlay, null);
                    /*WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
                    canvas.snapshot(null, writableImage);*/

                    int f = 2;
                    for (int i = 0; i < f; i++) {
                        final double opacity = 1 - ((double) 1) / f;
                        gc.setGlobalAlpha(opacity);
                        //gc.setEffect(new BoxBlur(2, 2, 3));
                        System.out.println(opacity);

                    }

                    gc.drawImage(img, 0, 0, 500, 500);
                    gc.setGlobalAlpha(1);
                    /*FileChooser openFile1 = new FileChooser();
                    openFile1.setTitle("Open File");
                    Stage fileUploadStage1 = new Stage();
                    File file1 = openFile1.showOpenDialog(fileUploadStage1);
                    if (file != null) {
                        try {
                            InputStream io1 = new FileInputStream(file1);
                            Image img = new Image(io1);

                            int f = 2;

                            for (int i = 0 ; i < f; i++) {
                                final double opacity = 1 - ((double) i) / f;
                                System.out.println(opacity);
                                gc.setGlobalAlpha(opacity);
                                gc.setEffect(new BoxBlur(i * 2, i * 2, 3));


                            }

                            gc.drawImage(img, 0, 0,500,200);
                          //  gc.drawImage(img, 0, 0, 500, 500);

                        } catch (IOException ex) {
                            System.out.println("Error!");
                        }*/
                }


            } catch (IOException ex) {
                System.out.println("Error!0 " + ex.getMessage());
            }
        }
    }

    public void getEyedropper(MouseEvent e) {
        isEyedropper = true;
    }


    public void reset(ActionEvent e) {
        //gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        text.setPrefRowCount(1);
    }


}
