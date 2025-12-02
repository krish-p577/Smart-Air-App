package com.SmartAir.ParentLink.view;

public interface EditChildView {
    void showSuccessMessage(String message);
    void setEditChildError(String message);
    void setLoading(boolean isLoading);
    void setChildName(String name);
    void setChildAge(String age);
    void setChildDob(String dob);
    void setChildNotes(String notes);
}
