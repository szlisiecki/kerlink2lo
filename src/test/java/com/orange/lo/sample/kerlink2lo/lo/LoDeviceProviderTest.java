package com.orange.lo.sample.kerlink2lo.lo;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

//@RunWith(SpringRunner.class)
@RestClientTest(LoDeviceProvider.class)
@SpringBootTest
public class LoDeviceProviderTest {

    @Autowired
    LoDeviceProvider loDeviceProvider;

    @Autowired
    private LoProperties loProperties;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        String loDeviceString = objectMapper.writeValueAsString(
                new LoDevice("urn:lo:nsid:x-connector:0018B20000002345", null, null, true));
        server.expect(requestTo(
                "https://liveobjects.orange-business.com/api/v1/deviceMgt/devices")).andRespond(
                        withSuccess(loDeviceString, MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenCallingGetDevices_thenClientMakesCorrectCall() {
        LoDevice device = loDeviceProvider.getDevices().get(0);
        assertEquals(device.getId(), "urn:lo:nsid:x-connector:0018B20000002345");
    }
}