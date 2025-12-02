package com.SmartAir.glossary.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.glossary.model.GlossaryItem;

import java.util.ArrayList;
import java.util.List;

public class GlossaryAdapter extends RecyclerView.Adapter<GlossaryAdapter.ViewHolder> {

    private List<GlossaryItem> items = new ArrayList<>();

    public void setItems(List<GlossaryItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_glossary_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GlossaryItem item = items.get(position);
        holder.termTextView.setText(item.getTerm());
        holder.definitionTextView.setText(item.getDefinition());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView termTextView;
        TextView definitionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            termTextView = itemView.findViewById(R.id.term_text_view);
            definitionTextView = itemView.findViewById(R.id.definition_text_view);
        }
    }
}
