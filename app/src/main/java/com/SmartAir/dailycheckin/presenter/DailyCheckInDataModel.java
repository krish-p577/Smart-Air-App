package com.SmartAir.dailycheckin.presenter;
import java.util.List;

public class DailyCheckInDataModel {
    private final String date;
    private final String entryAuthor;
    private final String childName;

    private final String parentId;

    private final boolean nightWaking;
    private final boolean limitedAbility;
    private final boolean sick;

    final List<String> triggers;

    public DailyCheckInDataModel(String date, String entryAuthor, String childName, String parentId,
            boolean nightWaking, boolean limitedAbility, boolean sick, List<String> triggers){
        this.date = date;
        this.entryAuthor = entryAuthor;
        this.childName = childName;
        this.parentId = parentId;
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

    public boolean getNightWaking(){
        return nightWaking;
    }

    public boolean getLimitedAbility(){
        return limitedAbility;
    }

    public boolean getSick(){
        return sick;
    }

    public List<String> getTriggers(){
        return triggers;
    }

    public String getParentId(){
        return parentId;
    }
}
