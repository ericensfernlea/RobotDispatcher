package com.fernleaflowers.n3DispatcherTestV2Final;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Names {

        @SerializedName("names")
        @Expose
        private List<String> names;

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }


}
