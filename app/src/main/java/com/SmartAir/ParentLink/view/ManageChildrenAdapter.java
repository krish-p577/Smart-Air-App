package com.SmartAir.ParentLink.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.SmartAir.R;
import com.SmartAir.onboarding.model.ChildUser;
import java.util.List;

public class ManageChildrenAdapter extends RecyclerView.Adapter<ManageChildrenAdapter.ViewHolder> {

    private final List<ChildUser> children;
    private final OnChildClickListener listener;

    public ManageChildrenAdapter(List<ChildUser> children, OnChildClickListener listener) {
        this.children = children;
        this.listener = listener;
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
        holder.itemView.setOnClickListener(v -> listener.onChildClicked(child));
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    public void setChildren(List<ChildUser> newChildren) {
        children.clear();
        children.addAll(newChildren);
    }

    public interface OnChildClickListener {
        void onChildClicked(ChildUser child);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView childNameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            childNameTextView = itemView.findViewById(R.id.child_name_text_view);
        }
    }
}
