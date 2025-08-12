package com.transaction.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.common.serialization.StringSerializer;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

import com.transaction.challenge.Repository.TransactionRepository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"transactions"})
public class TransactionConsumerTest {
    
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void testTransactionConsumer() throws Exception {
        var props = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        var pf = new DefaultKafkaProducerFactory<String, String>(props, new StringSerializer(), new StringSerializer());
        KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);

        String key = "00000000-0000-0000-0000-000000000001";
        String json = """
{"id":"00000000-0000-0000-0000-000000000001","customerId":"P-1",
 "accountIban":"CH93-...","currencyAmount":"CHF 100-","valueDate":"2020-10-01","description":"Test"}
""";

        try {
            template.send("transactions", key, json).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }

        template.flush();

        await().atMost(java.time.Duration.ofSeconds(10))
        .untilAsserted(() -> assertThat(transactionRepository.count()).isEqualTo(1));
    }
}
