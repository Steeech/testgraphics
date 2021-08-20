package com.company.testgraphics.web.screens;

import org.slf4j.Logger;
import org.vaadin.hezamu.canvas.Canvas;

public class LaserSetting {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(LaserSetting.class);
    private Canvas canvas;

    private int height, width;
    private int halfH, halfW;
    private int cellSize = 10;
    private final double millimetersToPixel = ((double) cellSize)/20;

    public LaserSetting(int height, int width) {
        this.canvas = new Canvas();
        setHeight(height);
        setWidth(width);
    }

    public void drawPattern(){
        paintGrid();
        paintAxes();
        paintTube();

        paintLaser(1, 0, 0, "#C4C4C4");
        paintLaser(-1, 0, 0, "#C4C4C4");
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

    public Canvas getCanvas() {
        return this.canvas;
    }

    private Canvas paintGrid() {
        int k;

        int rows = height / cellSize;
        int columns = width / cellSize;

        canvas.saveContext();
        canvas.beginPath();

        for (k = 0; k <= rows; k++) {
            canvas.moveTo(0, k * cellSize);
            canvas.lineTo(width, k * cellSize);
        }

        for (k = 0; k <= columns; k++) {
            canvas.moveTo(k * cellSize, 0);
            canvas.lineTo(k * cellSize, height);
        }
        canvas.setStrokeStyle("#E5E5E5");
        canvas.setLineWidth(1);
        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();

        return canvas;
    }

    private Canvas paintAxes() {

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

    private double getRayY(double x, int signK, int signB) {
        return -(signK*0.216 * x + signB*8.138) * cellSize;
    }

    private double getRayX(double xCell, int signX) {
        return signX * xCell * cellSize;
    }

    public Canvas paintLaser(int signX, double offsetX, double offsetY){

        String color = (Math.abs(offsetX)>10 || Math.abs(offsetY)>10) ? "#C65858" : "#58C65C";
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
        canvas.fillRect(getRayX(27, signX), -3*cellSize, 3*cellSize, 6*cellSize);
        canvas.restoreContext();

        return canvas;
    }

    private Canvas paintLaserRay(int signX, int singY) {

        canvas.saveContext();
        canvas.translate(halfW, halfH);
        canvas.beginPath();

        int singK = -singY * signX;
        int signB = singY;

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

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }
}
