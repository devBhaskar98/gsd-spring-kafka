package com.devbhaskar.gsd_kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessageToTopic(String message) {
		kafkaTemplate.send("CodeDecodeTopic", message);
	}

	public void sendMessageToDMSTopic(String message) {
		kafkaTemplate.send("no-03-osl.spo.dms.out.json", message);
	}

}