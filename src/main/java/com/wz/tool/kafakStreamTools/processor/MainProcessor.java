package com.wz.tool.kafakStreamTools.processor;

import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

//import com.wz.tool.kafakStreamTools.util.AvroConvertor;
//import io.confluent.connect.jms.Value;
import lombok.extern.slf4j.Slf4j;

@Component
@PropertySource("classpath:bootstrap.properties")
@Slf4j
public class MainProcessor {
    @Value("${sample.payload.1}")
    private String data;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    @Bean
    Supplier<Message<JSONObject>> process() {
        return () -> {
            String randomKey = "POD3_" + String.format((Locale)null, //don't want any thousand separators
                    "52%02d-%04d-%04d-%04d",
                    RANDOM.nextInt(100),
                    RANDOM.nextInt(10000),
                    RANDOM.nextInt(10000),
                    RANDOM.nextInt(10000));

            JSONParser parser = new JSONParser();
            JSONObject value  = null;

            try {
                value = (JSONObject) parser.parse(data);

                log.info("key : {}", randomKey);
                log.info("value : {}", value.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Message<JSONObject> msg = MessageBuilder.withPayload(value)
                    .setHeader(KafkaHeaders.MESSAGE_KEY, randomKey)
                    .build();

            return msg;
        };
    }
}
 