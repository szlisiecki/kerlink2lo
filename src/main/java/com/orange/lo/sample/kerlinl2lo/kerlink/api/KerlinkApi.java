package com.orange.lo.sample.kerlinl2lo.kerlink.api;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

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
import org.springframework.web.client.RestTemplate;

import com.orange.lo.sample.kerlinl2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlinl2lo.kerlink.api.model.EndDeviceDto;
import com.orange.lo.sample.kerlinl2lo.kerlink.api.model.JwtDto;
import com.orange.lo.sample.kerlinl2lo.kerlink.api.model.PaginatedDto;
import com.orange.lo.sample.kerlinl2lo.kerlink.api.model.UserDto;

@Component
@EnableScheduling
public class KerlinkApi {

	private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private RestTemplate restTemplate;
	private String token;
	
	private KerlinkProperties kerlinkProperties;
	
	@Autowired
	public KerlinkApi(KerlinkProperties kerlinkProperties) {
		this.restTemplate = new RestTemplate();
		this.kerlinkProperties = kerlinkProperties;
		
		login();
	}
	
	@Scheduled(fixedRate = 32400000) // every 9 hours
	public void login() {	
		LOG.info("Get bearer token");
		UserDto userDto = new UserDto();
		userDto.setLogin(kerlinkProperties.getLogin());
		userDto.setPassword(kerlinkProperties.getPassword());
		
		String url = kerlinkProperties.getBaseUrl() + "/gms/application/login";
		
		ResponseEntity<JwtDto> responseEntity = restTemplate.postForEntity(url, userDto, JwtDto.class);
		
		if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
			this.token = "Bearer " + responseEntity.getBody().getToken();
		} else {
			//TODO handle exceptions
		}
	}
	
	public List<EndDeviceDto> getEndDevices() {
		String url =  kerlinkProperties.getBaseUrl() + "/gms/application/endDevices";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json,application/vnd.kerlink.iot-v1+json");
		headers.set("Authorization", token);		
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
		
		ParameterizedTypeReference<PaginatedDto<EndDeviceDto>> returnType = new ParameterizedTypeReference<PaginatedDto<EndDeviceDto>>() {};
		ResponseEntity<PaginatedDto<EndDeviceDto>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, returnType);
		
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody().getList();
		} else {
			//TODO handle exceptions
			return Collections.emptyList();
		}
	}
}