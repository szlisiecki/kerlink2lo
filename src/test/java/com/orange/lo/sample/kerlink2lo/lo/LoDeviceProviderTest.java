package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(value = SpringRunner.class)
@RestClientTest(LoDeviceProvider.class)
public class LoDeviceProviderTest {

    @Autowired
    LoDeviceProvider loDeviceProvider;

    @Mock
    LoProperties loProperties;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        String loDeviceString = objectMapper.writeValueAsString(
                new LoDevice("urn:lo:nsid:x-connector:0018B20000002345", null, null, true));
        server.expect(requestTo("https://liveobjects.orange-business.com/api/v1/deviceMgt/devices")).andRespond(withSuccess(loDeviceString, MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenCallingGetDevices_thenClientMakesCorrectCall() {
        LoDevice device = loDeviceProvider.getDevices().get(0);
        assertEquals(device.getId(), "urn:lo:nsid:x-connector:0018B20000002345");
    }
}