package com.orange.lo.sample.kerlink2lo.lo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
@RestClientTest(LoDeviceProvider.class)
public class LoDeviceProviderTest2 {

    LoDeviceProvider loDeviceProvider;

    @Mock
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        LoProperties loProperties = new LoProperties();
        HttpHeaders httpHeaders = null;
        loProperties.setApiUrl("localhost/v1/deviceMgt/devices?limit=2&offset=0&groupId=null&fields=id,name,group");
        loProperties.setPageSize(20);
        loProperties.setDeviceGroupId("kerlink");
        loDeviceProvider = new LoDeviceProvider(loProperties, httpHeaders, restTemplate);
    }

    @Test
    public void shouldGet4Devices() {
        LoDevice[] devices = new LoDevice[] {new LoDevice(), new LoDevice()};
        ResponseEntity<LoDevice[]> response = new ResponseEntity<LoDevice[]>(devices, getHeadersWithRemainingAndReset(4, 3, 3), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), anyObject(), eq(LoDevice[].class)))
                .thenReturn(response);
        int count = loDeviceProvider.getDevices().size();
        Assert.assertEquals(4, count);
    }

    @Test
    public void shouldGetNoDevices() {
        LoDevice[] devices = new LoDevice[0];
        ResponseEntity<LoDevice[]> response = new ResponseEntity<LoDevice[]>(devices, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), anyObject(), eq(LoDevice[].class)))
                .thenReturn(response);
        int devicesCount = loDeviceProvider.getDevices().size();
        Assert.assertEquals(0, devicesCount);
    }

    @Test
    public void shouldGetTotalCountDevices() {
        int totalCount = 5;
        LoDevice[] devices = new LoDevice[totalCount];
        for (int i = 0;  i < totalCount; i++)
            devices[i] = new LoDevice();
        ResponseEntity<LoDevice[]> response = new ResponseEntity<>(devices, getHeadersWithTotalCount(totalCount), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), anyObject(), eq(LoDevice[].class)))
                .thenReturn(response);
        int devicesCount = loDeviceProvider.getDevices().size();
        Assert.assertEquals(totalCount, devicesCount);
    }

    @Test
    public void shouldSleep() {
        int totalCount = 5;
        int count = 3;
        String remain = "0";
    }

    private MultiValueMap<String, String> getHeadersWithTotalCount(long count) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Total-Count", String.valueOf(count));
        return headers;
    }

    private MultiValueMap<String, String> getHeadersWithRemainingAndReset(long count, long remaining, long reset) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Total-Count", String.valueOf(count));
        headers.add("X-Ratelimit-Remaining", String.valueOf(remaining));
        headers.add("X-Ratelimit-Reset", String.valueOf(reset));
        return headers;
    }


}
