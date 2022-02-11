package com.fernleaflowers.n3DispatcherTestV2Final;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PathContent {

        @SerializedName("path-name")
        @Expose
        private String path;

        @SerializedName("task")
        @Expose
        private String task;

        PathContent(String path, String task){
            this.path = path;
            this.task = task;
        }

}
