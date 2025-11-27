package com.SmartAir.history.presenter;

import com.SmartAir.history.HistoryContract;
import com.SmartAir.history.model.HistoryRepository;

import java.util.List;

public class HistoryPresenter implements HistoryContract.Presenter {
    private final HistoryContract.View view;
    private final HistoryContract.Repository repository;

    public HistoryPresenter(HistoryContract.View view){
        this.view = view;
        this.repository = new HistoryRepository();
    }

    public void loadData(FilterDataModel filter){
        repository.getData(filter, new HistoryContract.Repository.LoadCallback() {
            @Override
            public void onSuccess(List<HistoryItem> items) {
                view.showHistory(items);
            }

            @Override
            public void onFailure(Exception e) {
                view.showLoadError("Error fetching data");
            }
        });
    }
}
