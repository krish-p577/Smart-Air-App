package com.SmartAir.dailycheckin;

import com.SmartAir.dailycheckin.presenter.DailyCheckInDataModel;

import java.util.List;

/**
 * Contract interfaces defining the communication between the View, Presenter, and Repository,
 * within the Daily Check-in feature.
 */
public interface DailyCheckInContract {

    interface View {
        void showSubmitSuccess();
        void showSubmitFailure();
        void showSpinnerData(List<String> childrenNames);
        void showError(String message);
        void showAlreadySubmitted();
    }

    interface Presenter {
        void submitDailyCheckIn(String role, String childName, String parentId,
                                                Boolean isNightWalking, Boolean hasLimitedAbility,
                                                Boolean isSick, List<String> triggers);
        void loadChildren(String parentId);

        void checkIfCanSubmit(String childName);
    }

    interface Repository {
        interface SaveCallback {
            void onSuccess();
            void onFailure(Exception e);
        }

        interface LoadChildrenCallback{
            void onChildrenLoaded(List<String> childNames);
            void onError(Exception e);
        }

        interface SubmitValidityCallback{
            void onValidity(Boolean hasEntries);
            void onNotValid(Exception e);
        }

        void checkIfCanSubmit(String childName,SubmitValidityCallback callback);

        void loadChildren(String parentId, LoadChildrenCallback callback);

        void sendDataToDatabase(DailyCheckInDataModel data, SaveCallback callback);
    }
}
