package com.devbhaskar.gsd_kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Value("${kafka.config.topic-name}")
    private String topicName;

	public void sendMessageToTopic(String message) {
		kafkaTemplate.send("CodeDecodeTopic", message);
	}

	public void sendMessageToDMSTopic(String message) {
		kafkaTemplate.send(topicName, message);
	}

}