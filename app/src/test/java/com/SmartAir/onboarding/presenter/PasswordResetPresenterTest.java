package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.PasswordResetView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PasswordResetPresenterTest {

    @Mock private PasswordResetView mockView;
    @Mock private AuthRepository mockAuthRepository;

    @Captor private ArgumentCaptor<AuthRepository.AuthCallback> callbackCaptor;

    private PasswordResetPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        presenter = new PasswordResetPresenter(mockView, mockAuthRepository);
    }

    @Test
    public void onSendResetClicked_withNullEmail_showsError() {
        presenter.onSendResetClicked(null);
        verify(mockView).showErrorMessage("Please enter your email");
        verify(mockView, never()).setLoading(true);
        verifyNoInteractions(mockAuthRepository);
    }

    @Test
    public void onSendResetClicked_withEmptyEmail_showsError() {
        presenter.onSendResetClicked("   ");
        verify(mockView).showErrorMessage("Please enter your email");
        verify(mockView, never()).setLoading(true);
        verifyNoInteractions(mockAuthRepository);
    }

    @Test
    public void onSendResetClicked_withValidEmail_callsRepositoryAndShowsSuccess() {
        String email = "test@example.com";

        presenter.onSendResetClicked(email);

        // Verify loading state triggered
        verify(mockView).setLoading(true);

        // Capture the callback passed to repository
        verify(mockAuthRepository).sendPasswordResetEmail(eq(email), callbackCaptor.capture());

        // Simulate success
        callbackCaptor.getValue().onSuccess();

        verify(mockView).setLoading(false);
        verify(mockView).showSuccessMessage("Password reset email sent to " + email);
    }

    @Test
    public void onSendResetClicked_withValidEmail_callsRepositoryAndShowsFailure() {
        String email = "fail@example.com";
        String errorMessage = "Failed to send email";

        presenter.onSendResetClicked(email);

        verify(mockView).setLoading(true);
        verify(mockAuthRepository).sendPasswordResetEmail(eq(email), callbackCaptor.capture());

        // Simulate failure
        callbackCaptor.getValue().onFailure(errorMessage);

        verify(mockView).setLoading(false);
        verify(mockView).showErrorMessage(errorMessage);
    }
}
