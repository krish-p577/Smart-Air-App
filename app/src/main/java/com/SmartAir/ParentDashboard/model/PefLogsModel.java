package com.SmartAir.ParentDashboard.model;

import java.util.Date;

public class PefLogsModel {
    public String zone;

    private String childId;

    private Date timestamp;
    public PefLogsModel() {}

    public PefLogsModel(String zone, String childId, Date timestamp) {
        this.zone = zone;
        this.childId = childId;
        this.timestamp = timestamp;
    }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
