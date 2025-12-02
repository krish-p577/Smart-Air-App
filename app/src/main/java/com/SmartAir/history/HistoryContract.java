package com.SmartAir.history;

import com.SmartAir.history.presenter.FilterDataModel;
import com.SmartAir.history.presenter.HistoryItem;

import java.util.List;

/**
 * Contract interfaces defining the communication between the View, Presenter, Repository,
 * Adapter, and FilterDialog within the History feature.
 */
public interface HistoryContract {

    interface View {
        void showHistory(List<HistoryItem> items);
        void showLoadError(String message);
        void showLoading();
        void hideLoading();
    }

    interface FilterDialog{
        void show();
    }

    interface Presenter{
        void loadData(FilterDataModel filter);
        List<HistoryItem> getLastQuery();
    }

    interface Repository{
        interface LoadCallback {
            void onSuccess(List<HistoryItem> items);
            void onFailure(Exception e);
        }

        void getData(FilterDataModel filter, LoadCallback callback);
    }

    interface Export{
        void exportHistoryToPdf(List<HistoryItem> items);
    }

    interface Adapter {
        void setItems(List<HistoryItem> newItems);
    }
}
