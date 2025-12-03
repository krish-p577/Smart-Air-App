package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.view.OneTapView;

public class OneTapPresenter {
    private final OneTapView view;
    public OneTapPresenter(OneTapView view) { this.view = view;}
    public void cyanosisClicked(){
        view.incCount();
        view.cyanosisTrue();
        view.callEmergency();
    }
    public void chestClicked(){
        view.incCount();
        view.chestTrue();
        view.callEmergency();
    }
    public void speakClicked(){
        view.incCount();
        view.speakingTrue();
        view.callEmergency();
    }
    public void submitClicked(){
        view.homeSteps();
        view.logForm();
    }
    public void noClicked(){
        view.setNoTrue();
    }

    public void helpClicked(){
        view.callEmergency();
    }
}
