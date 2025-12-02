package com.SmartAir.homepage.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.SmartAir.R;
import com.SmartAir.onboarding.model.ChildUser;
import java.util.List;

public class ParentDashboardAdapter extends RecyclerView.Adapter<ParentDashboardAdapter.ViewHolder> {

    private final List<ChildUser> children;

    public ParentDashboardAdapter(List<ChildUser> children) {
        this.children = children;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_dashboard_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildUser child = children.get(position);
        holder.childNameTextView.setText(child.getDisplayName());

        // Set up the nested RecyclerView for the child's data
        ChildDataItemsAdapter adapter = new ChildDataItemsAdapter(child.getSharingSettings());
        holder.childDataRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.childDataRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView childNameTextView;
        RecyclerView childDataRecyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            childNameTextView = itemView.findViewById(R.id.child_name_text_view);
            childDataRecyclerView = itemView.findViewById(R.id.child_data_recycler_view);
        }
    }
}
