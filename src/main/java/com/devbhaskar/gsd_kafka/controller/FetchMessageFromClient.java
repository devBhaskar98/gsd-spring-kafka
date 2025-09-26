package com.devbhaskar.gsd_kafka.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devbhaskar.gsd_kafka.service.KafkaProducer;

@RestController
@RequestMapping("/kafka")
public class FetchMessageFromClient {

	private volatile boolean keepRunning = false;

	@Autowired
	KafkaProducer kafkaProducer;

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

	@GetMapping(value = "/producer")
	public String sendMessage(@RequestParam("message") String message) {
		kafkaProducer.sendMessageToTopic(message);
		return "Message sent Successfully to the your code decode topic ";
	}

	@GetMapping("/producer/start")
	public String startLoop(@RequestParam("maxAlarmId") int maxAlarmId, @RequestParam("timeout") int timeout) {
		if (keepRunning) {
			return "Loop already running.";
		}

		AtomicInteger alarmId = new AtomicInteger(maxAlarmId);

		keepRunning = true;
		new Thread(() -> {
			while (keepRunning) {
				kafkaProducer.sendMessageToDMSTopic(buildMessage(maxAlarmId));
				logger.info("Sending message to topic no-03-osl.spo.dms.out.json: {}", maxAlarmId);
				alarmId.incrementAndGet();
				try {
					Thread.sleep(timeout); // simulate work
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			System.out.println("Loop stopped.");
		}).start();

		return "Loop started.";
	}

	@GetMapping("/producer/stop")
	public String stopLoop() {
		keepRunning = false;
		return "Loop stop signal sent.";
	}

	private String buildMessage(int alarmId) {
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		return """
				{
				  "message": {
				    "project": "nospor25",
				    "timestamp": "2025-04-14 12:37:00",
				    "id": "NO-03-OSL.L0951-VXD-A1.7.200010.1.1704075255000",
				    "system": "DurhamCoast",
				    "locode": "NO-03-OSL",
				    "version": "0.0.1",
				    "type": "RI_EVENTS",
				    "data": {
				      "alarm_type": "Alarminformation",
				      "alarm_timestamp": "2025-04-19T13:41:12Z",
				      "alarm_number": "22040",
				      "alarm_title": "AMy Alarm test %d",
				      "asset_id": "SNF7196",
				      "asset_type": "VXD",
				      "additional_properties": true,
				      "alarm_id": "22040",
				      "alarm_sender_id": "NRTN_TFL",
				      "dms_gen_alarm_id": "2204040001_XXSNFTT227199_Point SNF7196 Predicted to fail soon test bhaskar %d %s",
				      "alarm_status": "pending",
				      "alarm_url": "http://localhost/tgmt/documents/index-154_433_783.html",
				      "alarm_priority": 2,
				      "alarm_comment": "first-alatm-test",
				      "affected_component": "L0951-VXD-A1",
				      "source_id": "Vicos_Oslo_line1",
				      "alarm_channel_id": "DurhamCoast",
				      "sender_id": "DIAG_RMS_1"
				    },
				    "receipt_timestamp": "2025-06-08T05:47:23.245Z",
				    "publish_timestamp": "2025-06-08T05:47:24.561Z"
				  }
				}
				"""
				.formatted(alarmId, alarmId, timestamp);
	}
}