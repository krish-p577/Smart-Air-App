package com.SmartAir.history.view;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.history.HistoryContract;
import com.SmartAir.history.presenter.FilterDataModel;
import com.SmartAir.history.presenter.HistoryItem;
import com.SmartAir.history.presenter.HistoryPresenter;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class HistoryActivity extends AppCompatActivity  implements HistoryContract.View{
    private RecyclerView recyclerView;
    private HistoryContract.Presenter presenter;
    private HistoryContract.Adapter adapter;
    private Button exitBtn;

    private FilterDataModel filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        presenter = new HistoryPresenter(this);
        filter = new FilterDataModel(null,null,null, getDefaultStartDate(),
                getToday(), new ArrayList<String>());


        presenter.loadData(filter);

        // TODO: filter button to change data
        // TODO: export button to get current filter and export


        exitBtn.setOnClickListener(v->finish());
    }

    public String getDefaultStartDate(){
        // 6 Months is default

        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return sixMonthsAgo.toString();
    }

    public String getToday(){
        LocalDate today = LocalDate.now();
        return today.toString();
    }

    @Override
    public void showSubmitSuccess(){

    }
    @Override
    public void showHistory(List<HistoryItem> items){
        adapter.setItems(items);
    }
    @Override
    public void showLoadError(String message){

    }

    @Override
    public void showSubmitFailure(){

    }
}
