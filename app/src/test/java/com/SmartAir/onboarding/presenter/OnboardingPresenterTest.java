package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.model.OnboardingStep;
import com.SmartAir.onboarding.view.OnboardingView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingPresenterTest {

    @Mock
    private OnboardingView mockView;

    @Mock
    private AuthRepository mockAuthRepository;

    @Mock
    private CurrentUser mockCurrentUser;

    private OnboardingPresenter presenter;

    @Before
    public void setUp() {
        presenter = new OnboardingPresenter(mockView, mockAuthRepository, mockCurrentUser);
    }

    @Test
    public void onViewCreated_withParentRole_displaysParentSpecificSteps() {
        when(mockCurrentUser.getRole()).thenReturn("parent");

        presenter.onViewCreated();

        ArgumentCaptor<List<OnboardingStep>> stepsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).displayOnboardingSteps(stepsCaptor.capture());

        List<OnboardingStep> capturedSteps = stepsCaptor.getValue();
        assertEquals("Parent onboarding should have the correct number of steps", 4, capturedSteps.size());
        assertTrue("Steps should contain 'Privacy and Sharing' content",
                capturedSteps.stream().anyMatch(s -> s.getTitle().contains("Privacy and Sharing")));
    }

    @Test
    public void onViewCreated_withProviderRole_displaysProviderSpecificSteps() {
        when(mockCurrentUser.getRole()).thenReturn("provider");

        presenter.onViewCreated();

        ArgumentCaptor<List<OnboardingStep>> stepsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).displayOnboardingSteps(stepsCaptor.capture());

        List<OnboardingStep> capturedSteps = stepsCaptor.getValue();
        assertEquals("Provider onboarding should have the correct number of steps", 3, capturedSteps.size());
        assertTrue("Steps should contain 'Read-Only Access' content",
                capturedSteps.stream().anyMatch(s -> s.getTitle().contains("Read-Only Access")));
    }

    @Test
    public void onViewCreated_withChildRole_displaysChildSpecificSteps() {
        when(mockCurrentUser.getRole()).thenReturn("child");

        presenter.onViewCreated();

        ArgumentCaptor<List<OnboardingStep>> stepsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).displayOnboardingSteps(stepsCaptor.capture());

        List<OnboardingStep> capturedSteps = stepsCaptor.getValue();
        assertEquals("Child onboarding should have the correct number of steps", 3, capturedSteps.size());
        assertTrue("Steps should contain 'Rescue vs. Controller' content",
                capturedSteps.stream().anyMatch(s -> s.getTitle().contains("Rescue vs. Controller")));
    }

    @Test
    public void onViewCreated_withNullRole_navigatesToWelcomeAndLogsOut() {
        when(mockCurrentUser.getRole()).thenReturn(null);

        presenter.onViewCreated();

        verify(mockView).navigateToWelcomeAndLogout();
        verify(mockView, never()).displayOnboardingSteps(any());
    }

    @Test
    public void onFinished_marksOnboardingAsCompleteAndNavigatesHome() {
        when(mockCurrentUser.getRole()).thenReturn("parent"); // Example role
        presenter.onViewCreated();
        presenter.onFinished();

        verify(mockAuthRepository).markOnboardingAsCompleted();
        verify(mockView).navigateToHome();
    }
}
