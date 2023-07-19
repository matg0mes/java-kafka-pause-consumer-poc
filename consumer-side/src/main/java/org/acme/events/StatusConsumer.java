package org.acme.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.StatusDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class StatusConsumer implements IKafkaConfig {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    IncomingConsumer incomingConsumer;

    @ConfigProperty(name = "kafka.status-incoming.topic")
    String topicName;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServer;

    private KafkaConsumer<String, String> kafkaConsumer;

    private AtomicBoolean paused = new AtomicBoolean(false);

    public void runListener() {
        System.out.println("StatusConsumer:: Inicializando...");
        try {
            while (true) {
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
            System.out.println("StatusConsumer:: Deu ruim\n" + e.getMessage() + e.getStackTrace());
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

    private void process(ConsumerRecord<String, String> message) {
        try {
            StatusDTO statusDTO = objectMapper.readValue(message.value(), StatusDTO.class);

            System.out.println("Recebido mensagem do produtor:: " + statusDTO.getDescription());

            if (statusDTO.getPaused()) {
                incomingConsumer.pause();
            } else {
                incomingConsumer.resume();
            }
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

        // Different consumer group for consumers...
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getProperties(
                UUID.randomUUID().toString(),
                bootstrapServer
        ));

        setKafkaConsumer(consumer);
        return this.kafkaConsumer;
    }

    public void setKafkaConsumer(KafkaConsumer<String, String> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

}
