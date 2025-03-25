package com.devbhaskar.gsd_kafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devbhaskar.gsd_kafka.service.KafkaProducer;

@RestController
@RequestMapping("/rest/api")
public class FetchMessageFromClient {

	@Autowired
	KafkaProducer kafkaProducer;

	@GetMapping(value = "/producer")
	public String sendMessage(@RequestParam("message") String message) {
		kafkaProducer.sendMessageToTopic(message);
		return "Message sent Successfully to the your code decode topic ";
	}

}