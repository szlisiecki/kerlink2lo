package com.orange.lo.sample.kerlink2lo.kerlink.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.EndDeviceDto;
import com.orange.lo.sample.kerlink2lo.lo.LoDevice;

import java.util.List;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class KerlinkApiTest {

    @Mock
    RestTemplate restTemplate;
    
    public void shouldGetAllDevices() {
        KerlinkProperties kerlinkProperties = new KerlinkProperties();
        kerlinkProperties.setPageSize(10);
        kerlinkProperties.setBaseUrl("localhost");
        
        when(restTemplate.exchange(eq(testUrl0), eq(HttpMethod.GET), any(), eq(LoDevice[].class))).thenReturn(response2);
        
        KerlinkApi kerlinkApi = new KerlinkApi(kerlinkProperties, restTemplate);
        
        
        List<EndDeviceDto> endDevices = kerlinkApi.getEndDevices();
    }
}
