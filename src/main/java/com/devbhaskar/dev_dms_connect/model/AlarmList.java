package com.devbhaskar.gsd_kafka.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "\"AlarmList\"")
public class AlarmList {

    @Id
    @Column(name = "\"AlarmID\"")
    private Long alarmId;

    @Column(name = "\"AlarmNumber\"")
    private String alarmNumber;

    @Column(name = "\"Equipment\"")
    private String equipment;

    @Column(name = "\"AlarmDateTime\"")
    private LocalDateTime alarmDateTime;

    @Column(name = "\"GeoLocation\"")
    private String geoLocation;

    @Column(name = "\"SchemeID\"")
    private String schemeId;

    @Column(name = "\"alarmcleartime\"")
    private LocalDateTime alarmClearTime;

    @Column(name = "\"EquipmentId\"")
    private Long equipmentId;

    @Column(name = "\"LocationId\"")
    private Long locationId;

    @Column(name = "\"CaseId\"")
    private Long caseId;

    @Column(name = "\"diagnosticsystemreference\"")
    private String diagnosticSystemReference;

    @Column(name = "\"AlarmUniqueId\"")
    private String alarmUniqueId;

    @Column(name = "\"AlarmSource\"")
    private String alarmSource;

    @Column(name = "\"IsAlarmOpen\"")
    private Boolean isAlarmOpen;

    @Column(name = "\"workOrderId\"")
    private Long workOrderId;

    @Column(name = "\"AlarmDescription\"")
    private String alarmDescription;

    @Column(name = "\"AnalyticsLink\"")
    private String analyticsLink;

    @Column(name = "\"isDeleted\"")
    private Boolean isDeleted;
}

