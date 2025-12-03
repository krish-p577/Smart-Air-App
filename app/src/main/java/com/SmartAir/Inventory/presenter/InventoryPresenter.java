package com.SmartAir.Inventory.presenter;

import com.SmartAir.Inventory.model.InventoryRepository;
import com.SmartAir.Inventory.view.InventoryView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InventoryPresenter {

    private final InventoryView view;
    private final InventoryRepository repo;

    public InventoryPresenter(InventoryView view, InventoryRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    public void onSubmitButtonClicked() {
        String inhalerType = view.getSelectedInhalerType();
        String remainingAmountText = view.getRemainingAmountText();

        if (inhalerType == null || inhalerType.trim().isEmpty()) {
            view.showMessage("Please select an inhaler type.");
            return;
        }

        if (remainingAmountText == null || remainingAmountText.trim().isEmpty()) {
            view.showMessage("Please enter the remaining amount.");
            return;
        }

        final int remainingAmount;
        try {
            remainingAmount = Integer.parseInt(remainingAmountText.trim());
        } catch (NumberFormatException e) {
            view.showMessage("Please enter a valid number.");
            return;
        }

        if (remainingAmount < 0) {
            view.showMessage("Remaining amount cannot be negative.");
            return;
        }

        repo.getRemainingAmount(inhalerType)
            .addOnSuccessListener(remaining -> {
                if (remaining == null) {
                    view.showMessage("Failed to retrieve current amount remaining.");
                    return;
                }

                if (remainingAmount >= remaining) {
                    view.showMessage("Remaining amount must be less than previous amount.");
                    return;
                }

                repo.getMaxAmount(inhalerType)
                        .addOnSuccessListener(max -> {
                            if (max == null) {
                                view.showMessage("Failed to retrieve maximum amount.");
                                return;
                            }

                            if (remainingAmount > max) {
                                view.showMessage("Remaining amount cannot be greater than maximum amount.");
                                return;
                            }

                            final boolean lowFlag = remainingAmount <= max * 0.2;

                            repo.getExpirationDate(inhalerType)
                                    .addOnSuccessListener(expirationDate -> {
                                        if (expirationDate == null) {
                                            view.showMessage("Expiration date not found.");
                                            return;
                                        }

                                        Date currentDate = new Date();

                                        boolean expiredFlag = currentDate.compareTo(expirationDate) >= 0;

                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("remainingAmount", remainingAmount);
                                        updates.put("lastUpdated", com.google.firebase.Timestamp.now());
                                        updates.put("lastUpdatedBy", "child");
                                        updates.put("lowFlag", lowFlag);
                                        updates.put("expiredFlag", expiredFlag);

                                        repo.updateInventory(inhalerType, updates)
                                                .addOnSuccessListener(unused -> {
                                                    view.showMessage("Inventory updated.");
                                                    view.clearRemainingAmount();
                                                    view.clearInhalerType();
                                                }).addOnFailureListener(e -> view.showMessage("Error: " + e.getMessage()));
                                    }).addOnFailureListener(e -> view.showMessage("Error retrieving expiration date: " + e.getMessage()));
                        }).addOnFailureListener(e -> view.showMessage("Error retrieving maximum amount: " + e.getMessage()));
            }).addOnFailureListener(e -> view.showMessage("Error retrieving remaining amount: " + e.getMessage()));
    }

    public void onBackClicked() {
        view.showChildDashboard();
    }
}
