package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.AddChildView;

import org.junit.Before;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddChildPresenterTest {

    @Mock
    private AddChildView mockView;

    @Mock
    private AuthRepository mockAuthRepository;

    @Captor
    private ArgumentCaptor<AuthRepository.AuthCallback> authCallbackCaptor;

    private AddChildPresenter presenter;

    @Before
    public void setUp() {
        presenter = new AddChildPresenter(mockView);
        // Mock the AuthRepository singleton
        try (MockedStatic<AuthRepository> mockedAuthRepo = Mockito.mockStatic(AuthRepository.class)) {
            mockedAuthRepo.when(AuthRepository::getInstance).thenReturn(mockAuthRepository);
        }
    }

    @Test
    public void onAddChildClicked_withEmptyUsername_showsError() {
        presenter.onAddChildClicked("", "password123");
        verify(mockView).setAddChildError("Username and password cannot be empty.");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_withShortPassword_showsError() {
        presenter.onAddChildClicked("new_child", "123");
        verify(mockView).setAddChildError("Password must be at least 6 characters.");
        verify(mockAuthRepository, never()).createChildUser(anyString(), anyString(), any());
    }

    @Test
    public void onAddChildClicked_withSuccessfulCreation_showsSuccessMessage() {
        presenter.onAddChildClicked("new_child", "password123");

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).createChildUser(anyString(), anyString(), authCallbackCaptor.capture());

        authCallbackCaptor.getValue().onSuccess();

        verify(mockView).setLoading(false);
        verify(mockView).showSuccessMessage("Child account created successfully!");
    }

    @Test
    public void onAddChildClicked_withFailedCreation_showsErrorMessage() {
        String errorMessage = "Username is already taken.";
        presenter.onAddChildClicked("existing_child", "password123");

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).createChildUser(anyString(), anyString(), authCallbackCaptor.capture());

        authCallbackCaptor.getValue().onFailure(errorMessage);

        verify(mockView).setLoading(false);
        verify(mockView).setAddChildError(errorMessage);
    }
}
