package com.orange.lo.sample.kerlink2lo;

import com.orange.lo.sample.kerlink2lo.kerlink.api.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceProvider;
import com.orange.lo.sample.kerlink2lo.lo.LoProperties;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@EnableScheduling
@Component
public class IotDeviceManagement {

    private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private KerlinkApi kerlinkApi;
    private LoDeviceProvider loDeviceProvider;
    private LoProperties loProperties;
    private Map<String, String> deviceProperties;
    private static final String LO_DEVICE_ID_PREFIX = "urn:lo:nsid:sensor:";

    public IotDeviceManagement(KerlinkApi kerlinkApi, LoDeviceProvider loDeviceProvider, LoProperties loProperties) {
        this.kerlinkApi = kerlinkApi;
        this.loDeviceProvider = loDeviceProvider;
        this.loProperties = loProperties;
        this.deviceProperties = Collections.singletonMap(loProperties.getPropertiesKey(),
                loProperties.getPropertiesValue());
    }

    @Scheduled(fixedRateString = "${lo.synchronization-device-interval}")
    public void synchronizeDevices() throws InterruptedException {
        LOG.info("Synchronizing devices... ");
        Set<String> kerlinkIds = kerlinkApi.getEndDevices().stream().map(d -> d.getDevEui())
                .collect(Collectors.toSet());
        LOG.debug("Devices from Kerlink: {}", kerlinkIds.toString());
        Set<String> loIds = loDeviceProvider.getDevices().stream().map(d -> d.getId()).collect(Collectors.toSet());
        LOG.debug("Devices from LO: {}", loIds.toString());
        // add devices to LO
        Set<String> devicesToAddToLo = new HashSet<String>(kerlinkIds).stream().map(id -> LO_DEVICE_ID_PREFIX + id).collect(Collectors.toSet());
        devicesToAddToLo.removeAll(loIds);
        LOG.debug("Devices to add to LO: {}", devicesToAddToLo.toString());
        // remove devices from LO
        Set<String> devicesToRemoveFromLo = new HashSet<String>(loIds);
        devicesToRemoveFromLo.removeAll(kerlinkIds.stream().map(kerlinkId -> LO_DEVICE_ID_PREFIX + kerlinkId).collect(Collectors.toSet()));
        LOG.debug("Devices to remove from LO: {}", devicesToRemoveFromLo.toString());
        if (devicesToAddToLo.size() + devicesToRemoveFromLo.size() > 0) {
            ThreadPoolExecutor synchronizingExecutor = new ThreadPoolExecutor(
                    loProperties.getSynchronizationThreadPoolSize(), loProperties.getSynchronizationThreadPoolSize(),
                    10, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(devicesToAddToLo.size() + devicesToRemoveFromLo.size()));
            for (String deviceId : devicesToAddToLo) {
                synchronizingExecutor.execute(() -> {
                    loDeviceProvider.addDevice(deviceId, deviceId.substring((deviceId.lastIndexOf(":") + 1)),
                            deviceProperties);
                    LOG.debug("Device created for {}", deviceId);
                });
            }
            for (String deviceId : devicesToRemoveFromLo) {
                synchronizingExecutor.execute(() -> {
                    loDeviceProvider.deleteDevice(deviceId);
                    LOG.debug("Device deleted for {}", deviceId);
                });
            }
        }
    }
}