package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.SignupView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SignupPresenterTest {

    @Mock
    private SignupView mockView;

    @Mock
    private AuthRepository mockAuthRepository;

    @Captor
    private ArgumentCaptor<AuthRepository.AuthCallback> authCallbackCaptor;

    @Captor
    private ArgumentCaptor<List<String>> passedRulesCaptor;

    @Captor
    private ArgumentCaptor<List<String>> failedRulesCaptor;

    @Test
    public void isValidEmail_withNullEmail_returnsFalse() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        assertFalse(presenter.isValidEmail(null));
    }

    @Test
    public void onSignupClicked_withEmptyFields_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("", "password123", "password123", "parent", "Test User");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withNullEmail_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked(null, "password123", "password123", "parent", "Test User");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withEmptyPassword_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "", "password123", "parent", "Test User");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withNullPassword_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", null, "password123", "parent", "Test User");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withEmptyConfirmPassword_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "password123", "", "parent", "Test User");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withNullConfirmPassword_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "password123", null, "parent", "Test User");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withEmptyDisplayName_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "password123", "password123", "parent", "");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withNullDisplayName_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "password123", "password123", "parent", null);
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withWhitespaceDisplayName_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "password123", "password123", "parent", "   ");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withShortPassword_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "12345", "12345", "parent", "Test User");
        verify(mockView).setSignupError("Password must be at least 6 characters long");
    }

    @Test
    public void onSignupClicked_missingUppercase_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "password1!", "password1!", "parent", "Test User");
        verify(mockView).setSignupError("Password must contain at least one uppercase letter");
    }

    @Test
    public void onSignupClicked_missingLowercase_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "PASSWORD1!", "PASSWORD1!", "parent", "Test User");
        verify(mockView).setSignupError("Password must contain at least one lowercase letter");
    }

    @Test
    public void onSignupClicked_missingNumber_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "Password!", "Password!", "parent", "Test User");
        verify(mockView).setSignupError("Password must contain at least one digit");
    }

    @Test
    public void onSignupClicked_missingSymbol_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "Password1", "Password1", "parent", "Test User");
        verify(mockView).setSignupError("Password must contain at least one special character (!@#$%^&*())");
    }

    @Test
    public void onSignupClicked_withMismatchedPasswords_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "Password1!", "Password2!", "parent", "Test User");
        verify(mockView).setSignupError("Passwords do not match");
    }

    @Test
    public void onSignupClicked_withInvalidEmail_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("invalid-email-format", "Password1!", "Password1!", "parent", "Test User");
        verify(mockView).setSignupError("Please enter a valid email address");
    }

    @Test
    public void onSignupClicked_withSuccessfulCreation_navigatesToHome() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "Password1!", "Password1!", "parent", "Test Parent");

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).createUser(eq("test@example.com"), eq("Password1!"), eq("parent"), eq("Test Parent"), authCallbackCaptor.capture());

        // simulate success callback
        authCallbackCaptor.getValue().onSuccess();
        verify(mockView).navigateToHome();
    }

    @Test
    public void onSignupClicked_withFailedCreation_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onSignupClicked("test@example.com", "Password1!", "Password1!", "parent", "Test Parent");

        verify(mockAuthRepository).createUser(anyString(), anyString(), anyString(), anyString(), authCallbackCaptor.capture());
        String errorMessage = "Email already in use";

        // simulate failure callback
        authCallbackCaptor.getValue().onFailure(errorMessage);
        verify(mockView).setSignupError(errorMessage);
    }

    @Test
    public void onLoginLinkClicked_navigatesToLogin() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.onLoginLinkClicked();
        verify(mockView).navigateToLogin();
    }

    @Test
    public void validatePasswordRealtime_withValidPassword_allRulesPass() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.validatePasswordRealtime("ValidPass1!");

        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        List<String> passedRules = passedRulesCaptor.getValue();
        List<String> failedRules = failedRulesCaptor.getValue();

        assertEquals(5, passedRules.size());
        assertTrue(failedRules.isEmpty());
    }

    @Test
    public void validatePasswordRealtime_withInvalidPassword_someRulesFail() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.validatePasswordRealtime("short");

        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        List<String> passedRules = passedRulesCaptor.getValue();
        List<String> failedRules = failedRulesCaptor.getValue();

        assertEquals(1, passedRules.size()); // passes lowercase
        assertEquals(4, failedRules.size());
        assertTrue(failedRules.contains("Password must be at least 6 characters long"));
        assertTrue(failedRules.contains("Password must contain at least one uppercase letter"));
        assertTrue(failedRules.contains("Password must contain at least one digit"));
        assertTrue(failedRules.contains("Password must contain at least one special character (!@#$%^&*())"));
    }

    @Test
    public void validatePasswordRealtime_passesOnlyLength() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.validatePasswordRealtime("longenough");

        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        List<String> passedRules = passedRulesCaptor.getValue();
        List<String> failedRules = failedRulesCaptor.getValue();

        assertEquals(2, passedRules.size());
        assertEquals(3, failedRules.size());
        assertTrue(failedRules.contains("Password must contain at least one uppercase letter"));
        assertTrue(failedRules.contains("Password must contain at least one digit"));
        assertTrue(failedRules.contains("Password must contain at least one special character (!@#$%^&*())"));
    }

    @Test
    public void validatePasswordRealtime_allRulesFail() {
        SignupPresenter presenter = new SignupPresenter(mockView, mockAuthRepository);
        presenter.validatePasswordRealtime("12345");

        verify(mockView).updatePasswordRequirements(passedRulesCaptor.capture(), failedRulesCaptor.capture());
        List<String> passedRules = passedRulesCaptor.getValue();
        List<String> failedRules = failedRulesCaptor.getValue();

        assertEquals(1, passedRules.size()); // passes digit
        assertEquals(4, failedRules.size());
        assertTrue(failedRules.contains("Password must be at least 6 characters long"));
        assertTrue(failedRules.contains("Password must contain at least one uppercase letter"));
        assertTrue(failedRules.contains("Password must contain at least one lowercase letter"));
        assertTrue(failedRules.contains("Password must contain at least one special character (!@#$%^&*())"));
    }
}
