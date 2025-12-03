package com.SmartAir.Inventory.view;

public interface InventoryView {
    void showChildDashboard();

    String getSelectedInhalerType();

    String getRemainingAmountText();

    void clearRemainingAmount();

    void clearInhalerType();

    void showMessage(String message);
}
