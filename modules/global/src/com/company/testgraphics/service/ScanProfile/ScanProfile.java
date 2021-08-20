package com.company.testgraphics.service.ScanProfile;

import com.company.testgraphics.entity.LaserTypeEnum;

import java.util.List;

public class ScanProfile {

    private int size;
    private List<Point> pointList;
    private Point minPoint;
    private LaserTypeEnum laserType;

    public ScanProfile(int size, List<Point> pointList, Point minPoint, LaserTypeEnum laserType) {
        this.size = size;
        this.pointList = pointList;
        this.minPoint = minPoint;
        this.laserType = laserType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public Point getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(Point minPoint) {
        this.minPoint = minPoint;
    }

    public LaserTypeEnum getLaserType() {
        return laserType;
    }

    public void setLaserType(LaserTypeEnum laserType) {
        this.laserType = laserType;
    }
}
