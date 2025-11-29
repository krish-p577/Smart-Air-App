package com.SmartAir.dailycheckin.presenter;
import com.SmartAir.dailycheckin.DailyCheckInContract;
import com.SmartAir.dailycheckin.model.DailyCheckInRepository;
import com.SmartAir.onboarding.model.CurrentUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DailyCheckInPresenter implements DailyCheckInContract.Presenter {

    private final DailyCheckInContract.View view;
    private final DailyCheckInContract.Repository repository;

    public DailyCheckInPresenter(DailyCheckInContract.View view){
        this.view = view;
        this.repository = new DailyCheckInRepository();
    }
    @Override
    public void submitDailyCheckIn(String role, String childName, String parentId,
                                   Boolean isNightWalking, Boolean hasLimitedAbility,
                                   Boolean isSick, List<String> triggers)
    {
        String date = getCurrentDate();

        DailyCheckInDataModel data = new DailyCheckInDataModel(date, role, childName, parentId,
                isNightWalking, hasLimitedAbility, isSick, triggers);
        repository.sendDataToDatabase(data, new DailyCheckInContract.Repository.SaveCallback() {
            @Override
            public void onSuccess() {
                view.showSubmitSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                view.showSubmitFailure();
            }
        });
    }

    public String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(now);
    }
}
