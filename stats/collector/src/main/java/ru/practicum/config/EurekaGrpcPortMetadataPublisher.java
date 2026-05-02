package ru.practicum.config;

import com.netflix.appinfo.ApplicationInfoManager;
import net.devh.boot.grpc.server.event.GrpcServerStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EurekaGrpcPortMetadataPublisher {

    private static final Logger log = LoggerFactory.getLogger(EurekaGrpcPortMetadataPublisher.class);

    private final ApplicationInfoManager applicationInfoManager;
    private final EurekaRegistration eurekaRegistration;
    private final EurekaServiceRegistry eurekaServiceRegistry;

    public EurekaGrpcPortMetadataPublisher(ApplicationInfoManager applicationInfoManager,
                                           EurekaRegistration eurekaRegistration,
                                           EurekaServiceRegistry eurekaServiceRegistry) {
        this.applicationInfoManager = applicationInfoManager;
        this.eurekaRegistration = eurekaRegistration;
        this.eurekaServiceRegistry = eurekaServiceRegistry;
    }

    @EventListener
    public void onGrpcServerStarted(GrpcServerStartedEvent event) {
        String grpcPort = Integer.toString(event.getServer().getPort());

        Map<String, String> metadata = new HashMap<>(applicationInfoManager.getInfo().getMetadata());
        metadata.put("grpcPort", grpcPort);

        applicationInfoManager.registerAppMetadata(metadata);
        eurekaRegistration.getMetadata().put("grpcPort", grpcPort);
        eurekaServiceRegistry.register(eurekaRegistration);

        log.info("Published grpcPort={} to Eureka metadata for {}",
                grpcPort, applicationInfoManager.getInfo().getAppName());
    }
}