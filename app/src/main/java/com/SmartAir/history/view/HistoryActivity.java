package com.SmartAir.history.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.history.HistoryContract;
import com.SmartAir.history.presenter.FilterDataModel;
import com.SmartAir.history.presenter.HistoryItem;
import com.SmartAir.history.presenter.HistoryPresenter;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class HistoryActivity extends AppCompatActivity  implements HistoryContract.View{
    private RecyclerView recyclerView;
    private HistoryContract.Presenter presenter;
    private HistoryContract.Adapter adapter;
    private ImageButton exitBtn;
    private CircularProgressIndicator loading;

    private FilterDataModel filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        exitBtn = findViewById(R.id.historyBtnClose);
        recyclerView = findViewById(R.id.historyRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter();
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);

        loading = findViewById(R.id.historyLoading);

        exitBtn.setOnClickListener(v->finish());

        presenter = new HistoryPresenter(this);
        filter = new FilterDataModel(null,null,null,
                getDefaultStartDate(), getToday(), new ArrayList<String>());

        showLoading();
        presenter.loadData(filter);
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
    public void showHistory(List<HistoryItem> items){
        adapter.setItems(items);
    }
    @Override
    public void showLoadError(String message){
        Toast.makeText(this, "Failed to Fetch Data, Please Try Again Later.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
        loading.show();
    }

    @Override
    public void hideLoading() {
        loading.hide();
        loading.setVisibility(View.GONE);
    }
}
