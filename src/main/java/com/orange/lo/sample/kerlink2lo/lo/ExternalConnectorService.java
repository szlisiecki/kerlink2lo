/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.LoDeviceCache;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataUpDto;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExternalConnectorService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private ExternalConnector externalConnector;
    private LoDeviceProvider loDeviceProvider;
    private LoDeviceCache deviceCache;
    private LoProperties loProperties;
    
    public ExternalConnectorService(ExternalConnector externalConnector, LoDeviceProvider loDeviceProvider, LoDeviceCache deviceCache, LoProperties loProperties) {
        this.externalConnector = externalConnector;
        this.loDeviceProvider = loDeviceProvider;
        this.deviceCache = deviceCache;
        this.loProperties = loProperties;
    }
    
    public void sendMessage(DataUpDto dataUpDto) {
        try {
            if (!deviceCache.contains(dataUpDto.getEndDevice().getDevEui())) {
                createDevice(dataUpDto.getEndDevice().getDevEui());
            }
            externalConnector.sendMessage(dataUpDto);            
        } catch (Exception e) {
            LOG.error("Unable to send message",e);
        }
    }
    
    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        externalConnector.sendCommandResponse(dataDownEventDto);
    }
    
    public void createDevice(String kerlinkDeviceId) {
        try {
            loDeviceProvider.addDevice(kerlinkDeviceId);
            externalConnector.sendStatus(kerlinkDeviceId);
            deviceCache.add(kerlinkDeviceId);
        } catch (Exception e) {
            LOG.error("Unable to create device",e);
        }
    }
    
    public void deleteDevice(String loDeviceId) {
        try {
            loDeviceProvider.deleteDevice(loDeviceId);
            String kerlinkDeviceId = loDeviceId.substring(loProperties.getDevicePrefix().length());
            deviceCache.delete(kerlinkDeviceId);
        } catch (Exception e) {
            LOG.error("Unable to delete device",e);
        }
    }
}
