package com.devbhaskar.gsd_kafka.DTO;

public class AlarmDetails {

	private String alarmUniqueId;
	private String system;
    private String alarmNumber;
    private String alarmTitle;
    private String senderId;
    private String assetId;
    private String assetType;
    private String alarmSenderId;
    private String alarmStatus;
    private String alarmChannelId;
    
	public String getAlarmUniqueId() {
		return alarmUniqueId;
	}
	public void setAlarmUniqueId(String alarmUniqueId) {
		this.alarmUniqueId = alarmUniqueId;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getAlarmSenderId() {
		return alarmSenderId;
	}
	public void setAlarmSenderId(String alarmSenderId) {
		this.alarmSenderId = alarmSenderId;
	}
	public String getAlarmStatus() {
		return alarmStatus;
	}
	public void setAlarmStatus(String alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
	public String getAlarmChannelId() {
		return alarmChannelId;
	}
	public void setAlarmChannelId(String alarmChannelId) {
		this.alarmChannelId = alarmChannelId;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getAlarmNumber() {
		return alarmNumber;
	}
	public void setAlarmNumber(String alarmNumber) {
		this.alarmNumber = alarmNumber;
	}
	public String getAlarmTitle() {
		return alarmTitle;
	}
	public void setAlarmTitle(String alarmTitle) {
		this.alarmTitle = alarmTitle;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
}
