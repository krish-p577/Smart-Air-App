package com.SmartAir.onboarding.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OnboardingStep implements Parcelable {
    private final String title;
    private final String description;

    public OnboardingStep(String title, String description) {
        this.title = title;
        this.description = description;
    }

    protected OnboardingStep(Parcel in) {
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<OnboardingStep> CREATOR = new Creator<OnboardingStep>() {
        @Override
        public OnboardingStep createFromParcel(Parcel in) {
            return new OnboardingStep(in);
        }

        @Override
        public OnboardingStep[] newArray(int size) {
            return new OnboardingStep[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
    }
}
