package com.SmartAir.homepage.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.SmartAir.R;
import java.util.List;

public class ProviderSharedDataAdapter extends RecyclerView.Adapter<ProviderSharedDataAdapter.ViewHolder> {

    private final List<String> sharedDataItems;

    public ProviderSharedDataAdapter(List<String> sharedDataItems) {
        this.sharedDataItems = sharedDataItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_provider_shared_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = sharedDataItems.get(position);
        holder.dataItemTextView.setText(item);
    }

    @Override
    public int getItemCount() {
        return sharedDataItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dataItemTextView;

        ViewHolder(View itemView) {
            super(itemView);
            dataItemTextView = itemView.findViewById(R.id.data_item_text_view);
        }
    }
}
