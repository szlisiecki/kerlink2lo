/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlinl2lo;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.orange.lo.sample.kerlinl2lo.kerlink.api.KerlinkApi;
import com.orange.lo.sample.kerlinl2lo.kerlink.api.model.EndDeviceDto;

@SpringBootApplication
public class Kerlink2LoApplication {

	private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private KerlinkApi kerlinkApi;
	
	public Kerlink2LoApplication(KerlinkApi kerlinkApi) {
		this.kerlinkApi = kerlinkApi;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Kerlink2LoApplication.class, args);		
	}
	
	@Bean
	public CommandLineRunner commandLineRunner() {
		
		List<EndDeviceDto> endDevices = kerlinkApi.getEndDevices();
		
		LOG.info("RECEIVED DEVICES");
		for (EndDeviceDto endDeviceDto : endDevices) {
			LOG.info(endDeviceDto.toString());
		}
		return null;
	}
}
