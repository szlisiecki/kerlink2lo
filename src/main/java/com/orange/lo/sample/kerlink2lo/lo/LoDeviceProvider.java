/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LoDeviceProvider {
    private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String PROPERTIES_FIELD = "properties";
    
    private RestTemplate restTemplate;
    private LoProperties loProperties;
    private HttpHeaders authenticationHeaders;
    private HttpEntity<Void> authenticationEntity;
    private final String DEVICES_PAGED_URL_TEMPLATE;
   
    @Autowired
    public LoDeviceProvider(LoProperties loProperties, HttpHeaders authenticationHeaders) {
        this.loProperties = loProperties;
        this.authenticationHeaders = authenticationHeaders;
        
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        this.authenticationEntity = new HttpEntity<Void>(authenticationHeaders);
        this.DEVICES_PAGED_URL_TEMPLATE = loProperties.getDevicesUrl() + "?limit=" + loProperties.getPageSize() + "&offset=" + "%1$s" + "&groupId=" + loProperties.getDeviceGroupId() + "&fields=" + PROPERTIES_FIELD;
    }

    public List<LoDevice> getDevices() throws InterruptedException {
        List<LoDevice> devices = new ArrayList<>(loProperties.getPageSize());
        Date date= new Date();
        for (int offset = 0;; offset++) {
            ResponseEntity<LoDevice[]> response = restTemplate.exchange(getPagedDevicesUrl(offset), HttpMethod.GET, authenticationEntity, LoDevice[].class);
            if (Integer.parseInt(response.getHeaders().get("X-Ratelimit-Remaining").get(0)) == 0) {
                long reset = Long.parseLong(response.getHeaders().get("X-Ratelimit-Reset").get(0));
                long current = date.getTime();
                Thread.sleep(reset - current);
                response = restTemplate.exchange(getPagedDevicesUrl(offset), HttpMethod.GET, authenticationEntity, LoDevice[].class);
            }
            LOG.debug("Calling LO url {}, and got {} devices", getPagedDevicesUrl(offset), response.getBody().length);
            if (response.getBody().length == 0) {
                break;
            }
            devices.addAll(Arrays.asList(response.getBody()));
            if (devices.size() >= Integer.parseInt(response.getHeaders().get("X-Total-Count").get(0))) {
                break;
            }
        }
        LOG.trace("Devices: " + devices.toString());
        return devices;
    }
    
    public void addDevice(String deviceId) {
        LOG.trace("Trying to add device {} to LO", deviceId);
        
        LoDevice device = new LoDevice(deviceId, loProperties.getDeviceGroupId(), loProperties.getDevicePrefix(), true);
        HttpEntity<LoDevice> httpEntity = new HttpEntity<LoDevice>(device, authenticationHeaders);
        
        restTemplate.exchange(loProperties.getDevicesUrl(), HttpMethod.POST, httpEntity, Void.class);
    }

    public void deleteDevice(String deviceId) {
        LOG.trace("Trying to delete device {} from LO", deviceId);
        restTemplate.exchange(loProperties.getDevicesUrl() + "/" + deviceId, HttpMethod.DELETE, authenticationEntity, Void.class);
    }

    private String getPagedDevicesUrl(int offset) {
        return String.format(DEVICES_PAGED_URL_TEMPLATE, offset * loProperties.getPageSize());
    }
}