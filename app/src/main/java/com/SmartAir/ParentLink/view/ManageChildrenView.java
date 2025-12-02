package com.SmartAir.ParentLink.view;

import com.SmartAir.onboarding.model.ChildUser;

import java.util.List;

public interface ManageChildrenView {
    void setLoading(boolean isLoading);
    void displayChildren(List<ChildUser> children);
    void displayError(String message);
    void navigateToChildDetail(String childId);
    void navigateToAddChild();
}
