package org.acme.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.EventDTO;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@ApplicationScoped
public class IncomingConsumer implements IKafkaConfig {

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "kafka.event-incoming.topic")
    String topicName;

    @ConfigProperty(name = "kafka.event-incoming.group.id")
    String groupId;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServer;

    private KafkaConsumer<String, String> kafkaConsumer;

    private AtomicBoolean paused = new AtomicBoolean(false);

    public void runListener() {
        System.out.println("Inicializando...");
        try {
            while (true) {
                System.out.println(this.kafkaConsumer.paused());

                if (paused.get() && this.kafkaConsumer.paused().isEmpty()) {
                    Set<TopicPartition> topicPartitions = this.kafkaConsumer.assignment();

                    this.kafkaConsumer.pause(topicPartitions);
                }

                if (!paused.get() && !this.kafkaConsumer.paused().isEmpty()) {
                    Set<TopicPartition> topicPartitions = this.kafkaConsumer.assignment();

                    this.kafkaConsumer.resume(topicPartitions);
                }

                if (!paused.get()) {
                    ConsumerRecords<String, String> records = this.kafkaConsumer.poll(Duration.ofMillis(1000));
                    records.forEach(this::process);
                }
            }
        } catch (Exception e) {
            System.out.println("Deu ruim");
            throw e;
        } finally {
            this.kafkaConsumer.close();
        }
    }

    public void subscribe() {
        System.out.println("Subscribe in topic");

        if (this.getConsumer().subscription().isEmpty()) {
            this.getConsumer().subscribe(List.of(topicName));
        }
    }

    public void pause() {
        System.out.println("Kafka paused");

        paused.set(true);
    }

    public void resume() {
        System.out.println("Kafka resumed");

        paused.set(false);
    }

    private List<TopicPartition> getAllTopics() {
        List<PartitionInfo> topicPartitionInfos = this.kafkaConsumer.listTopics().get(topicName);

        return topicPartitionInfos.stream()
                .map(info -> new TopicPartition(info.topic(), info.partition()))
                .collect(Collectors.toList());
    }

    private void process(ConsumerRecord<String, String> message) {
        try {
            EventDTO eventDTO = objectMapper.readValue(message.value(), EventDTO.class);

            System.out.println("Recebido mensagem do produtor" + eventDTO.getDescription());
        } catch (Exception e) {
            System.out.println("Deu ruim");
        } finally {
            this.kafkaConsumer.commitSync();
        }
    }

    private KafkaConsumer<String, String> getConsumer() {
        if (Objects.nonNull(kafkaConsumer)) {
            return this.kafkaConsumer;
        }

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getProperties(groupId, bootstrapServer));

        setKafkaConsumer(consumer);
        return this.kafkaConsumer;
    }

    public void setKafkaConsumer(KafkaConsumer<String, String> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

}
