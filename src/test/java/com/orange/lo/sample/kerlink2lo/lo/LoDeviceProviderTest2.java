package com.orange.lo.sample.kerlink2lo.lo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@RestClientTest(LoDeviceProvider.class)
public class LoDeviceProviderTest2 {

    @Mock
    LoProperties loProperties;

    @Mock
    RestTemplate restTemplate;

    @Mock
    HttpHeaders httpHeaders;

    @Before
    public void setUp() {
        when(loProperties.getApiUrl()).thenReturn("localhost");
    }

    @Test
    public void shouldGetNoDevices() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), ArgumentMatchers.any(), eq(LoDevice[].class)))
                .thenReturn(getResponse(0, 0, 0, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(0, count);
    }

    @Test
    public void shouldGetNoDevicesWitLimitZero() {
        int limit = 0;
        int total = 1;
        when(loProperties.getPageSize()).thenReturn(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(limit, count);
    }

    @Test
    public void shouldGetDevicesWithLimitEqualsTotalCount() {
        int limit = 2;
        when(loProperties.getPageSize()).thenReturn(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, limit, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(limit, count);
    }

    @Test
    public void shouldGetDevicesWithLimitGreaterThanTotalCount() {
        int limit = 3;
        int total = 2;
        when(loProperties.getPageSize()).thenReturn(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(total, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(total, count);
    }

    @Test
    public void shouldGetDevicesWithLimitUndercostamOfTotalCount() {
        int limit = 2;
        int total = 4;
        when(loProperties.getPageSize()).thenReturn(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                        getResponse(limit, total, 1, 0));
        when(restTemplate.exchange(eq(getUrl(limit, 2)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                        getResponse(limit, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(total, count);
    }

    @Test
    public void shouldGetDevicesWithLimitNotUndercostamOfTotalCount() {
        int limit = 2;
        int total = 3;
        when(loProperties.getPageSize()).thenReturn(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 1, 0));
        when(restTemplate.exchange(eq(getUrl(limit, 2)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(total - limit, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(total, count);
    }

    @Test
    public void shouldGetDevicesOfQuantityGreaterThanTotalCount() {
        int limit = 2;
        int total = 3;
        when(loProperties.getPageSize()).thenReturn(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 1, 0));
        when(restTemplate.exchange(eq(getUrl(limit, 2)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(2 * limit, count);
    }

    @Test
    public void shouldGetDevicesAfterSleep() {
        int limit = 2;
        int total = 3;
        when(loProperties.getPageSize()).thenReturn(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 0, System.currentTimeMillis() + 10000));
        when(restTemplate.exchange(eq(getUrl(limit, 2)), eq(HttpMethod.GET),
                ArgumentMatchers.any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 1, 0));
        long start = System.currentTimeMillis();
        new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices();
        long stop = System.currentTimeMillis();
        Assert.assertTrue(stop - start >= 10000);
    }

    @Test
    public void shouldAddDevice() {
        String deviceId = "test";
        when(loProperties.getPageSize()).thenReturn(0);
        when(loProperties.getDeviceGroupId()).thenReturn("kerlink");
        when(loProperties.getDevicePrefix()).thenReturn("urn:lo:nsid:x-connector:");
        when(restTemplate.exchange(eq("localhost/v1/deviceMgt/devices"), eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<LoDevice>>any(), eq(Void.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        new LoDeviceProvider(loProperties, httpHeaders, restTemplate).addDevice(deviceId);
    }

    @Test
    public void shouldDeleteDevice() {
        String deviceId = "test";
        when(loProperties.getPageSize()).thenReturn(0);
        when(restTemplate.exchange(eq("localhost/v1/deviceMgt/devices/" + deviceId), eq(HttpMethod.DELETE),
                ArgumentMatchers.any(), eq(Void.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        new LoDeviceProvider(loProperties, httpHeaders, restTemplate).deleteDevice(deviceId);
    }

    private String getUrl(int limit, int offset) {
        return "localhost/v1/deviceMgt/devices?limit=" + limit +
                "&offset=" + offset + "&groupId=null&fields=id,name,group";
    }

    private ResponseEntity<LoDevice[]> getResponse(int devicesCount, int xTotalCount, int xRateLimitRemaining, long xRateLimitReset) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Total-Count", String.valueOf(xTotalCount));
        headers.add("X-Ratelimit-Remaining", String.valueOf(xRateLimitRemaining));
        headers.add("X-Ratelimit-Reset", String.valueOf(xRateLimitReset));
        return new ResponseEntity<>(returnDevices(devicesCount), headers, HttpStatus.OK);
    }

    private LoDevice[] returnDevices(int count) {
        LoDevice[] devices = new LoDevice[count];
        for (int i = 0;  i < count; i++)
            devices[i] = new LoDevice();
        return devices;
    }
}
