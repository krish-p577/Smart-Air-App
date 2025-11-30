package com.SmartAir.ParentDashboard.model;


import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class CheckInModel {
    // Unique document ID (often set after retrieval)
    public String id;

    // The timestamp when the user performed the check-in (for filtering and sorting)
    @ServerTimestamp
    public Date timestamp;

    public String childId;
    public String enteredBy; // "Child" or "Parent"

    public String zone; // "Red", "Yellow", "Green"
    public int rescueCount; // Number of rescue inhaler uses that day
    public boolean controllerTaken; // Simple flag for adherence on this day
    public String symptomSummary; // e.g., "Cough and Wheeze"
    public List<String> triggers; // e.g., ["Exercise", "Cold Air"]

    // Triage Incidents (optional, only if notable)
    public boolean notableTriageIncident;
    public String triageNote; // Details of the incident

    // Default public constructor for Firestore deserialization
    public CheckInModel() {}


}
