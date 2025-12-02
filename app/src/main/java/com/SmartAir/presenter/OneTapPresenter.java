package com.SmartAir.presenter;

import com.SmartAir.view.OneTapView;
import com.SmartAir.view.WelcomeView;

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

    public void helpClicked(){
        view.callEmergency();
    }
}
