package com.SmartAir.ParentDashboard.view;

import java.util.List;

// Data container for the report
    public class AsthmaReportData {
        public String childName;
        public String currentZone;
        public int adherenceScore;
        public String period; // e.g., "Last 30 Days"
        public List<DailyLog> logs;

        public AsthmaReportData(String childName, String currentZone, int adherenceScore, String period, List<DailyLog> logs) {
            this.childName = childName;
            this.currentZone = currentZone;
            this.adherenceScore = adherenceScore;
            this.period = period;
            this.logs = logs;
        }
    }
