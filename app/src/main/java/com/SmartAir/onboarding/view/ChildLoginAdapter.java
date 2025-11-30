package com.SmartAir.onboarding.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.SmartAir.R;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.presenter.SelectChildLoginPresenter;
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
        holder.childNameButton.setText(child.getDisplayName());
        holder.childNameButton.setOnClickListener(v -> view.promptForChildPassword(child));
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button childNameButton;

        public ViewHolder(View itemView) {
            super(itemView);
            childNameButton = itemView.findViewById(R.id.child_name_button);
        }
    }
}
