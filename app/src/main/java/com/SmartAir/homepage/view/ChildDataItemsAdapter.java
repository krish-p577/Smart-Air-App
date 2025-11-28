package com.SmartAir.homepage.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.SmartAir.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChildDataItemsAdapter extends RecyclerView.Adapter<ChildDataItemsAdapter.ViewHolder> {

    private final List<Map.Entry<String, Boolean>> dataItems;

    public ChildDataItemsAdapter(Map<String, Boolean> sharingSettings) {
        this.dataItems = new ArrayList<>(sharingSettings.entrySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_data_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, Boolean> item = dataItems.get(position);
        holder.dataPointTextView.setText(item.getKey());
        if (item.getValue()) {
            holder.sharedWithProviderTag.setVisibility(View.VISIBLE);
        } else {
            holder.sharedWithProviderTag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dataPointTextView;
        TextView sharedWithProviderTag;

        ViewHolder(View itemView) {
            super(itemView);
            dataPointTextView = itemView.findViewById(R.id.data_point_text_view);
            sharedWithProviderTag = itemView.findViewById(R.id.shared_with_provider_tag);
        }
    }
}
