package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.SelectChildLoginView;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectChildLoginPresenterTest {

    @Mock private SelectChildLoginView mockView;
    @Mock private AuthRepository mockAuthRepository;
    @Mock private FirebaseUser mockFirebaseUser;
    @Mock private CurrentUser mockCurrentUser;

    @Captor private ArgumentCaptor<AuthRepository.ChildrenCallback> childrenCallbackCaptor;
    @Captor private ArgumentCaptor<AuthRepository.AuthCallback> authCallbackCaptor;

    private SelectChildLoginPresenter presenter;

    @Before
    public void setUp() {
        presenter = new SelectChildLoginPresenter(mockView, mockAuthRepository);
    }

    @Test
    public void fetchChildren_noParentLoggedIn_showsError() {
        when(mockAuthRepository.getCurrentFirebaseUser()).thenReturn(null);

        presenter.fetchChildren();

        verify(mockView).displayError("Could not verify parent session. Please try again.");
        verify(mockView).closeView();
        verify(mockView, never()).setLoading(true);
    }

    @Test
    public void fetchChildren_successfulFetch_withChildren_displaysChildren() {
        when(mockAuthRepository.getCurrentFirebaseUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn("parent123");

        List<ChildUser> children = new ArrayList<>();
        children.add(new ChildUser());

        presenter.fetchChildren();

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).fetchChildrenForParent(eq("parent123"), childrenCallbackCaptor.capture());
        childrenCallbackCaptor.getValue().onSuccess(children);

        verify(mockView).setLoading(false);
        verify(mockView).displayChildren(children);
    }

    @Test
    public void fetchChildren_successfulFetch_noChildren_showsError() {
        when(mockAuthRepository.getCurrentFirebaseUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn("parent123");

        presenter.fetchChildren();

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).fetchChildrenForParent(eq("parent123"), childrenCallbackCaptor.capture());
        childrenCallbackCaptor.getValue().onSuccess(new ArrayList<>());

        verify(mockView).setLoading(false);
        verify(mockView).displayError("No children found for this account.");
    }

    @Test
    public void fetchChildren_failedFetch_showsError() {
        when(mockAuthRepository.getCurrentFirebaseUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn("parent123");

        presenter.fetchChildren();

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).fetchChildrenForParent(eq("parent123"), childrenCallbackCaptor.capture());
        childrenCallbackCaptor.getValue().onFailure("Database error");

        verify(mockView).setLoading(false);
        verify(mockView).displayError("Database error");
    }

    @Test
    public void onChildSelected_validPassword_navigatesToChildHome() {
        ChildUser child = new ChildUser("child@test.com", "Child Name", "parent123");
        child.setHasCompletedOnboarding(true);
        String password = "password";

        try (MockedStatic<CurrentUser> currentUserStatic = Mockito.mockStatic(CurrentUser.class)) {
            currentUserStatic.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(child);

            presenter.onChildSelected(child, password);

            verify(mockView).setLoading(true);
            verify(mockAuthRepository).signInChild(eq("Child Name"), eq(password), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).setLoading(false);
            verify(mockView).navigateToChildHome();
        }
    }

    @Test
    public void onChildSelected_validPassword_newUser_navigatesToOnboarding() {
        ChildUser child = new ChildUser("child@test.com", "Child Name", "parent123");
        child.setHasCompletedOnboarding(false);
        String password = "password";

        try (MockedStatic<CurrentUser> currentUserStatic = Mockito.mockStatic(CurrentUser.class)) {
            currentUserStatic.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(child);

            presenter.onChildSelected(child, password);

            verify(mockView).setLoading(true);
            verify(mockAuthRepository).signInChild(eq("Child Name"), eq(password), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).setLoading(false);
            verify(mockView).navigateToOnboarding();
            verify(mockView, never()).navigateToChildHome();
        }
    }

    @Test
    public void onChildSelected_loginSuccessButProfileIsNull_navigatesToOnboarding() {
        ChildUser child = new ChildUser("child@test.com", "Child Name", "parent123");
        String password = "password";

        try (MockedStatic<CurrentUser> currentUserStatic = Mockito.mockStatic(CurrentUser.class)) {
            currentUserStatic.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getUserProfile()).thenReturn(null);

            presenter.onChildSelected(child, password);

            verify(mockView).setLoading(true);
            verify(mockAuthRepository).signInChild(eq("Child Name"), eq(password), authCallbackCaptor.capture());
            authCallbackCaptor.getValue().onSuccess();

            verify(mockView).setLoading(false);
            verify(mockView).navigateToOnboarding();
            verify(mockView, never()).navigateToChildHome();
        }
    }

    @Test
    public void onChildSelected_nullPassword_showsError() {
        ChildUser child = new ChildUser();
        presenter.onChildSelected(child, null);
        verify(mockView).displayError("Password cannot be empty.");
        verify(mockAuthRepository, never()).signInChild(any(), any(), any());
    }

    @Test
    public void onChildSelected_emptyPassword_showsError() {
        ChildUser child = new ChildUser();
        presenter.onChildSelected(child, "");
        verify(mockView).displayError("Password cannot be empty.");
        verify(mockAuthRepository, never()).signInChild(any(), any(), any());
    }

    @Test
    public void onChildSelected_loginFailure_showsError() {
        ChildUser child = new ChildUser("child@test.com", "Child Name", "parent123");
        String password = "wrongpassword";
        String errorMessage = "Invalid credentials";

        presenter.onChildSelected(child, password);

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).signInChild(eq("Child Name"), eq(password), authCallbackCaptor.capture());
        authCallbackCaptor.getValue().onFailure(errorMessage);

        verify(mockView).setLoading(false);
        verify(mockView).displayError("Failed to log in as child: " + errorMessage);
    }
}
