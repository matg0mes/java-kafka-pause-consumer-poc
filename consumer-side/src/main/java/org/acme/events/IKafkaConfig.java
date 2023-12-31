package org.acme.events;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

@ApplicationScoped
public interface IKafkaConfig {

    void runListener();

    void subscribe();

    default Properties getProperties(String groupId, String bootstrapServer, String autoResetConfig) {
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoResetConfig);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

        return properties;
    }

}
