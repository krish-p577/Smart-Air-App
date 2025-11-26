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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
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
        presenter = new LoginPresenter(mockView);
        // Mock the AuthRepository singleton
        try (MockedStatic<AuthRepository> mockedAuthRepo = Mockito.mockStatic(AuthRepository.class)) {
            mockedAuthRepo.when(AuthRepository::getInstance).thenReturn(mockAuthRepository);
        }
    }

    @Test
    public void onLoginClicked_withSuccessfulLogin_andOnboardingComplete_navigatesToHome() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = Mockito.mockStatic(CurrentUser.class)) {
            // Arrange
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(mockUser);
            when(mockUser.isHasCompletedOnboarding()).thenReturn(true);

            presenter.onLoginClicked("test@example.com", "password123");

            // Act
            verify(mockAuthRepository).signInUser(anyString(), anyString(), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            // Assert
            verify(mockView).navigateToHome();
        }
    }

    @Test
    public void onLoginClicked_withSuccessfulLogin_andOnboardingIncomplete_navigatesToOnboarding() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = Mockito.mockStatic(CurrentUser.class)) {
            // Arrange
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(mockUser);
            when(mockUser.isHasCompletedOnboarding()).thenReturn(false);

            presenter.onLoginClicked("test@example.com", "password123");

            // Act
            verify(mockAuthRepository).signInUser(anyString(), anyString(), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            // Assert
            verify(mockView).navigateToOnboarding();
        }
    }

    @Test
    public void onLoginClicked_withFailedLogin_showsError() {
        // Arrange
        String errorMessage = "Invalid credentials";
        presenter.onLoginClicked("test@example.com", "wrongpassword");

        // Act
        verify(mockAuthRepository).signInUser(anyString(), anyString(), authCallbackCaptor.capture());
        authCallbackCaptor.getValue().onFailure(errorMessage);

        // Assert
        verify(mockView).setLoginError(errorMessage);
    }

    @Test
    public void onSignupLinkClicked_navigatesToSignup() {
        presenter.onSignupLinkClicked();
        verify(mockView).navigateToSignup();
    }
}
