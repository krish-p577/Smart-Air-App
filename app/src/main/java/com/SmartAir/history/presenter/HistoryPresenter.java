package com.SmartAir.history.presenter;

import com.SmartAir.history.HistoryContract;
import com.SmartAir.history.model.HistoryRepository;

import java.util.List;

/**
 * Presenter for the History feature. Acts as the middle layer between the
 * HistoryActivity (View) and HistoryRepository (data source). Responsible for
 * requesting data from Firestore (Through HistoryRepository), returning the last filter query
 * to HistoryActivity, and returning results back to HistoryActivity for display.
 */
public class HistoryPresenter implements HistoryContract.Presenter {

    private final HistoryContract.View view;
    private final HistoryContract.Repository repository;
    private List<HistoryItem> lastQuery;

    public HistoryPresenter(HistoryContract.View view){
        this.view = view;
        this.repository = new HistoryRepository();
    }

    public void loadData(FilterDataModel filter){
        view.showLoading();
        repository.getData(filter, new HistoryContract.Repository.LoadCallback() {
            @Override
            public void onSuccess(List<HistoryItem> items) {
                view.hideLoading();
                lastQuery = items;
                view.showHistory(items);
            }

            @Override
            public void onFailure(Exception e) {
                view.hideLoading();
                e.printStackTrace();
                view.showLoadError("Failed to Fetch Data, Please Try Again Later.");
            }
        });
    }

    @Override
    public List<HistoryItem> getLastQuery(){
        return lastQuery;
    }
}
