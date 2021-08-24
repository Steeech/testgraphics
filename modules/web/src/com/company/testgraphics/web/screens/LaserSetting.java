package com.company.testgraphics.web.screens;

import com.company.testgraphics.service.ScanProfile.Point;
import org.slf4j.Logger;
import org.vaadin.hezamu.canvas.Canvas;

import java.util.List;

public class LaserSetting {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(LaserSetting.class);
    private Canvas canvas;

    private int height, width;
    private int halfH, halfW;
    private int cellSize = 10;
    private double millimetersToPixel = ((double) cellSize)/20;

    public LaserSetting(int height, int width) {
        this.canvas = new Canvas();
        setHeight(height);
        setWidth(width);
    }

    public void drawPattern(){
        paintRoundedRect();
        this.setCellSize(cellSize*2);
        paintGrid(false);
        this.setCellSize(cellSize/2);
        paintCenterAxes();
        paintTube();

        paintLaser(1, 0, 0, "#C4C4C4");
        paintLaser(-1, 0, 0, "#C4C4C4");
    }

    public void drawPatterForSingleLaser(){
        paintRoundedRect();
        paintGrid(true);
        paintBottomAxes();
        paintLaserArea(1);
        paintLaserArea(-1);

    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        this.halfH = height / 2;
        this.canvas.setHeight(height + "px");
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        this.halfW = width / 2;
        this.canvas.setWidth(width + "px");
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
        this.millimetersToPixel = ((double) cellSize)/20;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    private Canvas paintRoundedRect() {

        int radius = 15;

        canvas.saveContext();
        canvas.moveTo(0,radius);
        canvas.lineTo(0,height-radius);
        canvas.quadraticCurveTo(0,height,radius,height);
        canvas.lineTo(width-radius,height);
        canvas.quadraticCurveTo(width,height,width,height-radius);
        canvas.lineTo(width,radius);
        canvas.quadraticCurveTo(width,0,width-radius,0);
        canvas.lineTo(radius,0);
        canvas.quadraticCurveTo(0,0,0,radius);

        canvas.setStrokeStyle("#E5E5E5");
        canvas.setLineWidth(3);
        canvas.stroke();

        canvas.closePath();
        canvas.restoreContext();

        return canvas;
    }

    private Canvas paintGrid(boolean isSingleLaser) {
        int k;

        int rows = height / cellSize;
        int columns = width / cellSize;
        String text;

        canvas.saveContext();
        canvas.beginPath();

        for (k = 1; k < rows; k++) {
            canvas.moveTo(0, k * cellSize);
            canvas.lineTo(width, k * cellSize);
            if (isSingleLaser)
                canvas.fillText(String.valueOf((rows-k-2)*20), halfW+5, k * cellSize+5, 20);


        }

        for (k = 1; k < columns; k++) {
            canvas.moveTo(k * cellSize, 0);
            canvas.lineTo(k * cellSize, height);
            if (isSingleLaser && k!=columns/2) {
                text = String.valueOf((k-columns/2)*20);
                canvas.fillText(text, k * cellSize-text.length()/2, height-2*cellSize+10, 20);
            }
        }
        canvas.setStrokeStyle("#E5E5E5");
        canvas.setLineWidth(1);
        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();

        return canvas;
    }

    private Canvas paintCenterAxes() {

        canvas.saveContext();
        canvas.beginPath();

        canvas.moveTo(0, halfH);
        canvas.lineTo(width, halfH);

        canvas.moveTo(halfW, 0);
        canvas.lineTo(halfW, height);

        canvas.setStrokeStyle(0, 0, 0);
        canvas.setLineWidth(1);
        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();

        return canvas;
    }

    private Canvas paintTube() {

        canvas.saveContext();
        canvas.beginPath();

        canvas.arc(halfW, halfH, cellSize * 10, 0, Math.PI * 2, true);

        canvas.setStrokeStyle("#C4C4C4");
        canvas.setLineWidth(5);
        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();
        return canvas;
    }

    public Canvas paintLaser(int signX, double offsetX, double offsetY){
        String color = (Math.abs(offsetX)>10/millimetersToPixel || Math.abs(offsetY)>10/millimetersToPixel) ? "#C65858" : "#58C65C";
        return paintLaser(signX, offsetX, offsetY, color);
    }

    public Canvas paintLaser(int signX, double offsetX, double offsetY, String color){
        canvas.saveContext();
        log.info(String.valueOf(offsetX));
        log.info(String.valueOf(offsetY));
        canvas.translate(offsetX*millimetersToPixel, offsetY*millimetersToPixel);
        canvas.setFillStyle(color);
        canvas.setStrokeStyle(color);
        paintLaserRay(signX, 1);
        paintLaserRay(signX, -1);
        paintLaserRect(signX);
        canvas.restoreContext();
        return canvas;
    }

    private Canvas paintLaserRect(int signX){
        canvas.saveContext();
        canvas.translate(halfW, halfH);
        canvas.beginPath();
        canvas.scale(signX*cellSize, 1*cellSize);
        canvas.fillRect(27, -3, 3, 6);
        canvas.closePath();
        canvas.restoreContext();

        return canvas;
    }

    private Canvas paintLaserRay(int signX, int signY) {

        canvas.saveContext();
        canvas.translate(halfW, halfH);
        canvas.beginPath();

        int singK = -signY * signX;
        int signB = signY;

        canvas.moveTo(getRayX(5.5, signX), 0);

        canvas.lineTo(getRayX(5.5, signX), getRayY(signX*5.5, singK, signB));
        canvas.lineTo(getRayX(18, signX), getRayY(signX*18, singK, signB));
        canvas.lineTo(getRayX(18, signX), 0);

        canvas.setLineWidth(3);
        canvas.stroke();

        canvas.moveTo(getRayX(5.5, signX), getRayY(signX*5.5, singK, signB));
        canvas.lineTo(getRayX(27, signX), getRayY(signX*27, singK, signB));

        canvas.setLineWidth(1);
        canvas.stroke();
        canvas.closePath();

        canvas.restoreContext();


        return canvas;
    }

    private double getRayY(double x, int signK, int signB) {
        return -(signK*0.216 * x + signB*8.138) * cellSize;
    }

    private double getRayX(double xCell, int signX) {
        return signX * xCell * cellSize;
    }

    private Canvas paintBottomAxes() {

        canvas.saveContext();
        canvas.beginPath();

        canvas.moveTo(0, height-2*cellSize);
        canvas.lineTo(width, height-2*cellSize);

        canvas.moveTo(halfW, 0);
        canvas.lineTo(halfW, height);

        canvas.setStrokeStyle(0, 0, 0);
        canvas.setLineWidth(1);
        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();

        return canvas;
    }

    private Canvas paintLaserArea(int signX){
        canvas.saveContext();
        canvas.translate(halfW, height-2*cellSize);
        canvas.beginPath();

        int singK = -signX;
        int signB = signX;

        canvas.moveTo(0, 0);

        canvas.lineTo(signX*85*millimetersToPixel, 0);
        canvas.lineTo(signX*(278/2)*millimetersToPixel, -(250)*millimetersToPixel);
        canvas.lineTo(0, -(250)*millimetersToPixel);

        canvas.setLineWidth(3);
        canvas.stroke();
        canvas.closePath();

        canvas.restoreContext();


        return canvas;
    }
    
    public Canvas paintPoints(List<Point> pointList){
        canvas.saveContext();
        canvas.translate(halfW, height-2*cellSize);
        canvas.beginPath();
        canvas.setFillStyle(255,0,0);

        for (Point point : pointList) {
            canvas.fillRect(point.getX(), -point.getY(), 1, 1);
        }
        canvas.closePath();
        canvas.restoreContext();

        return canvas;
    }

}
