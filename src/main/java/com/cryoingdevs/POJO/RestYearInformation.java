package com.cryoingdevs.POJO;

/**
 * Created by IvànAlejandro on 21/10/2018.
 */
public class RestYearInformation {
    private String name;
    private long value;

    public RestYearInformation(String name, long value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
