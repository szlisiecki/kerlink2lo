/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.orange.lo.sample.kerlink2lo.kerlink.api.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.EndDeviceDto;

import com.orange.lo.sample.kerlink2lo.lo.LoDeviceProvider;

@SpringBootApplication
public class Kerlink2LoApplication {

	private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private KerlinkApi kerlinkApi;
	private LoDeviceProvider loDeviceProvider;
	Map<String, String> properties = Stream.of(new String[][] {
			{ "platform", "kerlink" },
	}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	
	public Kerlink2LoApplication(KerlinkApi kerlinkApi, LoDeviceProvider loDeviceProvider) {
		this.kerlinkApi = kerlinkApi;
		this.loDeviceProvider = loDeviceProvider;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Kerlink2LoApplication.class, args);		
	}
//	
//	@Bean
//	public CommandLineRunner commandLineRunner() {
//		
//		List<EndDeviceDto> endDevices = kerlinkApi.getEndDevices();
//		
//		LOG.info("Devices from Kerlink");
//		for (EndDeviceDto endDeviceDto : endDevices) {
//			LOG.info(endDeviceDto.toString());
//		}
//		return null;
//	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		LOG.info("Devices from LO: ");
		LOG.info(loDeviceProvider.getDevices().toString());
		
		LOG.info("Devices from Kerlink");
		for (EndDeviceDto endDeviceDto : kerlinkApi.getEndDevices()) {
			LOG.info(endDeviceDto.toString());
		}
		return null;
	}

	/*@Bean
	public CommandLineRunner commandLineRunner() {
		LOG.info("Adding device to LO...");
		loDeviceProvider.addDevice("urn:lo:nsid:sensor:temp10", "temp10", properties);
		return null;
	}*/

	/*@Bean
	public CommandLineRunner commandLineRunner() {
		LOG.info("Adding device to LO...");
		loDeviceProvider.deleteDevice("urn:lo:nsid:sensor:temp001");
		return null;
	}*/
}
