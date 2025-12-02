package com.SmartAir.ParentDashboard.view;

import java.util.List;

// Data container for specific log entries
    public class DailyLog {
        public String date;
        public List<String> triggers;
        public String note;

        public DailyLog(String date, List<String> triggers, String note) {
            this.date = date;
            this.triggers = triggers;
            this.note = note;
        }
    }
