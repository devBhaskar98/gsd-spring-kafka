package com.devbhaskar.gsd_kafka.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devbhaskar.gsd_kafka.DTO.AlarmData;
import com.devbhaskar.gsd_kafka.DTO.AlarmDetails;
import com.devbhaskar.gsd_kafka.DTO.Message;
import com.devbhaskar.gsd_kafka.DTO.MessageWrapper;
import com.devbhaskar.gsd_kafka.service.AlarmListService;
import com.devbhaskar.gsd_kafka.service.KafkaProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/kafka")
public class AlarmsReceiver {

	private volatile boolean keepRunning = false;

	@Autowired
	KafkaProducer kafkaProducer;
	
	@Autowired
	AlarmListService alarmListService;
	
	@Value("${kafka.config.topic-name}")
    private String topicName;

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	private Map<String, String> alarmHistoryMap = new ConcurrentHashMap<>();

//	@GetMapping(value = "/producer")
//	public String sendMessage(@RequestParam("message") String message) {
//		kafkaProducer.sendMessageToTopic(message);
//		return "Message sent Successfully to the your code decode topic ";
//	}
	
	@PostMapping(  "/producer/alarm")
	public String sendMessage(@RequestBody AlarmDetails alarmDetails, @RequestParam("maxAlarmId") int maxAlarmId) throws JsonProcessingException {
		kafkaProducer.sendMessageToDMSTopic(buildMessage(maxAlarmId, alarmDetails, "alarmBackend"));
		return buildMessage(maxAlarmId, alarmDetails, "alarmBackend");
	}

	/*
	 * @maxAlarmId -> helps in generating unique DMS Gen Alarm ID
	 * */
	@PostMapping("/producer/start")
	public String startLoop(@RequestBody AlarmDetails alarmDetails ,@RequestParam("maxAlarmId") int maxAlarmId, @RequestParam("timeout") int timeout) {
		if (keepRunning) {
			return "Loop already running.";
		}
		
		logger.info("Sending message to topic {}: {}", topicName, maxAlarmId);

		AtomicInteger alarmId = new AtomicInteger(maxAlarmId);
		keepRunning = true;
		
		new Thread(() -> {
			while (keepRunning) {
				try {
					logger.info("Sending message to topic {}: {}", topicName, maxAlarmId);
					kafkaProducer.sendMessageToDMSTopic(buildMessage(maxAlarmId, alarmDetails,"loopSingleAlarmBackend"));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
	
	/* Alternating list of alarms
	 * @maxAlarmId -> helps in generating unique DMS Gen Alarm ID
	 * */
	@PostMapping("/producer/start/listOfAlarms")
	public String startLoop(
	        @RequestBody List<AlarmDetails> alarmList,
	        @RequestParam("maxAlarmId") int maxAlarmId,
	        @RequestParam("timeout") int timeout) {

	    if (keepRunning) {
	        return "Loop already running.";
	    }
	    
	    

	    logger.info("Starting random alarm loop with {} alarms", alarmList.size());

	    keepRunning = true;

	    new Thread(() -> {
	        Random random = new Random();
	        AtomicInteger alarmId = new AtomicInteger(maxAlarmId);

	        while (keepRunning) {
	            try {
	                // Pick a random alarm from list
	                AlarmDetails selectedAlarm =alarmList.get(random.nextInt(alarmList.size()));
	                String randomStatus = Math.random() < 0.5 ? "pending" : "rectified";

	                int currentAlarmId = alarmId.getAndIncrement();

	                logger.info("Sending random alarmId={} -> {} with status as {}", currentAlarmId, selectedAlarm.getAssetId(), randomStatus);

	                // Build your Kafka message with custom alarm
	                String message = buildMessage(currentAlarmId, selectedAlarm, "loopListOfAlarmsBackend", randomStatus);

	                kafkaProducer.sendMessageToDMSTopic(message);

	                Thread.sleep(timeout);

	            } catch (Exception e) {
	                logger.error("Error sending alarm message", e);
	            }
	        }

	        logger.info("Loop stopped.");
	    }).start();

	    return "Loop started with random alarm generator.";
	}
	
	/* Alternating list of alarms
	 * @maxAlarmId -> helps in generating unique DMS Gen Alarm ID
	 * */
	@PostMapping("/producer/start/burstAlarms")
	public String startLoopForBurstAlarms(
	        @RequestBody List<AlarmDetails> alarmList,
	        @RequestParam("maxAlarmId") int maxAlarmId,
	        @RequestParam("timeout") int timeout,
	        @RequestParam("backendId") String backendUniqueId) {

	    if (keepRunning) {
	        return "Loop already running.";
	    }
	    
	    alarmHistoryMap = new ConcurrentHashMap<>();

	    logger.info("Starting burst alarm loop with {} alarms and cleared previous burst alarms from table - {} rows", alarmList.size(), alarmListService.deleteBurstAlarms(backendUniqueId));

	    keepRunning = true;
		    new Thread(() -> {
	//	        Random random = new Random();
		        AtomicInteger alarmId = new AtomicInteger(maxAlarmId);
		        AtomicInteger index = new AtomicInteger(0);
		       
	
		        while (keepRunning) {
		            try {
		                // Pick a random alarm from list
		            	int currentIndex = index.getAndIncrement();  // get FIRST, then increment
		                AlarmDetails selectedAlarm =alarmList.get(currentIndex);
		                
		                int currentAlarmId = alarmId.getAndIncrement();
	
		                logger.info("Sending burstAlarm for Index={} -> {} with status as {} and type {}", currentIndex, selectedAlarm.getAssetId(), selectedAlarm.getAlarmStatus(), selectedAlarm.getAlarmNumber());
	
		                // Build your Kafka message with custom alarm
		                String message = buildMessage(currentAlarmId, selectedAlarm, backendUniqueId);
	
		                kafkaProducer.sendMessageToDMSTopic(message);
	
		                Thread.sleep(timeout);
		                
	
		            } catch (Exception e) {
		            	 keepRunning = false;
		                logger.error("Error sending alarm message", e);
		            }
		        }

	        logger.info("Loop stopped.");
	    }).start();

	    return "Loop started with Burst alarm generator.";
	}

	@GetMapping("/producer/stop")
	public String stopLoop() {
		keepRunning = false;
		return "Loop stop signal sent.";
	}
	
	private String buildMessage(int alarmId, AlarmDetails alarmDetails, String backendId)
	        throws JsonProcessingException {
	    return buildMessage(alarmId, alarmDetails, backendId, null);
	}

	private String buildMessage(int alarmId,AlarmDetails alarmDetails, String backendId, String randomAlarmStatus) throws JsonProcessingException {

        // Current time in IST
        ZonedDateTime nowIst = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        // Convert to UTC
        ZonedDateTime nowUtc = nowIst.withZoneSameInstant(ZoneId.of("UTC"));

        // 1️⃣ ISO 8601 UTC with 3-digit milliseconds
        DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
        String isoUtcTimestamp = nowUtc.format(isoFormatter);

        // 2️⃣ Human-readable format
        DateTimeFormatter readableFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String alarm_timestamp = nowIst.format(readableFormatter);		
        
        String finalAlarmStatus = (randomAlarmStatus != null && !randomAlarmStatus.isBlank())
                ? randomAlarmStatus
                : alarmDetails.getAlarmStatus();
        
        String finalDmsGenAlarmId = createNewDmsId(alarmId, alarmDetails, backendId);
        
        if(alarmDetails.getAlarmStatus().equalsIgnoreCase("pending")) {
        	alarmHistoryMap.put(alarmDetails.getAlarmUniqueId(), finalDmsGenAlarmId);
        }
        
     // SUPPORT FOR BURST ALARM
        if(alarmHistoryMap != null && !alarmHistoryMap.isEmpty()) {
        	if(alarmDetails.getAlarmStatus().equalsIgnoreCase("rectified")) {
        		String dmsGenAlarmId = alarmHistoryMap.get(alarmDetails.getAlarmUniqueId());
            	if(dmsGenAlarmId != null) { // same alarm is already present
            		finalDmsGenAlarmId = dmsGenAlarmId;
            		alarmHistoryMap.remove(alarmDetails.getAlarmUniqueId());
            	}
        	}
        }
        
       // randomAlarmStatus is for alternate alarm list loop
        // not applicable for SingleAlarm and BurstAlarm
//        String finalDmsGenAlarmId = "Failed to Initalized";
//        if (randomAlarmStatus != null && !randomAlarmStatus.isBlank() && "rectified".equalsIgnoreCase(finalAlarmStatus)) {
//        	if (!alarmHistoryMap.isEmpty()) {
//        		Object[] keys = alarmHistoryMap.keySet().toArray();
//            	Integer randomKey = (Integer) keys[new Random().nextInt(keys.length)];
//
//            	 finalDmsGenAlarmId = alarmHistoryMap.get(randomKey);
//            	 
//            	 logger.info("Alarm Closing with DMS Gen Alarm Id {}",finalDmsGenAlarmId);
//            	alarmHistoryMap.remove(randomKey);
//        	}
//        } else {
//            finalDmsGenAlarmId = createNewDmsId(alarmId, alarmDetails);
//            alarmHistoryMap.put(alarmDetails.getAlarmUniqueId(), finalDmsGenAlarmId);
//        }
        
        
        
        

	    AlarmData data = AlarmData.builder()
	            .alarmType("Alarminformation")
	            .alarmTimestamp(isoUtcTimestamp)
	            .alarmNumber(alarmDetails.getAlarmNumber())
	            .alarmTitle(alarmDetails.getAlarmTitle() + ">>" + alarmId)
	            .assetId(alarmDetails.getAssetId())
	            .assetType(alarmDetails.getAssetType())
	            .additionalProperties(true)
	            .alarmId(alarmDetails.getAlarmNumber())
	            .alarmSenderId(alarmDetails.getAlarmSenderId())
	            .dmsGenAlarmId(finalDmsGenAlarmId)
	            .alarmStatus(finalAlarmStatus)
	            .alarmUrl("http://localhost/tgmt/documents/index-154_433_783.html")
	            .alarmPriority("2")
	            .alarmComment("first-alatm-test")
	            .affectedComponent("L0951-VXD-A1")
	            .sourceId("Vicos_Oslo_line1")
	            .alarmChannelId(alarmDetails.getAlarmChannelId())
	            .senderId(alarmDetails.getAlarmSenderId())
	            .build();

	    Message msg = Message.builder()
	            .project("nospor25")
	            .timestamp(alarm_timestamp)
	            .id("NO-03-OSL.L0951-VXD-A1.7.200010.1.1704075255000")
	            .system(alarmDetails.getSystem())
	            .locode("NO-03-OSL")
	            .version("0.0.1")
	            .type("RI_EVENTS")
	            .data(data)
	            .receiptTimestamp(isoUtcTimestamp)
	            .publishTimestamp(isoUtcTimestamp)
	            .build();

	    MessageWrapper wrapper = MessageWrapper.builder()
	            .message(msg)
	            .build();

	    return objectMapper.writeValueAsString(wrapper);
	}
	
	private String createNewDmsId(int alarmId, AlarmDetails alarmDetails, String backendId) {
	    String iso = ZonedDateTime.now(ZoneId.of("UTC"))
	            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	    return "GEN_" + alarmId + "_" +alarmDetails.getAssetId() +"_"+ iso+ "_" + backendId;
	}
}