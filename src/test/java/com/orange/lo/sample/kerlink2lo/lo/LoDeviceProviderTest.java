package com.orange.lo.sample.kerlink2lo.lo;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class LoDeviceProviderTest {

    @Mock
    LoProperties loProperties;
    
    @Mock
    RestTemplate restTemplate;
    
    @Mock
    HttpHeaders httpHeaders;
    
    @Test
    public void shouldReturn5devices() {
        
        
        
        LoDevice loDevice1 = new LoDevice();
        LoDevice loDevice2 = new LoDevice();
        LoDevice loDevice3 = new LoDevice();
        
        LoDevice[] array2 = new LoDevice[2];
        array2[0] = loDevice1;
        array2[1] = loDevice2;
        
        LoDevice[] array1 = new LoDevice[1];
        array1[0] = loDevice3;
        
        
        MultiValueMap<String, String> headers = getHeaders(3, 3, 3);
        
        ResponseEntity<LoDevice[]> response2 = new ResponseEntity<LoDevice[]>(array2, headers, HttpStatus.OK);
        ResponseEntity<LoDevice[]> response1 = new ResponseEntity<LoDevice[]>(array1, headers, HttpStatus.OK);
    
        String testUrl0 = "localhost/v1/deviceMgt/devices?limit=2&offset=0&groupId=null&fields=id,name,group";
        String testUrl2 = "localhost/v1/deviceMgt/devices?limit=2&offset=2&groupId=null&fields=id,name,group";
        
        when(loProperties.getApiUrl()).thenReturn("localhost");
        when(loProperties.getPageSize()).thenReturn(2);
        
        when(restTemplate.exchange(eq(testUrl0), eq(HttpMethod.GET), any(), eq(LoDevice[].class))).thenReturn(response2);
        when(restTemplate.exchange(eq(testUrl2), eq(HttpMethod.GET), any(), eq(LoDevice[].class))).thenReturn(response1);
        
        
//        LoDeviceProvider loDeviceProvider = new LoDeviceProvider(loProperties, httpHeaders, restTemplate);
//        List<LoDevice> devices = loDeviceProvider.getDevices();
//        
//        assertEquals(3, devices.size());
    }
    
    private MultiValueMap<String, String> getHeaders(long count, long remaining, long reset) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(); 
        headers.add("X-Total-Count", String.valueOf(count));
        headers.add("X-Ratelimit-Remaining", String.valueOf(remaining));
        headers.add("X-Ratelimit-Reset", String.valueOf(reset));
        return headers;
    }
}
