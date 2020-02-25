package com.orange.lo.sample.kerlink2lo.kerlink.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataDownDto {
    @JsonProperty("fPort")
    private Integer fPort = null;

    @JsonProperty("endDeviceDto")
    private EndDeviceDto endDeviceDto = null;

    @JsonProperty("payload")
    private String payload = null;

    @JsonProperty("contentType")
    private String contentType = null;

    @JsonProperty("confirmed")
    private String confirmed = null;

    @JsonProperty("ttl")
    private Integer ttl = null;

    @JsonProperty("maxAttempts")
    private Integer maxAttempts = null;

    public Integer getFPort() {
        return fPort;
    }

    public void setFPort(Integer fPort) {
        this.fPort = fPort;
    }

    public EndDeviceDto getEndDeviceDto() {
        return endDeviceDto;
    }

    public void setEndDeviceDto(EndDeviceDto endDeviceDto) {
        this.endDeviceDto = endDeviceDto;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
