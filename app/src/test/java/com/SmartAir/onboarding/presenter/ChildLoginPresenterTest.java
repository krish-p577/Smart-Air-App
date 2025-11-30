package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.BaseUser;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.ChildLoginView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChildLoginPresenterTest {

    @Mock private ChildLoginView mockView;
    @Mock private AuthRepository mockAuthRepository;
    @Mock private CurrentUser mockCurrentUser;
    @Mock private BaseUser mockUser;

    @Captor private ArgumentCaptor<AuthRepository.AuthCallback> authCallbackCaptor;

    @Test
    public void onLoginClicked_withNullUsername_showsError() {
        ChildLoginPresenter presenter = new ChildLoginPresenter(mockView, mockAuthRepository);
        presenter.onLoginClicked(null, "password123");
        verify(mockView).setLoginError("Username and password cannot be empty");
        verify(mockAuthRepository, never()).signInChild(anyString(), anyString(), any());
    }

    @Test
    public void onLoginClicked_withEmptyUsername_showsError() {
        ChildLoginPresenter presenter = new ChildLoginPresenter(mockView, mockAuthRepository);
        presenter.onLoginClicked("", "password123");
        verify(mockView).setLoginError("Username and password cannot be empty");
        verify(mockAuthRepository, never()).signInChild(anyString(), anyString(), any());
    }

    @Test
    public void onLoginClicked_withNullPassword_showsError() {
        ChildLoginPresenter presenter = new ChildLoginPresenter(mockView, mockAuthRepository);
        presenter.onLoginClicked("child_user", null);
        verify(mockView).setLoginError("Username and password cannot be empty");
        verify(mockAuthRepository, never()).signInChild(anyString(), anyString(), any());
    }

    @Test
    public void onLoginClicked_withEmptyPassword_showsError() {
        ChildLoginPresenter presenter = new ChildLoginPresenter(mockView, mockAuthRepository);
        presenter.onLoginClicked("child_user", "");
        verify(mockView).setLoginError("Username and password cannot be empty");
        verify(mockAuthRepository, never()).signInChild(anyString(), anyString(), any());
    }

    @Test
    public void onLoginClicked_withSuccessfulLogin_andOnboardingComplete_navigatesToChildHome() {
        try (MockedStatic<CurrentUser> currentUserStatic = Mockito.mockStatic(CurrentUser.class)) {
            currentUserStatic.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(mockUser);
            when(mockUser.isHasCompletedOnboarding()).thenReturn(true);

            ChildLoginPresenter presenter = new ChildLoginPresenter(mockView, mockAuthRepository);
            presenter.onLoginClicked("child_user", "password123");

            verify(mockAuthRepository).signInChild(eq("child_user"), eq("password123"), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).navigateToChildHome();
        }
    }

    @Test
    public void onLoginClicked_withSuccessfulLogin_andOnboardingIncomplete_navigatesToOnboarding() {
        try (MockedStatic<CurrentUser> currentUserStatic = Mockito.mockStatic(CurrentUser.class)) {
            currentUserStatic.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(mockUser);
            when(mockUser.isHasCompletedOnboarding()).thenReturn(false);

            ChildLoginPresenter presenter = new ChildLoginPresenter(mockView, mockAuthRepository);
            presenter.onLoginClicked("child_user", "password123");

            verify(mockAuthRepository).signInChild(eq("child_user"), eq("password123"), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).navigateToOnboarding();
        }
    }

    @Test
    public void onLoginClicked_withSuccessfulLogin_andNullProfile_navigatesToOnboarding() {
        try (MockedStatic<CurrentUser> currentUserStatic = Mockito.mockStatic(CurrentUser.class)) {
            currentUserStatic.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(null);

            ChildLoginPresenter presenter = new ChildLoginPresenter(mockView, mockAuthRepository);
            presenter.onLoginClicked("child_user", "password123");

            verify(mockAuthRepository).signInChild(eq("child_user"), eq("password123"), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).navigateToOnboarding();
        }
    }

    @Test
    public void onLoginClicked_withFailedLogin_showsError() {
        ChildLoginPresenter presenter = new ChildLoginPresenter(mockView, mockAuthRepository);
        String errorMessage = "Username not found";

        presenter.onLoginClicked("nonexistent_user", "password123");

        verify(mockAuthRepository).signInChild(eq("nonexistent_user"), eq("password123"), authCallbackCaptor.capture());
        authCallbackCaptor.getValue().onFailure(errorMessage);

        verify(mockView).setLoginError(errorMessage);
    }
}
