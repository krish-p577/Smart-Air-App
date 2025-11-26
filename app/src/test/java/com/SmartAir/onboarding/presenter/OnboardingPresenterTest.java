package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.model.OnboardingStep;
import com.SmartAir.onboarding.view.OnboardingView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingPresenterTest {

    @Mock
    private OnboardingView mockView;

    @Mock
    private AuthRepository mockAuthRepository;

    @Mock
    private CurrentUser mockCurrentUser;

    // No @Before block is needed. The presenter is instantiated inside each test.

    @Test
    public void onViewCreated_withParentRole_displaysParentSpecificSteps() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = Mockito.mockStatic(CurrentUser.class)) {
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getRole()).thenReturn("parent");

            OnboardingPresenter presenter = new OnboardingPresenter(mockView);
            presenter.onViewCreated();

            ArgumentCaptor<List<OnboardingStep>> stepsCaptor = ArgumentCaptor.forClass(List.class);
            verify(mockView).displayOnboardingSteps(stepsCaptor.capture());

            List<OnboardingStep> capturedSteps = stepsCaptor.getValue();
            assertEquals("Parent onboarding should have 4 steps", 4, capturedSteps.size());
            assertTrue("Steps should contain 'Privacy and Sharing' content",
                    capturedSteps.stream().anyMatch(s -> s.getTitle().contains("Privacy and Sharing")));
        }
    }

    @Test
    public void onViewCreated_withProviderRole_displaysProviderSpecificSteps() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = Mockito.mockStatic(CurrentUser.class)) {
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getRole()).thenReturn("provider");

            OnboardingPresenter presenter = new OnboardingPresenter(mockView);
            presenter.onViewCreated();

            ArgumentCaptor<List<OnboardingStep>> stepsCaptor = ArgumentCaptor.forClass(List.class);
            verify(mockView).displayOnboardingSteps(stepsCaptor.capture());

            List<OnboardingStep> capturedSteps = stepsCaptor.getValue();
            assertEquals("Provider onboarding should have 3 steps", 3, capturedSteps.size());
            assertTrue("Steps should contain 'Read-Only Access' content",
                    capturedSteps.stream().anyMatch(s -> s.getTitle().contains("Read-Only Access")));
        }
    }

    @Test
    public void onViewCreated_withChildRole_displaysChildSpecificSteps() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = Mockito.mockStatic(CurrentUser.class)) {
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getRole()).thenReturn("child");

            OnboardingPresenter presenter = new OnboardingPresenter(mockView);
            presenter.onViewCreated();

            ArgumentCaptor<List<OnboardingStep>> stepsCaptor = ArgumentCaptor.forClass(List.class);
            verify(mockView).displayOnboardingSteps(stepsCaptor.capture());

            List<OnboardingStep> capturedSteps = stepsCaptor.getValue();
            assertEquals("Child onboarding should have 3 steps", 3, capturedSteps.size());
            assertTrue("Steps should contain 'Rescue vs. Controller' content",
                    capturedSteps.stream().anyMatch(s -> s.getTitle().contains("Rescue vs. Controller")));
        }
    }

    @Test
    public void onViewCreated_withNullRole_navigatesToWelcomeAndLogsOut() {
        try (MockedStatic<CurrentUser> mockedCurrentUser = Mockito.mockStatic(CurrentUser.class)) {
            mockedCurrentUser.when(CurrentUser::getInstance).thenReturn(mockCurrentUser);
            when(mockCurrentUser.getRole()).thenReturn(null);

            OnboardingPresenter presenter = new OnboardingPresenter(mockView);
            presenter.onViewCreated();

            verify(mockView).navigateToWelcomeAndLogout();
            verify(mockView, never()).displayOnboardingSteps(any());
        }
    }

    @Test
    public void onFinished_marksOnboardingAsCompleteAndNavigatesHome() {
        // This test requires the AuthRepository mock.
        try (MockedStatic<AuthRepository> mockedAuthRepo = Mockito.mockStatic(AuthRepository.class)) {
            mockedAuthRepo.when(AuthRepository::getInstance).thenReturn(mockAuthRepository);

            // Presenter must be created *after* the static mock is set up.
            OnboardingPresenter presenter = new OnboardingPresenter(mockView);
            presenter.onFinished();

            // Assert
            verify(mockAuthRepository).markOnboardingAsCompleted();
            verify(mockView).navigateToHome();
        }
    }
}
