package com.SmartAir.history.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

/**
 * Main view for the History Feature of R5. Displays filtered daily check-ins and contains
 * the filter button and export to PDF button to meet requirements. This Activity communicates
 * with HistoryPresenter to load data using the MVP architecture.
 */
public class HistoryActivity extends AppCompatActivity  implements HistoryContract.View{

    private RecyclerView recyclerView;
    private HistoryContract.Presenter presenter;
    private HistoryContract.Adapter adapter;
    private ImageButton exitBtn;
    private Button filterBtn;
    private Button exportPdfBtn;
    private CircularProgressIndicator loading;
    private FilterDialog filterDialog;

    private FilterDataModel filter;

    private HistoryContract.Export pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        loading = findViewById(R.id.historyLoading);
        filterBtn = findViewById(R.id.filterBtn);
        exitBtn = findViewById(R.id.historyBtnClose);
        recyclerView = findViewById(R.id.historyRecycler);
        exportPdfBtn = findViewById(R.id.exportPdfBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter();
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);

        exitBtn.setOnClickListener(v-> finish());

        pdf = new DataToPdfExporter(this);

        exportPdfBtn.setOnClickListener(v->{
            List<HistoryItem> items = presenter.getLastQuery();
            if (items == null || items.isEmpty()){
                Toast.makeText(this, "Query Empty, No data to export.", Toast
                        .LENGTH_SHORT).show();
            }
            else{
                pdf.exportHistoryToPdf(items);
            }
        });

        presenter = new HistoryPresenter(this);

        filterDialog = new FilterDialog(this, presenter);
        filterBtn.setOnClickListener(v-> filterDialog.show());

        filter = new FilterDataModel(null,null,null,
                null, null, new ArrayList<String>());

        presenter.loadData(filter);
    }

    @Override
    public void showHistory(List<HistoryItem> items){
        if (items.isEmpty()) {
            Toast.makeText(this, "No Items Match this Query", Toast.LENGTH_SHORT)
                    .show();
        }
        adapter.setItems(items);
    }

    @Override
    public void showLoadError(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
