package com.umc.sp.contents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class IntegrationTestDockerServicesInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext>, ApplicationListener<ContextClosedEvent> {

    private final static String TEST_RESOURCES_DOCKER_COMPOSE_YML = "src/test/resources/docker-compose.yml";
    private static final List<ServiceConfig> SERVICES_PROPERTIES = List.of(//new ServiceConfig("redis", 6379, false, Duration.ofSeconds(60))
                                                                           //, new ServiceConfig("elasticsearch", 9200, false, Duration.ofSeconds(60))
                                                                           );
    private final static ComposeContainer ENVIRONMENT = initEnvironment();

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        ENVIRONMENT.start();

        var servicesEnvProperties = new HashMap<String, String>();
        SERVICES_PROPERTIES.forEach(serviceConfig -> {
            var servicePort = ENVIRONMENT.getServicePort(serviceConfig.name, serviceConfig.port);
            var serviceHost = ENVIRONMENT.getServiceHost(serviceConfig.name, serviceConfig.port);

            servicesEnvProperties.put(serviceConfig.name + ".host", serviceHost);
            servicesEnvProperties.put(serviceConfig.name + ".port", servicePort.toString());
        });

        log.info("******************" + servicesEnvProperties);
        TestPropertyValues.of(servicesEnvProperties).applyTo(applicationContext.getEnvironment());
    }

    @Override
    public void onApplicationEvent(final ContextClosedEvent event) {
        ENVIRONMENT.stop();
    }

    private static ComposeContainer initEnvironment() {
        var composeContainer = new ComposeContainer(new File(TEST_RESOURCES_DOCKER_COMPOSE_YML));

        SERVICES_PROPERTIES.forEach(serviceConfig -> {
            setExposedService(composeContainer, serviceConfig.name, serviceConfig.port, serviceConfig.showLog, serviceConfig.startupTimeout);
        });

        return composeContainer;
    }

    private static void setExposedService(final ComposeContainer composeContainer,
                                          final String serviceName,
                                          final Integer servicePort,
                                          final boolean showLog,
                                          final Duration startupTimeout) {
        composeContainer.withExposedService(serviceName, servicePort).waitingFor(serviceName, Wait.forListeningPort()).withStartupTimeout(startupTimeout);
        if (showLog) {
            composeContainer.withLogConsumer(serviceName, new Slf4jLogConsumer(log));
        }
    }

    record ServiceConfig(String name, Integer port, boolean showLog, Duration startupTimeout) {
    }
}
