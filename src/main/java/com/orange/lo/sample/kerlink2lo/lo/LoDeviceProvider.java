/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.invoke.MethodHandles;
import java.util.*;

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
    private HttpEntity<ObjectNode> updatePropertiesHttpEntity;
    private final String DEVICES_PAGED_URL_TEMPLATE;
   
    @Autowired
    public LoDeviceProvider(LoProperties loProperties, HttpHeaders authenticationHeaders) {
        this.loProperties = loProperties;
        this.authenticationHeaders = authenticationHeaders;
        
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        this.authenticationEntity = new HttpEntity<Void>(authenticationHeaders);
        this.updatePropertiesHttpEntity = getUpdateHttpEntity();
        this.DEVICES_PAGED_URL_TEMPLATE = loProperties.getDevicesUrl() + "?limit=" + loProperties.getPageSize() +
                "&offset=" + "%1$s" + "&filterQuery=" + PROPERTIES_FIELD + "." +
                loProperties.getPropertiesKey() + "==" + loProperties.getPropertiesValue() + "&fields=" + PROPERTIES_FIELD;
    }

    public List<LoDevice> getAllDevices() {
        ResponseEntity<LoDevice[]> response = restTemplate.exchange(loProperties.getDevicesUrl(), HttpMethod.GET, authenticationEntity, LoDevice[].class);
        return Arrays.asList(response.getBody());
    }

    //TODO use x-total-count
    //TODO use x-ratelimit-limit, x-ratelimit-remaining, x-ratelimit-reset"
    public List<LoDevice> getDevices() throws InterruptedException {
        List<LoDevice> devices = new ArrayList<>(loProperties.getPageSize());
        Date date= new Date();
        for (int offset = 0;; offset++) {
            ResponseEntity<LoDevice[]> response = restTemplate.exchange(getpagedDevicesUrl(offset), HttpMethod.GET, authenticationEntity, LoDevice[].class);
            if (Integer.parseInt(response.getHeaders().get("X-Ratelimit-Remaining").get(0)) == 0) {
                long reset = Long.parseLong(response.getHeaders().get("X-Ratelimit-Reset").get(0));
                long current = date.getTime();
                long diff = reset - current;
                Thread.sleep(reset - current);
                response = restTemplate.exchange(getpagedDevicesUrl(offset), HttpMethod.GET, authenticationEntity, LoDevice[].class);
            }
            LOG.debug("Calling LO url {}, and got {} devices", getpagedDevicesUrl(offset), response.getBody().length);
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

    @Deprecated
    public void addDevice(String deviceId, String deviceName, Map<String, String> properties) {
        LoDevice loDevice = new LoDevice(deviceId, deviceName, properties);
        HttpEntity<LoDevice> httpEntity = new HttpEntity<LoDevice>(loDevice, authenticationHeaders);
        LOG.trace("Device: " + loDevice.toString());
        try {
            ResponseEntity<Void> response = restTemplate.exchange(loProperties.getDevicesUrl(), HttpMethod.POST, httpEntity, Void.class);
            LOG.trace("Response: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateDeviceProperties(String deviceId) {
        String url = loProperties.getDevicesUrl() + "/" + deviceId;
        restTemplate.exchange(url, HttpMethod.PATCH, updatePropertiesHttpEntity, Void.class);
    }

    private HttpEntity<ObjectNode> getUpdateHttpEntity() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> deviceProperties = Collections.singletonMap(loProperties.getPropertiesKey(), loProperties.getPropertiesValue());
        ObjectNode propertiesRootNode = objectMapper.createObjectNode();
        JsonNode devicePropertiesJsonNode = objectMapper.valueToTree(deviceProperties);
        propertiesRootNode.set(PROPERTIES_FIELD, devicePropertiesJsonNode);
        HttpEntity<ObjectNode> httpEntity = new HttpEntity<ObjectNode>(propertiesRootNode, authenticationHeaders);
        return httpEntity;
    }

    public void deleteDevice(String deviceId) {
        restTemplate.exchange(loProperties.getDevicesUrl() + "/" + deviceId, HttpMethod.DELETE, authenticationEntity, Void.class);
    }

    private String getpagedDevicesUrl(int offset) {
        return String.format(DEVICES_PAGED_URL_TEMPLATE, offset * loProperties.getPageSize());
    }
}