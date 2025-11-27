package com.SmartAir.onboarding.view;

import com.SmartAir.onboarding.model.ChildUser;
import java.util.List;

public interface SelectChildLoginView {
    void displayChildren(List<ChildUser> children);
    void navigateToChildHome();
    void navigateToOnboarding();
    void displayError(String message);
    void setLoading(boolean isLoading);
    void closeView();
    void promptForChildPassword(ChildUser child);
}
