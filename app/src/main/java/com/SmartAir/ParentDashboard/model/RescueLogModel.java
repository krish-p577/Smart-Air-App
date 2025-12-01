package com.SmartAir.ParentDashboard.model;
import com.google.firebase.Timestamp;

public class RescueLogModel {
    private String childid;
    private Timestamp timestamp; // Use Firestore Timestamp, NOT java.sql.Timestamp

    public RescueLogModel() {} // no-arg constructor required

    public String getChildid() { return childid; }
    public void setChildid(String childid) { this.childid = childid; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
