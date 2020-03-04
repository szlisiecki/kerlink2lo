/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataUpDto;
import com.orange.lo.sample.kerlink2lo.lo.model.CommandResponse;
import com.orange.lo.sample.kerlink2lo.lo.model.DataMessage;
import com.orange.lo.sample.kerlink2lo.lo.model.NodeStatus;
import com.orange.lo.sample.kerlink2lo.lo.model.NodeStatus.Capabilities;

import java.lang.invoke.MethodHandles;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExternalConnector {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private IMqttClient loMqttClient;
    private ObjectMapper objectMapper;
    private LoProperties loProperties;

    private static final String DATA_TOPIC_TEMPLATE = "connector/v1/nodes/%s/data";
    private static final String STATUS_TOPIC_TEMPLATE = "connector/v1/nodes/%s/status";
    private static final String COMMAND_RESPONSE_TOPIC = "connector/v1/responses/command";
    private static final String COMMAND_REQUEST_TOPIC = "connector/v1/requests/command";
    //TODO add subscriber

    public ExternalConnector(IMqttClient loMqttClient, ObjectMapper objectMapper, LoProperties loProperties) {
        this.loMqttClient = loMqttClient;
        this.objectMapper = objectMapper;
        this.loProperties = loProperties;
    }

    public void sendMessage(DataUpDto dataUpDto) {
        LOG.debug("Sending data message from device {}", dataUpDto.getEndDevice().getDevEui());

        DataMessage dataMessage = new DataMessage();
        dataMessage.setValue(dataUpDto);

        MqttMessage msg = prepareMqttMessgae(dataMessage);
        String topic = String.format(DATA_TOPIC_TEMPLATE, dataUpDto.getEndDevice().getDevEui());

        publish(topic, msg);
    }

    public void sendStatus(String devEui) {
        LOG.debug("Sending status message for device {}", devEui);

        NodeStatus nodeStatus = new NodeStatus();
        nodeStatus.setStatus("ONLINE");
        nodeStatus.setCapabilities(new Capabilities(true));

        MqttMessage msg = prepareMqttMessgae(nodeStatus);
        String topic = String.format(STATUS_TOPIC_TEMPLATE, devEui);

        publish(topic, msg);
    }

    public void sendCommandResponse(String commandId, String nodeId) {
        LOG.debug("Sending command response for device {}", nodeId);
        CommandResponse commandResponse = new CommandResponse(commandId, nodeId);

        MqttMessage msg = prepareMqttMessgae(commandResponse);
        publish(COMMAND_RESPONSE_TOPIC, msg);
    }

    private MqttMessage prepareMqttMessgae(Object dataMessage) {
        try {
            String payload = objectMapper.writeValueAsString(dataMessage);
            MqttMessage msg = new MqttMessage();
            msg.setQos(loProperties.getMessageQos());
            msg.setPayload(payload.getBytes());
            return msg;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
            //TODO create custom exception
        }
    }

    private void publish(String topic, MqttMessage msg) {
        try {
            loMqttClient.publish(topic, msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
            //TODO create custom exception
        }
    }
}
