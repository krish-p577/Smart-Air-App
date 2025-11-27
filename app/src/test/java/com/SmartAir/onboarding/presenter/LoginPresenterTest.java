package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.BaseUser;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.LoginView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    private LoginView mockView;

    @Mock
    private AuthRepository mockAuthRepository;

    @Mock
    private CurrentUser mockCurrentUser;

    @Mock
    private BaseUser mockUser;

    @Captor
    private ArgumentCaptor<AuthRepository.AuthCallback> authCallbackCaptor;

    private LoginPresenter presenter;

    @Before
    public void setUp() {
        presenter = new LoginPresenter(mockView, mockAuthRepository);
    }

    // ====================
    // Tests for login click
    // ====================

    @Test
    public void onLoginClicked_withEmptyEmail_showsError() {
        presenter.onLoginClicked("", "password123");
        verify(mockView).setLoginError("Email and password cannot be empty");
    }

    @Test
    public void onLoginClicked_withEmptyPassword_showsError() {
        presenter.onLoginClicked("test@example.com", "");
        verify(mockView).setLoginError("Email and password cannot be empty");
    }

    @Test
    public void onLoginClicked_withNullEmail_showsError() {
        presenter.onLoginClicked(null, "password123");
        verify(mockView).setLoginError("Email and password cannot be empty");
    }

    @Test
    public void onLoginClicked_withNullPassword_showsError() {
        presenter.onLoginClicked("test@example.com", null);
        verify(mockView).setLoginError("Email and password cannot be empty");
    }

    @Test
    public void onLoginClicked_trimsInputBeforeLogin() {
        presenter.onLoginClicked("  test@example.com  ", "  password123  ");

        verify(mockAuthRepository).signInUser(
                eq("test@example.com"),
                eq("password123"),
                authCallbackCaptor.capture()
        );
    }


    @Test
    public void onLoginClicked_withSuccessfulLogin_andOnboardingComplete_navigatesToHome() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = mockStatic(CurrentUser.class)) {
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(mockUser);
            when(mockUser.isHasCompletedOnboarding()).thenReturn(true);

            presenter.onLoginClicked("test@example.com", "password123");

            verify(mockAuthRepository).signInUser(anyString(), anyString(), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).navigateToHome();
        }
    }

    @Test
    public void onLoginClicked_withSuccessfulLogin_andOnboardingIncomplete_navigatesToOnboarding() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = mockStatic(CurrentUser.class)) {
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(mockUser);
            when(mockUser.isHasCompletedOnboarding()).thenReturn(false);

            presenter.onLoginClicked("test@example.com", "password123");

            verify(mockAuthRepository).signInUser(anyString(), anyString(), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).navigateToOnboarding();
        }
    }

    @Test
    public void onLoginClicked_withFailedLogin_showsError() {
        String errorMessage = "Invalid credentials";

        presenter.onLoginClicked("test@example.com", "wrongpassword");

        verify(mockAuthRepository).signInUser(anyString(), anyString(), authCallbackCaptor.capture());
        authCallbackCaptor.getValue().onFailure(errorMessage);

        verify(mockView).setLoginError(errorMessage);
    }

    // ====================
    // Tests for signup link
    // ====================

    @Test
    public void onSignupLinkClicked_navigatesToSignup() {
        presenter.onSignupLinkClicked();
        verify(mockView).navigateToSignup();
    }
}
