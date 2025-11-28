package com.SmartAir.history.presenter;

import java.time.LocalDate;
import java.util.List;


public class FilterDataModel {
    private Boolean nightWaking;
    private Boolean limitedAbility;
    private Boolean sick;
    private String startDate;
    private String endDate;
    final List<String> triggers;
    private boolean invalidInput;
    public FilterDataModel(Boolean nightWaking, Boolean limitedAbility, Boolean sick,
                           String startDate, String endDate, List<String> triggers){
        this.triggers = triggers;
        this.nightWaking = nightWaking;
        this.limitedAbility = limitedAbility;
        this.sick = sick;

        LocalDate today = LocalDate.now();
        LocalDate maxRangeStart = today.minusMonths(6);

        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : maxRangeStart;
        LocalDate end   = (endDate != null)   ? LocalDate.parse(endDate)   : today;

        if (start.isBefore(maxRangeStart)) {
            start = maxRangeStart;
            invalidInput = true;
        }

        if (end.isBefore(start)) {
            end = start;
            invalidInput = true;
        }

        this.startDate = start.toString();
        this.endDate = end.toString();
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

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    public List<String> getTriggers(){
        return triggers;
    }

    public boolean isInvalidInput(){
        return invalidInput;
    }
}
