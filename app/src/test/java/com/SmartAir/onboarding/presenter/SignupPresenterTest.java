package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.SignupView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class SignupPresenterTest {

    @Mock
    private SignupView mockView;

    @Mock
    private AuthRepository mockAuthRepository;

    @Captor
    private ArgumentCaptor<AuthRepository.AuthCallback> authCallbackCaptor;

    @Test
    public void onSignupClicked_withEmptyFields_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView);
        presenter.onSignupClicked("", "password123", "password123", "parent", "Test User");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withEmptyDisplayName_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView);
        presenter.onSignupClicked("test@example.com", "password123", "password123", "parent", "");
        verify(mockView).setSignupError("All fields must be filled");
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void onSignupClicked_withShortPassword_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView);
        presenter.onSignupClicked("test@example.com", "12345", "12345", "parent", "Test User");
        verify(mockView).setSignupError("Password must be at least 6 characters long");
    }

    @Test
    public void onSignupClicked_withMismatchedPasswords_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView);
        presenter.onSignupClicked("test@example.com", "password123", "password456", "parent", "Test User");
        verify(mockView).setSignupError("Passwords do not match");
    }

    @Test
    public void onSignupClicked_withInvalidEmail_showsError() {
        SignupPresenter presenter = new SignupPresenter(mockView);
        presenter.onSignupClicked("invalid-email-format", "password123", "password123", "parent", "Test User");
        verify(mockView).setSignupError("Please enter a valid email address");
    }

    @Test
    public void onSignupClicked_withSuccessfulCreation_navigatesToHome() {
        try (MockedStatic<AuthRepository> mockedAuthRepo = Mockito.mockStatic(AuthRepository.class)) {
            mockedAuthRepo.when(AuthRepository::getInstance).thenReturn(mockAuthRepository);

            SignupPresenter presenter = new SignupPresenter(mockView);
            presenter.onSignupClicked("test@example.com", "password123", "password123", "parent", "Test Parent");

            verify(mockView).setLoading(true);
            verify(mockAuthRepository).createUser(eq("test@example.com"), eq("password123"), eq("parent"), eq("Test Parent"), authCallbackCaptor.capture());

            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).navigateToHome();
        }
    }

    @Test
    public void onSignupClicked_withFailedCreation_showsError() {
        try (MockedStatic<AuthRepository> mockedAuthRepo = Mockito.mockStatic(AuthRepository.class)) {
            mockedAuthRepo.when(AuthRepository::getInstance).thenReturn(mockAuthRepository);

            SignupPresenter presenter = new SignupPresenter(mockView);
            presenter.onSignupClicked("test@example.com", "password123", "password123", "parent", "Test Parent");

            verify(mockAuthRepository).createUser(anyString(), anyString(), anyString(), anyString(), authCallbackCaptor.capture());
            String errorMessage = "Email already in use";

            authCallbackCaptor.getValue().onFailure(errorMessage);

            verify(mockView).setSignupError(errorMessage);
        }
    }

    @Test
    public void onLoginLinkClicked_navigatesToLogin() {
        SignupPresenter presenter = new SignupPresenter(mockView);
        presenter.onLoginLinkClicked();
        verify(mockView).navigateToLogin();
    }
}
