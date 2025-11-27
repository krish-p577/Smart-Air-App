package com.SmartAir.history;

import com.SmartAir.history.presenter.FilterDataModel;
import com.SmartAir.history.presenter.HistoryItem;

import java.util.List;

public interface HistoryContract {
    interface View {
        public abstract void showSubmitSuccess();
        public abstract void showSubmitFailure();
        void showHistory(List<HistoryItem> items);
        void showLoadError(String message);
    }

    interface Presenter{
        public abstract void loadData(FilterDataModel filterDataModel);
    }

    interface Repository{
        interface LoadCallback {
            void onSuccess(List<HistoryItem> items);
            void onFailure(Exception e);
        }
        public abstract void getData(FilterDataModel filter, LoadCallback callback);
    }

    interface Adapter {
        public abstract void setItems(List<HistoryItem> items);
    }
}
