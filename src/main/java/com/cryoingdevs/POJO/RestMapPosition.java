package com.cryoingdevs.POJO;

/**
 * Created by IvànAlejandro on 21/10/2018.
 */
public class RestMapPosition {
    private double[][] boundingBox;
    private double[] userLocation;
    private RestImage image;

    public double[][] getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(double[][] boundingBox) {
        this.boundingBox = boundingBox;
    }

    public double[] getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(double[] userLocation) {
        this.userLocation = userLocation;
    }

    public RestImage getImage() {
        return image;
    }

    public void setImage(RestImage image) {
        this.image = image;
    }
}
