package com.cryoingdevs.POJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IvànAlejandro on 21/10/2018.
 */
public class RestImagePercentages {
    private String name;
    private List<RestYearInformation> series;

    public RestImagePercentages(String name){
        this.name = name;
        series = new ArrayList<RestYearInformation>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RestYearInformation> getSeries() {
        return series;
    }

    public void setSeries(List<RestYearInformation> series) {
        this.series = series;
    }
}
