package com.SmartAir.onboarding.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.SmartAir.R;
import com.SmartAir.onboarding.model.ChildUser;

import java.util.List;

public class ChildLoginAdapter extends RecyclerView.Adapter<ChildLoginAdapter.ViewHolder> {

    private final List<ChildUser> children;
    private final SelectChildLoginView view;

    public ChildLoginAdapter(List<ChildUser> children, SelectChildLoginView view) {
        this.children = children;
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildUser child = children.get(position);
        holder.childNameTextView.setText(child.getDisplayName());
        holder.itemView.setOnClickListener(v -> view.promptForChildPassword(child));
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView childNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            childNameTextView = itemView.findViewById(R.id.child_name_text_view);
        }
    }
}
