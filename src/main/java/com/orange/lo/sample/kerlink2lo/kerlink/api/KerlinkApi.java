package com.orange.lo.sample.kerlink2lo.kerlink.api;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.*;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
@EnableScheduling
public class KerlinkApi {

    private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RestTemplate restTemplate;
    private KerlinkProperties kerlinkProperties;

    HttpEntity<Void> httpEntity;
    private String firstHref;
    private String token;

    @Autowired
    public KerlinkApi(KerlinkProperties kerlinkProperties) {
        this.kerlinkProperties = kerlinkProperties;
        this.restTemplate = prepareRestTemplate();
        this.firstHref = "/application/endDevices?fields=devEui,devAddr,name,country,status&sort=%2BdevEui&page=1&pageSize="
                + kerlinkProperties.getPageSize();
        this.login();
    }

    @Scheduled(initialDelay = 32400000, fixedRate = 32400000) // every 9 hours
    public void login() {
        LOG.info("Trying to login and get bearer token");
        UserDto userDto = new UserDto();
        userDto.setLogin(kerlinkProperties.getLogin());
        userDto.setPassword(kerlinkProperties.getPassword());
        String url = kerlinkProperties.getBaseUrl() + "/application/login";
        try {
            ResponseEntity<JwtDto> responseEntity = restTemplate.postForEntity(url, userDto, JwtDto.class);
            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                this.httpEntity = prepareHttpEntity("Bearer " + responseEntity.getBody().getToken());
                this.token = responseEntity.getBody().getToken();
            } else {
                LOG.error("Error while trying to login to Kerlink platform, returned status code is {}", responseEntity.getStatusCodeValue());
                System.exit(1);
            }
        } catch (Exception e) {
            LOG.error("Error while trying to login to Kerlink platform, ", e);
            System.exit(1);
        }
        //sending a command to a device
        LOG.info("Sending command...");
        EndDeviceDto endDevice = new EndDeviceDto();
        endDevice.setDevEui("0018B20000002345");
        DataDownDto dataDownDto = new DataDownDto();
        dataDownDto.setFPort(1);
        dataDownDto.setEndDevice(endDevice);
        dataDownDto.setPayload("FA");
        dataDownDto.setContentType("HEXA");
        dataDownDto.setConfirmed(true);
        dataDownDto.setTtl(1000);
        dataDownDto.setMaxAttempts(1);
        LOG.info("Token: " + token);
        sendCommand(dataDownDto);
    }

    public List<EndDeviceDto> getEndDevices() {
        ParameterizedTypeReference<PaginatedDto<EndDeviceDto>> returnType = new ParameterizedTypeReference<PaginatedDto<EndDeviceDto>>() {};
        List<EndDeviceDto> devicesList = new ArrayList<EndDeviceDto>();
        Optional<String> href = Optional.of(firstHref);
        while (href.isPresent()) {
            try {
                String url = kerlinkProperties.getBaseUrl() + href.get();
                ResponseEntity<PaginatedDto<EndDeviceDto>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                        httpEntity, returnType);
                PaginatedDto<EndDeviceDto> body = responseEntity.getBody();
                LOG.debug("Calling kerlink url {}, and got {} devices", url, body.getList().size());
                devicesList.addAll(body.getList());
                href = getNextPageHref(body.getLinks());
            } catch (Exception e) {
                LOG.error("Error while retrieve data from Kerlink platform, " + e);
                return Collections.emptyList();
            }
        }
        return devicesList;
    }

    public void sendCommand(@RequestBody DataDownDto dataDownDto) {
        String url = kerlinkProperties.getBaseUrl() + "/application/dataDown";
        HttpEntity<?> httpEntity = prepareHttpEntity(dataDownDto, token);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (Exception e) {
            LOG.error("Error while trying to send command to Kerlink device, ", e);
            System.exit(1);
        }
    }

    private HttpEntity<Void> prepareHttpEntity(String token) {
        //LOG.info("Token: " + token);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json,application/vnd.kerlink.iot-v1+json");
        headers.set("Authorization", token);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        return httpEntity;
    }

    private <T> HttpEntity<?> prepareHttpEntity(T t, String token) {
        //LOG.info("Token: " + token);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", token);
        HttpEntity<?> httpEntity = new HttpEntity<>(t, headers);
        return httpEntity;
    }

    private Optional<String> getNextPageHref(List<LinkDto> links) {
        return links.stream().filter(l -> l.getRel().equals("next")).findFirst().map(l -> l.getHref());
    }

    private RestTemplate prepareRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
        return restTemplate;
    }
}