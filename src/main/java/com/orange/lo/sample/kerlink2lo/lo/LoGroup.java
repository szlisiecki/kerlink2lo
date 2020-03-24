package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoGroup {

    private String id;
    
    @JsonCreator
    public LoGroup(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
