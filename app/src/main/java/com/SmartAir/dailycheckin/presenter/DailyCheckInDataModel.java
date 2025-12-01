package com.SmartAir.dailycheckin.presenter;
import java.util.List;

/**
 * Data model representing one Daily Check-in entry.
 * It contains all relevant information for the check-in, including:
 * <ul>
 *     <li>The date the entry was submitted.</li>
 *     <li>The author of the entry (child or parent).</li>
 *     <li>The child's name.</li>
 *     <li>The parent’s ID.</li>
 *     <li>Symptom flags (night waking, limited activity, sickness).</li>
 *     <li>A list of selected environmental triggers.</li>
 * </ul>
 */
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
