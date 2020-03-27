package com.orange.lo.sample.kerlink2lo.lo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class LoDeviceProviderTest2 {

    LoDeviceProvider loDeviceProvider;

    @Mock
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        LoProperties loProperties = new LoProperties();
        HttpHeaders httpHeaders = null;
        loProperties.setDevicesUrl("localhost/v1/deviceMgt/devices?limit=2&offset=0&groupId=null&fields=id,name,group");
        loProperties.setPageSize(20);
        loProperties.setDeviceGroupId("kerlink");
        loDeviceProvider = new LoDeviceProvider(loProperties, httpHeaders);
    }

    @Test
    public void shouldGetDevices() {
        LoDevice loDevice1 = new LoDevice();
        LoDevice loDevice2 = new LoDevice();
        LoDevice[] devices = new LoDevice[2];
        devices[0] = loDevice1;
        devices[1] = loDevice2;
        ResponseEntity<LoDevice[]> response = new ResponseEntity<LoDevice[]>(devices, getHeaders(3, 3, 3), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), anyObject(), eq(LoDevice[].class)))
                .thenReturn(response);
        LoDevice loDevice = loDeviceProvider.getDevices().get(0);
        Assert.assertEquals(loDevice, devices[0]);
    }

    private MultiValueMap<String, String> getHeaders(long count, long remaining, long reset) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Total-Count", String.valueOf(count));
        headers.add("X-Ratelimit-Remaining", String.valueOf(remaining));
        headers.add("X-Ratelimit-Reset", String.valueOf(reset));
        return headers;
    }
}