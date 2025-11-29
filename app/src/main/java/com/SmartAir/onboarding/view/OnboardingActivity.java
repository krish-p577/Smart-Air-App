package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.model.OnboardingStep;
import com.SmartAir.onboarding.presenter.OnboardingPresenter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class OnboardingActivity extends AppCompatActivity implements OnboardingView {

    private OnboardingPresenter presenter;
    private ViewPager2 viewPager;
    private Button nextButton;
    private Button skipButton;
    private TabLayout tabLayout;
    private OnboardingAdapter adapter;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Inject dependencies instead of using default constructor
        presenter = new OnboardingPresenter(
                this,
                AuthRepository.getInstance(),
                CurrentUser.getInstance()
        );

        viewPager = findViewById(R.id.view_pager);
        nextButton = findViewById(R.id.next_button);
        skipButton = findViewById(R.id.skip_button);
        tabLayout = findViewById(R.id.dots_indicator);
        loadingIndicator = findViewById(R.id.loading_indicator);

        // Show loading indicator
        loadingIndicator.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        skipButton.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        // Delay the presenter call to allow the UI to draw the loading indicator
        new Handler().postDelayed(() -> presenter.onViewCreated(), 500); // 500ms delay
    }


    @Override
    public void displayOnboardingSteps(List<OnboardingStep> steps) {
        adapter = new OnboardingAdapter(this, steps);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        // Hide loading indicator and show content
        loadingIndicator.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        skipButton.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);

        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                presenter.onFinished();
            }
        });

        skipButton.setOnClickListener(v -> presenter.onFinished());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    nextButton.setText("Done");
                } else {
                    nextButton.setText("Next");
                }
            }
        });
    }

    @Override
    public void navigateToHome() {
        String role = CurrentUser.getInstance().getRole();
        Intent intent;

        if (role == null) {
            navigateToWelcomeAndLogout();
            return;
        }

        switch (role.toLowerCase()) {
            case "parent":
                intent = new Intent(this, ParentHomeActivity.class);
                break;
            case "provider":
                intent = new Intent(this, ProviderHomeActivity.class);
                break;
            case "child":
                intent = new Intent(this, ChildDashboardActivity.class);
                break;
            default:
                intent = new Intent(this, WelcomeActivity.class);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToWelcomeAndLogout() {
        AuthRepository.getInstance().logout();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private static class OnboardingAdapter extends FragmentStateAdapter {
        private final List<OnboardingStep> steps;

        public OnboardingAdapter(FragmentActivity fa, List<OnboardingStep> steps) {
            super(fa);
            this.steps = steps;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return OnboardingStepFragment.newInstance(steps.get(position));
        }

        @Override
        public int getItemCount() {
            return steps.size();
        }
    }

    public static class OnboardingStepFragment extends Fragment {
        private static final String ARG_STEP = "onboarding_step";

        public static OnboardingStepFragment newInstance(OnboardingStep step) {
            OnboardingStepFragment fragment = new OnboardingStepFragment();
            Bundle args = new Bundle();
            args.putParcelable(ARG_STEP, step);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_onboarding_step, container, false);
            TextView title = view.findViewById(R.id.onboarding_title);
            TextView description = view.findViewById(R.id.onboarding_description);

            if (getArguments() != null) {
                OnboardingStep step = getArguments().getParcelable(ARG_STEP);
                if (step != null) {
                    title.setText(step.getTitle());
                    description.setText(step.getDescription());
                }
            }
            return view;
        }
    }
}
