/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoDevice {

    private String id;
    private String name;
    private Map<String, String> properties;

    @JsonCreator
    public LoDevice(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("properties") Map<String, String> properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        StringBuilder propertiesString = new StringBuilder();
        properties.forEach((key, value) -> {
            propertiesString.append(key).append(" ").append(value).toString();
        });
        return "Lo Device [id: " + id + "; name: " + name + "; properties: " + propertiesString + "]";
    }
}