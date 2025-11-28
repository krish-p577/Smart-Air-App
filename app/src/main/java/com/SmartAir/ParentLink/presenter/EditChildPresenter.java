package com.SmartAir.ParentLink.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.ParentLink.view.EditChildView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditChildPresenter {

    private EditChildView view;
    private AuthRepository authRepository;

    public EditChildPresenter(EditChildView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void fetchChild(String childId) {
        view.setLoading(true);
        authRepository.fetchChildProfile(childId, new AuthRepository.ChildProfileCallback() {
            @Override
            public void onSuccess(com.SmartAir.onboarding.model.ChildUser child) {
                view.setLoading(false);
                view.setChildName(child.getDisplayName());
                view.setChildAge(child.getAge());
                if (child.getDateOfBirth() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    view.setChildDob(sdf.format(child.getDateOfBirth()));
                } else {
                    view.setChildDob("");
                }
                view.setChildNotes(child.getNotes());
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.setEditChildError(errorMessage);
            }
        });
    }

    public void onSaveClicked(String childId, String newName, String newAge, String newDob, String newNotes) {
        if (newName.isEmpty()) {
            view.setEditChildError("Name cannot be empty");
            return;
        }

        view.setLoading(true);
        authRepository.updateChildDetails(childId, newName, newAge, newDob, newNotes, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                view.setLoading(false);
                view.showSuccessMessage("Child details updated");
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.setEditChildError(errorMessage);
            }
        });
    }
}
