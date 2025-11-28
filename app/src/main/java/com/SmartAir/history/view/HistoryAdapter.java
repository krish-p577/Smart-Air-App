package com.SmartAir.history.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.history.HistoryContract;
import com.SmartAir.history.presenter.HistoryItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>
        implements HistoryContract.Adapter {

    private final List<HistoryItem> items = new ArrayList<>();

    @Override
    public void setItems(List<HistoryItem> newItems) {
        items.clear();
        if (newItems != null){
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        holder.textDate.setText(item.getDate());
        holder.textChild.setText(item.getChildName());
        holder.textAuthor.setText(item.getEntryAuthor());
    }

    //
    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;
        TextView textChild;
        TextView textAuthor;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textChild = itemView.findViewById(R.id.textChild);
            textAuthor = itemView.findViewById(R.id.textAuthor);
        }
    }

}
