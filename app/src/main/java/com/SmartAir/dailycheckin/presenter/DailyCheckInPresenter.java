package com.SmartAir.dailycheckin.presenter;
import com.SmartAir.dailycheckin.DailyCheckInContract;
import com.SmartAir.dailycheckin.model.DailyCheckInRepository;
import com.SmartAir.dailycheckin.presenter.Date;

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
        String date = Date.getCurrentDate();

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

    @Override
    public void loadChildren(String parentId) {
        repository.loadChildren(parentId, new DailyCheckInContract.Repository.LoadChildrenCallback()
        {
            @Override
            public void onChildrenLoaded(List<String> childNames) {
                view.showSpinnerData(childNames);
            }

            @Override
            public void onError(Exception e) {
                view.showError(e.getMessage());
            }
        });
    }

    @Override
    public void checkIfCanSubmit(String childName){
        repository.checkIfCanSubmit(childName,
                new DailyCheckInContract.Repository.SubmitValidityCallback(){
            @Override
            public void onValidity(Boolean hasEntries){
                if (hasEntries){
                    view.showAlreadySubmitted();
                }
            }

            public void onNotValid(Exception e){
                view.showError("ERROR: child may have already complete Daily-check-in.");
            }
        });
    }

}
