package com.orange.lo.sample.kerlink2lo.lo;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

@RunWith(SpringRunner.class)
@RestClientTest(LoDeviceProvider.class)
//@SpringBootTest
public class LoDeviceProviderTest {

    private MockRestServiceServer server;

    @Before
    public void setUp() throws Exception {
        //loProperties.setDevicesUrl("/url");
        ObjectMapper objectMapper = new ObjectMapper();
        String loDeviceString = objectMapper.writeValueAsString(
                new LoDevice("urn:lo:nsid:x-connector:0018B20000002345", null, null, true));
        server.expect(requestTo(anyString())).andRespond(withSuccess(loDeviceString, MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetMessage() {
        server.expect(requestTo("http://google.com")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("resultSuccess", MediaType.TEXT_PLAIN));

        //String result = simpleRestService.getMessage();

        server.verify();
        //assertThat(result, allOf(containsString("SUCCESS"), containsString("resultSuccess")));
    }
}