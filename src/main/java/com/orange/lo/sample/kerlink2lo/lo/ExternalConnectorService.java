/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataUpDto;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExternalConnectorService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final String LO_DEVICE_ID_PREFIX = "urn:lo:nsid:x-connector:";
    private ExternalConnector externalConnector;
    private LoDeviceProvider loDeviceProvider;
    
    public ExternalConnectorService(ExternalConnector externalConnector, LoDeviceProvider loDeviceProvider) {
        this.externalConnector = externalConnector;
        this.loDeviceProvider = loDeviceProvider;
    }
    
    //TODO add cache for devices
    public void sendMessage(DataUpDto dataUpDto) {
        try {
            externalConnector.sendMessage(dataUpDto);
            externalConnector.sendStatus(dataUpDto.getEndDevice().getDevEui());
            waitFor(2000);
            loDeviceProvider.updateDeviceProperties(LO_DEVICE_ID_PREFIX + dataUpDto.getEndDevice().getDevEui());            
        } catch (Exception e) {
            LOG.error("Unable to send message",e);
        }
    }
    
    public void createDevice(String kerlinkDeviceId) {
        try {
            externalConnector.sendStatus(kerlinkDeviceId);
            waitFor(2000);
            loDeviceProvider.updateDeviceProperties(LO_DEVICE_ID_PREFIX + kerlinkDeviceId);            
        } catch (Exception e) {
            LOG.error("Unable to create device",e);
        }
    }
    
    public void deleteDevice(String loDeviceId) {
        try {
            loDeviceProvider.deleteDevice(loDeviceId);            
        } catch (Exception e) {
            LOG.error("Unable to delete device",e);
        }
    }
    
    private void waitFor(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
