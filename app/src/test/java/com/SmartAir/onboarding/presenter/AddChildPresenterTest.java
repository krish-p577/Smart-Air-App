package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.AddChildView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddChildPresenterTest {

    @Mock private AddChildView mockView;
    @Mock private AuthRepository mockAuthRepository;

    @Captor private ArgumentCaptor<AuthRepository.AuthCallback> callbackCaptor;
    @Captor private ArgumentCaptor<List<String>> passedRulesCaptor;
    @Captor private ArgumentCaptor<List<String>> failedRulesCaptor;

    private AddChildPresenter presenter;

    @Before
    public void setUp() {
        presenter = new AddChildPresenter(mockView, mockAuthRepository);
    }

    @Test
    public void onAddChildClicked_nullUsername_showsError() {
        presenter.onAddChildClicked(null, "ValidPass1!", "ValidPass1!");
        verify(mockView).setAddChildError("All fields must be filled");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_emptyUsername_showsError() {
        presenter.onAddChildClicked(" ", "ValidPass1!", "ValidPass1!");
        verify(mockView).setAddChildError("All fields must be filled");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_nullPassword_showsError() {
        presenter.onAddChildClicked("child1", null, "ValidPass1!");
        verify(mockView).setAddChildError("All fields must be filled");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_emptyPassword_showsError() {
        presenter.onAddChildClicked("child1", "", "ValidPass1!");
        verify(mockView).setAddChildError("All fields must be filled");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_nullConfirmPassword_showsError() {
        presenter.onAddChildClicked("child1", "ValidPass1!", null);
        verify(mockView).setAddChildError("All fields must be filled");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_emptyConfirmPassword_showsError() {
        presenter.onAddChildClicked("child1", "ValidPass1!", "");
        verify(mockView).setAddChildError("All fields must be filled");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_passwordsDoNotMatch_showsError() {
        presenter.onAddChildClicked("child1", "ValidPass1!", "DifferentPass1!");
        verify(mockView).setAddChildError("Passwords do not match");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_shortPassword_showsError() {
        presenter.onAddChildClicked("child1", "Vp1!", "Vp1!");
        verify(mockView).setAddChildError("Password must be at least 6 characters long");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_noUpperCase_showsError() {
        presenter.onAddChildClicked("child1", "validpass1!", "validpass1!");
        verify(mockView).setAddChildError("Password must contain at least one uppercase letter");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_noLowerCase_showsError() {
        presenter.onAddChildClicked("child1", "VALIDPASS1!", "VALIDPASS1!");
        verify(mockView).setAddChildError("Password must contain at least one lowercase letter");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_noDigit_showsError() {
        presenter.onAddChildClicked("child1", "ValidPass!", "ValidPass!");
        verify(mockView).setAddChildError("Password must contain at least one digit");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_noSpecialChar_showsError() {
        presenter.onAddChildClicked("child1", "ValidPass1", "ValidPass1");
        verify(mockView).setAddChildError("Password must contain at least one special character (!@#$%^&*())");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_withWhitespace_trimsUsername() {
        presenter.onAddChildClicked("  child1  ", "ValidPass1!", "ValidPass1!");
        verify(mockAuthRepository).createChildUser(eq("child1"), eq("ValidPass1!"), any(AuthRepository.AuthCallback.class));
    }

    @Test
    public void onAddChildClicked_successfulCreation_showsSuccess() {
        presenter.onAddChildClicked("child1", "ValidPass1!", "ValidPass1!");

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).createChildUser(eq("child1"), eq("ValidPass1!"), callbackCaptor.capture());

        callbackCaptor.getValue().onSuccess();

        verify(mockView).setLoading(false);
        verify(mockView).showSuccessMessage("Child account created successfully!");
    }

    @Test
    public void onAddChildClicked_failedCreation_showsError() {
        presenter.onAddChildClicked("child1", "ValidPass1!", "ValidPass1!");

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).createChildUser(eq("child1"), eq("ValidPass1!"), callbackCaptor.capture());

        String errorMessage = "Username taken";
        callbackCaptor.getValue().onFailure(errorMessage);

        verify(mockView).setLoading(false);
        verify(mockView).setAddChildError(errorMessage);
    }

    @Test
    public void validatePasswordRealtime_allRulesFail() {
        presenter.validatePasswordRealtime("");
        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        assertEquals(0, passedRulesCaptor.getValue().size());
        assertEquals(5, failedRulesCaptor.getValue().size());
    }

    @Test
    public void validatePasswordRealtime_onlyFailsLength() {
        presenter.validatePasswordRealtime("Vp1!");
        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        assertEquals(4, passedRulesCaptor.getValue().size());
        assertEquals(1, failedRulesCaptor.getValue().size());
    }

    @Test
    public void validatePasswordRealtime_onlyFailsUppercase() {
        presenter.validatePasswordRealtime("validpass1!");
        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        assertEquals(4, passedRulesCaptor.getValue().size());
        assertEquals(1, failedRulesCaptor.getValue().size());
    }

    @Test
    public void validatePasswordRealtime_onlyFailsLowercase() {
        presenter.validatePasswordRealtime("VALIDPASS1!");
        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        assertEquals(4, passedRulesCaptor.getValue().size());
        assertEquals(1, failedRulesCaptor.getValue().size());
    }

    @Test
    public void validatePasswordRealtime_onlyFailsDigit() {
        presenter.validatePasswordRealtime("ValidPass!");
        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        assertEquals(4, passedRulesCaptor.getValue().size());
        assertEquals(1, failedRulesCaptor.getValue().size());
    }

    @Test
    public void validatePasswordRealtime_onlyFailsSpecialChar() {
        presenter.validatePasswordRealtime("ValidPass1");
        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        assertEquals(4, passedRulesCaptor.getValue().size());
        assertEquals(1, failedRulesCaptor.getValue().size());
    }

    @Test
    public void validatePasswordRealtime_allRulesPass() {
        presenter.validatePasswordRealtime("ValidPass1!");
        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        assertEquals(5, passedRulesCaptor.getValue().size());
        assertEquals(0, failedRulesCaptor.getValue().size());
    }
}
