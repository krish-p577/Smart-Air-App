package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.BaseUser;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.ChildLoginView;

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
public class ChildLoginPresenterTest {

    @Mock
    private ChildLoginView mockView;

    @Mock
    private AuthRepository mockAuthRepository;

    @Mock
    private CurrentUser mockCurrentUser;

    @Mock
    private BaseUser mockUser;

    @Captor
    private ArgumentCaptor<AuthRepository.AuthCallback> authCallbackCaptor;

    private ChildLoginPresenter presenter;

    @Before
    public void setUp() {
        presenter = new ChildLoginPresenter(mockView);
        // Mock the AuthRepository singleton
        try (MockedStatic<AuthRepository> mockedAuthRepo = Mockito.mockStatic(AuthRepository.class)) {
            mockedAuthRepo.when(AuthRepository::getInstance).thenReturn(mockAuthRepository);
        }
    }

    @Test
    public void onLoginClicked_withEmptyUsername_showsError() {
        presenter.onLoginClicked("", "password123");
        verify(mockView).setLoginError("Username and password cannot be empty");
    }

    @Test
    public void onLoginClicked_withSuccessfulLogin_andOnboardingComplete_navigatesToChildHome() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = Mockito.mockStatic(CurrentUser.class)) {
            // Arrange
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(mockUser);
            when(mockUser.isHasCompletedOnboarding()).thenReturn(true);

            presenter.onLoginClicked("child_user", "password123");

            // Act
            verify(mockAuthRepository).signInChild(anyString(), anyString(), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            // Assert
            verify(mockView).navigateToChildHome();
        }
    }

    @Test
    public void onLoginClicked_withSuccessfulLogin_andOnboardingIncomplete_navigatesToOnboarding() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = Mockito.mockStatic(CurrentUser.class)) {
            // Arrange
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(mockUser);
            when(mockUser.isHasCompletedOnboarding()).thenReturn(false);

            presenter.onLoginClicked("child_user", "password123");

            // Act
            verify(mockAuthRepository).signInChild(anyString(), anyString(), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            // Assert
            verify(mockView).navigateToOnboarding();
        }
    }

    @Test
    public void onLoginClicked_withFailedLogin_showsError() {
        // Arrange
        String errorMessage = "Username not found";
        presenter.onLoginClicked("nonexistent_user", "password123");

        // Act
        verify(mockAuthRepository).signInChild(anyString(), anyString(), authCallbackCaptor.capture());
        authCallbackCaptor.getValue().onFailure(errorMessage);

        // Assert
        verify(mockView).setLoginError(errorMessage);
    }
}
