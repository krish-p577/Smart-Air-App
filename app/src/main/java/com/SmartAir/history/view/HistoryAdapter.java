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
        holder.textAuthor.setText("Entry Author: " + item.getEntryAuthor());
        holder.textChild.setText("Child: " + item.getChildName());

        holder.textNightWaking.setText(
                "Night waking: " + (Boolean.TRUE.equals(item.getNightWaking()) ? "True" : "False")
        );

        holder.textLimitedAbility.setText(
                "Limited ability: " + (Boolean.TRUE.equals(item.getLimitedAbility()) ? "True" :
                        "False")
        );

        holder.textSick.setText(
                "Cough/Wheeze: " + (Boolean.TRUE.equals(item.getSick()) ? "True" : "False")
        );

        holder.textTriggers.setText(
                "Triggers: " +
                        (item.getTriggers() == null || item.getTriggers().isEmpty()
                                ? "None"
                                : String.join(", ", item.getTriggers()))
        );

    }


    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;
        TextView textAuthor;
        TextView textChild;
        TextView textNightWaking;
        TextView textLimitedAbility;
        TextView textSick;
        TextView textTriggers;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textDate = itemView.findViewById(R.id.textDate);
            textAuthor = itemView.findViewById(R.id.textAuthor);
            textChild = itemView.findViewById(R.id.textChild);

            textNightWaking = itemView.findViewById(R.id.textNightWaking);
            textLimitedAbility = itemView.findViewById(R.id.textLimitedAbility);
            textSick = itemView.findViewById(R.id.textSick);
            textTriggers = itemView.findViewById(R.id.textTriggers);
        }
    }

}
