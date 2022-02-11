package com.fernleaflowers.n3DispatcherTestV2Final;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Robots {

    @SerializedName("names")
    @Expose
    private List<String> names;

    @SerializedName("attributes")
    @Expose
    private List<String> attributes;



    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

}
