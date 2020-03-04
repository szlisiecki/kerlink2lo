/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo;

import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataUpDto;
import com.orange.lo.sample.kerlink2lo.lo.ExternalConnectorService;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Kerlink2LoController {

    private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ExternalConnectorService externalConnectorService;
    
    public Kerlink2LoController(ExternalConnectorService externalConnectorService) {
        this.externalConnectorService = externalConnectorService;
    }
    
    @PostMapping("/dataUp")
    public Callable<ResponseEntity<Void>> dataUp(@RequestBody DataUpDto dataUpDto) {
        LOG.debug("received {}", dataUpDto);
        return () -> {
            externalConnectorService.sendMessage(dataUpDto);
            return ResponseEntity.ok().build();
        };
    }
    
    @PostMapping("/dataDownEvent")
    public Callable<ResponseEntity<Void>> dataUp(@RequestBody DataDownEventDto dataDownEventDto) {
        LOG.debug("received {}", dataDownEventDto);
        return () -> {
            return ResponseEntity.ok().build();
        };
    }
}