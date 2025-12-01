package com.SmartAir.history.presenter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Data model representing the filter used to query the Daily check-ins recorded in Firebase.
 * This model is used in HistoryActivity, HistoryPresenter, HistoryRepository, and FilterDialog
 * to track, update, and apply the filter created by the user in FilterDialog.
 */
public class FilterDataModel {

    private final Boolean nightWaking;
    private final Boolean limitedAbility;
    private final Boolean sick;
    private final String startDate;
    private final String endDate;
    final List<String> triggers;
    private boolean invalidInput;

    public FilterDataModel(Boolean nightWaking, Boolean limitedAbility, Boolean sick,
                           String startDate, String endDate, List<String> triggers){
        this.triggers = triggers;
        this.nightWaking = nightWaking;
        this.limitedAbility = limitedAbility;
        this.sick = sick;

        LocalDate today = LocalDate.now();
        LocalDate defaultStart = today.minusMonths(6);

        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : defaultStart;
        LocalDate end = (endDate != null)   ? LocalDate.parse(endDate)   : today;

        long monthsBetween = ChronoUnit.MONTHS.between(start, end);

        // if not between 3 and 6 months diff go to default selection which is today to 6 months
        // back.
        if ((monthsBetween < 3|| monthsBetween > 6) || end.isBefore(start)) {
            start = defaultStart;
            end = today;
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
