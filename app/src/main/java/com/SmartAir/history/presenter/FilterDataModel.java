package com.SmartAir.history.presenter;

import java.util.List;


public class FilterDataModel {
    private Boolean nightWaking;
    private Boolean limitedAbility;
    private Boolean sick;
    private String startDate;
    private String endDate;
    final List<String> triggers;

    public FilterDataModel(Boolean nightWaking, Boolean limitedAbility, Boolean sick,
                           String startDate, String endDate, List<String> triggers){
        this.triggers = triggers;
        this.nightWaking = nightWaking;
        this.limitedAbility = limitedAbility;
        this.sick = sick;
        this.startDate = startDate;
        this.endDate = endDate;
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
}
