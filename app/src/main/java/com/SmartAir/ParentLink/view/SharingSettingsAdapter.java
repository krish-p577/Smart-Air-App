package com.SmartAir.ParentLink.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.SmartAir.R;
import java.util.List;
import java.util.Map;

public class SharingSettingsAdapter extends RecyclerView.Adapter<SharingSettingsAdapter.ViewHolder> {

    private final List<String> sharingOptions;
    private final Map<String, Boolean> currentSettings;
    private final OnSharingOptionToggleListener listener;

    public SharingSettingsAdapter(List<String> sharingOptions, Map<String, Boolean> currentSettings, OnSharingOptionToggleListener listener) {
        this.sharingOptions = sharingOptions;
        this.currentSettings = currentSettings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sharing_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String option = sharingOptions.get(position);
        holder.optionName.setText(option);

        // Set the initial state of the switch
        boolean isEnabled = currentSettings.containsKey(option) && currentSettings.get(option);
        holder.optionSwitch.setChecked(isEnabled);

        // Set the listener
        holder.optionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) { // Only trigger listener on user interaction
                listener.onSharingOptionToggled(option, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sharingOptions.size();
    }

    public interface OnSharingOptionToggleListener {
        void onSharingOptionToggled(String option, boolean isEnabled);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView optionName;
        Switch optionSwitch;

        ViewHolder(View itemView) {
            super(itemView);
            optionName = itemView.findViewById(R.id.option_name);
            optionSwitch = itemView.findViewById(R.id.option_switch);
        }
    }
}
