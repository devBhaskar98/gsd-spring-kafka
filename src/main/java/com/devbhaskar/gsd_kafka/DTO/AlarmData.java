package com.devbhaskar.gsd_kafka.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlarmData {
    @JsonProperty("alarm_type") private String alarmType;
    @JsonProperty("alarm_timestamp") private String alarmTimestamp;
    @JsonProperty("alarm_number") private String alarmNumber;
    @JsonProperty("alarm_title") private String alarmTitle;
    @JsonProperty("asset_id") private String assetId;
    @JsonProperty("asset_type") private String assetType;
    @JsonProperty("additional_properties") private boolean additionalProperties;
    @JsonProperty("alarm_id") private String alarmId;
    @JsonProperty("alarm_sender_id") private String alarmSenderId;
    @JsonProperty("dms_gen_alarm_id") private String dmsGenAlarmId;
    @JsonProperty("alarm_status") private String alarmStatus;
    @JsonProperty("alarm_url") private String alarmUrl;
    @JsonProperty("alarm_priority") private String alarmPriority;
    @JsonProperty("alarm_comment") private String alarmComment;
    @JsonProperty("affected_component") private String affectedComponent;
    @JsonProperty("source_id") private String sourceId;
    @JsonProperty("alarm_channel_id") private String alarmChannelId;
    @JsonProperty("sender_id") private String senderId;
}

