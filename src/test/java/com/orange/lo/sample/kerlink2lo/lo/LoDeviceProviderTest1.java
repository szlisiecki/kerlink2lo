package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
//@RestClientTest(LoDeviceProvider.class)
@SpringBootTest
public class LoDeviceProviderTest1 {

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    LoProperties loProperties;

    @Autowired
    private LoDeviceProvider loDeviceProvider;

    @Test
    public void testGetDevices() {
        LoDevice device = new LoDevice();
        ResponseEntity<LoDevice[]> myEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<LoDevice[]>>any())).thenReturn(myEntity);
        List<LoDevice> res = loDeviceProvider.getDevices();
        //Assert.assertEquals(myobjectA, res.get(0));
    }
}
