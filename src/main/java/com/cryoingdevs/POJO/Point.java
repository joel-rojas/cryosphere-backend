package com.cryoingdevs.POJO;

/**
 * Created by IvànAlejandro on 21/10/2018.
 */
public class Point {
    private int x;
    private int y;
    private double distanceToSource;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getDistanceToSource() {
        return distanceToSource;
    }

    public void setDistanceToSource(double distanceToSource) {
        this.distanceToSource = distanceToSource;
    }
}
