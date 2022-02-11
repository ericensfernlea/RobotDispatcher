package com.fernleaflowers.n3DispatcherTestV2Final;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class PathJSON {

    @SerializedName("path")
    @Expose
    private PathContent path;



    PathJSON(String path, String task){
        this.path = new PathContent(path,task);
    }

}


