package com.devbhaskar.gsd_kafka.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
    private String project;
    private String timestamp;
    private String id;
    private String system;
    private String locode;
    private String version;
    private String type;
    private AlarmData data;
    @JsonProperty("receipt_timestamp")
    private String receiptTimestamp;
    @JsonProperty("publish_timestamp")
    private String publishTimestamp;
}
