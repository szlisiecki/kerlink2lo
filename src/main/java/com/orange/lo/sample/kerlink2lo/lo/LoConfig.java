/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class LoConfig {

    private LoProperties loProperties;

    public LoConfig(LoProperties loProperties) {
        this.loProperties = loProperties;
    }

    @Bean
    public HttpHeaders authenticationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("X-API-KEY", loProperties.getApiKey());
        return headers;
    }
}
