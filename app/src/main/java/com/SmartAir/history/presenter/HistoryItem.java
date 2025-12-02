package com.SmartAir.history.presenter;

import java.util.List;

/**
 * Data model representing a single Daily Check-In record retrieved from
 * Firebase. This model is used in the HistoryRepository, HistoryPresenter, HistoryActivity,
 * and HistoryAdapter to transfer and display history information.
 */
public class HistoryItem {

    private final String date;
    private final String entryAuthor;
    private final String childName;
    private final Boolean nightWaking;
    private final Boolean limitedAbility;
    private final Boolean sick;

    final List<String> triggers;

    public HistoryItem(String date, String entryAuthor, String childName, Boolean
            nightWaking, Boolean limitedAbility, Boolean sick, List<String> triggers){
        this.date = date;
        this.entryAuthor = entryAuthor;
        this.childName = childName;
        this.nightWaking = nightWaking;
        this.limitedAbility = limitedAbility;
        this.sick = sick;
        this.triggers = triggers;
    }

    public String getDate(){
        return date;
    }

    public String getEntryAuthor(){
        return entryAuthor;
    }

    public String getChildName(){
        return childName;
    }

    public Boolean getNightWaking(){
        return nightWaking;
    }

    public Boolean getLimitedAbility(){
        return limitedAbility;
    }

    public Boolean getSick(){
        return sick;
    }

    public List<String> getTriggers(){
        return triggers;
    }
}
