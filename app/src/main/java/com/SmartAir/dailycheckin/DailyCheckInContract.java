package com.SmartAir.dailycheckin;

import com.SmartAir.dailycheckin.presenter.DailyCheckInDataModel;

import java.util.List;

public interface DailyCheckInContract {

    interface View {
        public abstract void showSubmitSuccess();
        public abstract void showSubmitFailure();
    }

    interface Presenter {
        public abstract void submitDailyCheckIn(String role, String childName, String parentId,
                                                Boolean isNightWalking, Boolean hasLimitedAbility,
                                                Boolean isSick, List<String> triggers);
    }

    interface Repository {
        interface SaveCallback {
            void onSuccess();
            void onFailure(Exception e);
        }

        public abstract void sendDataToDatabase(DailyCheckInDataModel data, SaveCallback callback);
    }
}
